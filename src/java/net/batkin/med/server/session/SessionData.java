package net.batkin.med.server.session;

import java.util.Date;

import net.batkin.med.server.dataModel.Session;

import org.bson.types.ObjectId;

public class SessionData implements Cloneable {

	private ObjectId sessionId;
	private ObjectId userId;
	private Date lastTouched;
	private boolean isDirty;
	private boolean isLoggedOut;

	public SessionData(ObjectId sessionId, ObjectId userId) {
		this.sessionId = sessionId;
		this.userId = userId;
		this.lastTouched = new Date();
	}

	public SessionData(Session session) {
		this.sessionId = session.getSessionId();
		this.userId = session.getUserId();
		this.lastTouched = session.getLastUpdatedAt();
	}

	public void touch() {
		isDirty = true;
		lastTouched = new Date();
	}

	public boolean isValid(Date oldestDate) {
		return lastTouched.after(oldestDate) && !isLoggedOut;
	}

	public ObjectId getSessionId() {
		return sessionId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public Date getLastTouched() {
		return lastTouched;
	}

	public boolean isDirty() {
		isDirty = false;
		return isDirty;
	}

	public void logout() {
		isLoggedOut = true;
	}

	@Override
	public SessionData clone() {
		try {
			return (SessionData) super.clone();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
