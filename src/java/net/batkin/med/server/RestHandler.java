package net.batkin.med.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.batkin.med.server.controllers.Controller;
import net.batkin.med.server.controllers.StatusController;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.LoggerFactory;

public class RestHandler extends AbstractHandler {

	private Map<String, Map<String, Controller>> controllerMap;

	public RestHandler() {
		controllerMap = new HashMap<String, Map<String, Controller>>();
		Map<String, Controller> gets = new HashMap<String, Controller>();
		Map<String, Controller> posts = new HashMap<String, Controller>();
		Map<String, Controller> puts = new HashMap<String, Controller>();
		controllerMap.put("GET", gets);
		controllerMap.put("POST", posts);
		controllerMap.put("PUT", puts);

		gets.put("status", new StatusController());
		gets.put("shutdown", new Controller() {
			@Override
			public void handle(String[] parts, HttpServletRequest request, HttpServletResponse response) {
				LoggerFactory.getLogger(RunServer.class).warn("Server is shutting down");
				try {
					new Thread() {
						public void run() {
							try {
								Thread.sleep(5000);
								
								getServer().stop();
							} catch (Exception e) {
								// Ignore
							}
						};
					}.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Map<String, Controller> controllers = controllerMap.get(baseRequest.getMethod());
		if (controllers != null) {
			String[] parts = target.substring(1).split("/");
			if (parts.length > 0) {
				String controllerName = parts[0];
				Controller controller = controllers.get(controllerName);
				if (controller != null) {
					controller.handle(parts, request, response);
					baseRequest.setHandled(true);
					return;
				}
			}
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
	}

}
