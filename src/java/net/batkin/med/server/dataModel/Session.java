package net.batkin.med.server.dataModel;

import java.util.Date;

import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Session extends DataModel {
	private ObjectId sessionId;
	private ObjectId userId;
	private Date createdAt;
	private Date lastUpdatedAt;

	public Session(BSONObject obj) throws ServerDataException {
		sessionId = getObjectIdValue(obj, "_id");
		userId = getObjectIdValue(obj, "userId");
		createdAt = getDateValue(obj, "createdAt");
		lastUpdatedAt = getDateValue(obj, "lastUpdatedAt");
	}

	public ObjectId getSessionId() {
		return sessionId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public static SessionCreator createSession(ObjectId userId) {
		return new SessionCreator(userId);
	}

	public static class SessionCreator {
		private ObjectId userId;

		private SessionCreator(ObjectId userId) {
			this.userId = userId;
		}

		public DBObject toDBObject() {
			BasicDBObject obj = new BasicDBObject();
			Date now = new Date();
			obj.put("userId", userId);
			obj.put("createdAt", now);
			obj.put("lastUpdatedAt", now);
			return obj;
		}

		public Session getSession(DBObject response) throws ServerDataException {
			return new Session(response);
		}
	}
}
