package net.batkin.med.server.session;

import net.batkin.med.server.configuration.Configuration;
import net.batkin.med.server.configuration.ConfigurationOption;
import net.batkin.med.server.db.dataModel.User;
import net.batkin.med.server.exception.ServerDataException;

import org.bson.types.ObjectId;

public class SessionManager {

	private static SessionManager instance = new SessionManager();

	public static SessionManager getInstance() {
		return instance;
	}

	private Thread thread;
	private SessionManagerThread sessionManagerThread;

	public void start() {
		Configuration config = Configuration.getInstance();
		int sleepTime = config.getIntegerValue(ConfigurationOption.CONFIG_SESSION_SLEEP_TIME, 60 * 1000);
		int validityTime = config.getIntegerValue(ConfigurationOption.CONFIG_SESSION_VALIDITY_TIME, 60 * 60 * 8 * 1000);

		this.sessionManagerThread = new SessionManagerThread(sleepTime, validityTime);
		this.thread = new Thread(sessionManagerThread, "SessionManagerThread");
		thread.start();
	}

	public void stop() {
		sessionManagerThread.shutdown();
		thread.interrupt();
	}

	public String createSession(User user) throws ServerDataException {
		ObjectId userId = user.getObjectId();
		return sessionManagerThread.createSession(userId).getSessionId().toStringMongod();
	}

	public void invalidateSession(String sessionId) {
		sessionManagerThread.invalidateSession(new ObjectId(sessionId));
	}

	public User validateSession(String sessionId) throws ServerDataException {
		return sessionManagerThread.validateSession(new ObjectId(sessionId));
	}
}
