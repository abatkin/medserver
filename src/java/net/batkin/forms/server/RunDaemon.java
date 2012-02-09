package net.batkin.forms.server;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

public class RunDaemon implements Daemon {

	private RunServer runServer;

	@Override
	public void init(DaemonContext context) throws DaemonInitException, Exception {
		runServer = new RunServer();
		runServer.init(context.getArguments());
	}

	@Override
	public void start() throws Exception {
		runServer.start();
	}

	@Override
	public void stop() throws Exception {
		runServer.stop();
	}

	@Override
	public void destroy() {
	}

}
