package net.batkin.med.server.session;

import java.util.Date;

import net.batkin.med.server.db.dataModel.Session;

import org.bson.types.ObjectId;

public class SessionData implements Cloneable {

	private Session session;
	private boolean isDirty;
	private boolean isLoggedOut;

	public SessionData(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	public ObjectId getSessionId() {
		return session.getSessionId();
	}

	public ObjectId getUserId() {
		return session.getUserId();
	}

	public Date getLastUpdatedAt() {
		return session.getLastUpdatedAt();
	}

	public void touch() {
		isDirty = true;
		session.setLastUpdatedAt(new Date());
	}

	public boolean isValid(Date oldestDate) {
		return session.getLastUpdatedAt().after(oldestDate) && !isLoggedOut;
	}

	public boolean isDirty() {
		isDirty = false;
		return isDirty;
	}

	public void logout() {
		isLoggedOut = true;
	}

}
