package net.batkin.med.server.controllers;

import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.http.JsonController;
import net.batkin.med.server.http.RequestContext;
import net.batkin.med.server.http.RunServer;

import org.eclipse.jetty.server.Server;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class ShutdownController extends JsonController {

	private Server server;

	public ShutdownController(Server server) {
		this.server = server;
	}

	@Override
	public JsonObject handle(RequestContext context, JsonObject request) throws ControllerException {
		LoggerFactory.getLogger(RunServer.class).warn("Server is shutting down");

		new Thread() {
			public void run() {
				try {
					Thread.sleep(5000);

					server.stop();
				} catch (Exception e) {
					LoggerFactory.getLogger(RunServer.class).warn("Error in shutdown: " + e.getMessage(), e);
				}
			};
		}.start();

		return null;
	}

}
