package net.batkin.med.server.session;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.batkin.med.server.dataModel.Session;
import net.batkin.med.server.dataModel.Session.SessionCreator;
import net.batkin.med.server.dataModel.User;
import net.batkin.med.server.db.DBAccess;
import net.batkin.med.server.db.DBAccess.DatabaseCollection;
import net.batkin.med.server.db.DBUserUtility;
import net.batkin.med.server.exception.ServerDataException;

import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class SessionManagerThread implements Runnable {

	private Map<ObjectId, SessionData> sessionMap;
	private int sleepTime;
	private int validityTime;
	private boolean running;

	public SessionManagerThread(int sleepTime, int validityTime) {
		this.sessionMap = new HashMap<ObjectId, SessionData>();
		this.sleepTime = sleepTime;
		this.validityTime = validityTime;
	}

	@Override
	public void run() {
		running = true;

		while (running) {
			try {
				Thread.sleep(sleepTime);
				LoggerFactory.getLogger(SessionManagerThread.class).info("Checking for sessions to persist");
				persistChanges();
			} catch (InterruptedException e) {
				LoggerFactory.getLogger(SessionManagerThread.class).warn("Session manager was interrupted, shutting down");
				running = false;
			} catch (Exception e) {
				LoggerFactory.getLogger(SessionManagerThread.class).warn("Caught exception processing sessions: " + e.getMessage(), e);
			}
		}

		persistChanges();
		LoggerFactory.getLogger(SessionManagerThread.class).warn("Session Manager Shut Down");
	}

	private Date getOldestDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, -validityTime);
		return cal.getTime();
	}

	private void persistChanges() {
		Set<SessionData> expireds = new HashSet<SessionData>();
		Set<SessionData> dirtys = new HashSet<SessionData>();

		synchronized (this) {
			for (SessionData data : sessionMap.values()) {
				if (!data.isValid(getOldestDate())) {
					expireds.add(data);
				} else if (data.isDirty()) {
					dirtys.add(data.clone());
				}
			}

			for (SessionData data : expireds) {
				sessionMap.remove(data.getSessionId());
			}
		}

		DBCollection collection = DBAccess.getCollection(DatabaseCollection.Sessions);
		for (SessionData data : expireds) {
			removeDbSession(collection, data);
		}
		for (SessionData data : dirtys) {
			touchDbSession(collection, data);
		}
		removeOldDbSessions(collection);
	}

	private void removeOldDbSessions(DBCollection collection) {
		BasicDBObject query = new BasicDBObject("lastUpdatedAt", new BasicDBObject("$lt", getOldestDate()));
		WriteResult result = collection.remove(query);
		if (result != null) {
			LoggerFactory.getLogger(SessionManagerThread.class).info("Removed " + result.getN() + " stale (uncached) sessions");
		}
	}

	private void removeDbSession(DBCollection collection, SessionData data) {
		ObjectId sessionId = data.getSessionId();
		LoggerFactory.getLogger(SessionManagerThread.class).info("Destroying session [" + sessionId + "]");
		collection.remove(new BasicDBObject("_id", sessionId));
	}

	private DBObject loadDbSession(DBCollection collection, ObjectId sessionId) {
		DBObject session = collection.findOne(new BasicDBObject("_id", sessionId));
		return session;
	}

	private void touchDbSession(DBCollection collection, SessionData data) {
		ObjectId sessionId = data.getSessionId();
		LoggerFactory.getLogger(SessionManagerThread.class).info("Touching session [" + sessionId + "]");
		DBObject session = loadDbSession(collection, sessionId);
		if (session == null) {
			synchronized (this) {
				sessionMap.remove(sessionId);
			}
			return;
		}

		Object lastUpdatedObj = session.get("lastUpdatedAt");
		if (lastUpdatedObj == null || !(lastUpdatedObj instanceof Date)) {
			synchronized (this) {
				sessionMap.remove(sessionId);
			}
			return;
		}

		Date oldLastUpdated = (Date) lastUpdatedObj;
		Date newLastUpdated = data.getLastTouched();
		if (oldLastUpdated.after(newLastUpdated)) {
			return;
		}

		session.put("lastUpdatedAt", newLastUpdated);
		collection.save(session);
	}

	private SessionData loadSession(ObjectId sessionId) {
		DBObject dbSession = loadDbSession(DBAccess.getCollection(DatabaseCollection.Sessions), sessionId);
		if (dbSession == null) {
			return null;
		}

		try {
			Session session = new Session(dbSession);

			SessionData sessionData = new SessionData(session);
			synchronized (this) {
				sessionMap.put(sessionData.getSessionId(), sessionData);
			}
			return sessionData;
		} catch (ServerDataException e) {
			return null;
		}
	}

	public SessionData createSession(ObjectId userId) throws ServerDataException {
		SessionCreator creator = Session.createSession(userId);
		DBObject sessionObject = creator.toDBObject();
		DBAccess.getCollection(DatabaseCollection.Sessions).save(sessionObject);
		Session session = creator.getSession(sessionObject);
		SessionData sessionData = new SessionData(session);
		synchronized (this) {
			sessionMap.put(sessionData.getSessionId(), sessionData);
		}
		return sessionData;
	}

	public User validateSession(ObjectId sessionId) throws ServerDataException {
		SessionData data = null;
		synchronized (this) {
			data = sessionMap.get(sessionId);
		}

		if (data == null) {
			data = loadSession(sessionId);
		}

		synchronized (this) {
			if (data != null && data.isValid(getOldestDate())) {
				data.touch();
				ObjectId userId = data.getUserId();
				return DBUserUtility.loadUserById(userId);
			}
		}
		return null;
	}

	public void invalidateSession(ObjectId sessionId) {
		SessionData data = null;
		synchronized (this) {
			data = sessionMap.get(sessionId);
		}

		if (data == null) {
			data = loadSession(sessionId);
		}

		synchronized (this) {
			if (data == null || !data.isValid(getOldestDate())) {
				return;
			}
		}
	}

	public void shutdown() {
		running = false;
	}

}
