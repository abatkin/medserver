package net.batkin.med.server.controllers;

import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.http.JsonController;
import net.batkin.med.server.http.RequestContext;
import net.batkin.med.server.http.RestHandler;
import net.batkin.med.server.http.Controller.ControllerMapping;
import net.batkin.med.server.http.Controller.RequestMethod;
import net.batkin.med.server.session.SessionManager;

import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

@ControllerMapping(prefix = "shutdown", requestMethods = { RequestMethod.POST })
public class ShutdownController extends JsonController {

	private RestHandler server;

	public ShutdownController(RestHandler restHandler) {
		this.server = restHandler;
	}

	@Override
	public JsonObject handle(RequestContext context, JsonObject request) throws ControllerException {
		LoggerFactory.getLogger(ShutdownController.class).warn("Server is shutting down");

		new Thread() {
			public void run() {
				try {
					Thread.sleep(5000);
					server.getServer().stop();
					SessionManager.getInstance().stop();

					LoggerFactory.getLogger(ShutdownController.class).warn("Shutdown thread is finished");
				} catch (Exception e) {
					LoggerFactory.getLogger(ShutdownController.class).warn("Error in shutdown: " + e.getMessage(), e);
				}
			};
		}.start();

		return null;
	}

}
