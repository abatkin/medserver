package net.batkin.forms.server.session;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.batkin.forms.server.db.dataModel.Session;
import net.batkin.forms.server.db.dataModel.User;
import net.batkin.forms.server.db.dataModel.Session.SessionCreator;
import net.batkin.forms.server.db.utility.DBAccess.DatabaseCollection;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
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
					dirtys.add(data);
				}
			}

			for (SessionData data : expireds) {
				sessionMap.remove(data.getSessionId());
			}
		}

		for (SessionData data : expireds) {
			removeDbSession(data);
		}
		for (SessionData data : dirtys) {
			touchDbSession(data);
		}
		removeOldDbSessions();
	}

	private void removeOldDbSessions() {
		BasicDBObject query = new BasicDBObject("lastUpdatedAt", new BasicDBObject("$lt", getOldestDate()));
		WriteResult result = DatabaseCollection.Sessions.removeByQuery(query);
		if (result != null) {
			LoggerFactory.getLogger(SessionManagerThread.class).info("Removed " + result.getN() + " stale (uncached) sessions");
		}
	}

	private void removeDbSession(SessionData data) {
		ObjectId sessionId = data.getSessionId();
		LoggerFactory.getLogger(SessionManagerThread.class).info("Destroying session [" + sessionId + "]");
		DatabaseCollection.Sessions.deleteObject(data.getSession());
	}

	private void touchDbSession(SessionData data) {
		ObjectId sessionId = data.getSessionId();
		LoggerFactory.getLogger(SessionManagerThread.class).info("Touching session [" + sessionId + "]");
		Session dbSession = null;
		try {
			dbSession = Session.findBySessionId(sessionId);
		} catch (ServerDataException e) {
			// Ignore
		}

		if (dbSession == null) {
			synchronized (this) {
				sessionMap.remove(sessionId);
			}
			return;
		}

		Date oldLastUpdated = dbSession.getLastUpdatedAt();
		Date newLastUpdated = data.getLastUpdatedAt();
		if (oldLastUpdated.after(newLastUpdated)) {
			return;
		}

		DatabaseCollection.Sessions.saveObject(data.getSession());
	}

	public SessionData createSession(ObjectId userId) throws ServerDataException {
		SessionCreator creator = Session.createSession(userId);
		DBObject sessionObject = creator.toDBObject();
		DatabaseCollection.Sessions.saveDbObject(sessionObject);
		Session session = creator.getSession(sessionObject);
		SessionData sessionData = new SessionData(session);
		synchronized (this) {
			sessionMap.put(sessionData.getSessionId(), sessionData);
		}
		return sessionData;
	}

	private SessionData getSessionData(ObjectId sessionId) {
		SessionData data = null;
		synchronized (this) {
			data = sessionMap.get(sessionId);
		}

		if (data == null) {
			try {
				Session session = Session.findBySessionId(sessionId);

				SessionData sessionData = new SessionData(session);
				synchronized (this) {
					sessionMap.put(sessionData.getSessionId(), sessionData);
				}
				return sessionData;
			} catch (ServerDataException e) {
				// Ignore
			}
		}

		return data;

	}

	public User validateSession(ObjectId sessionId) throws ServerDataException {
		SessionData data = getSessionData(sessionId);

		if (data != null && data.isValid(getOldestDate())) {
			data.touch();
			ObjectId userId = data.getUserId();
			return User.loadUserById(userId);
		}
		return null;
	}

	public void invalidateSession(ObjectId sessionId) {
		SessionData data = getSessionData(sessionId);

		synchronized (this) {
			if (data == null || !data.isValid(getOldestDate())) {
				return;
			}
			
			data.logout();
		}
	}

	public void shutdown() {
		running = false;
	}

}
