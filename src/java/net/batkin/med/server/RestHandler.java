package net.batkin.med.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.batkin.med.server.controllers.Controller;
import net.batkin.med.server.controllers.LoginController;
import net.batkin.med.server.controllers.RequestContext;
import net.batkin.med.server.controllers.ShutdownController;
import net.batkin.med.server.controllers.StatusController;
import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.exception.FileNotFoundException;

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

		posts.put("login", new LoginController());
		posts.put("shutdown", new ShutdownController(getServer()));
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		RequestContext context = new RequestContext(baseRequest, request, response);
		try {
			context.setTarget(target);
			Controller controller = getController(context);
			controller.handle(context);
		} catch (ControllerException e) {
			LoggerFactory.getLogger(RestHandler.class).warn("Error processing " + target + ": " + e.getMessage(), e);
			Controller.sendError(context, e.getHttpCode(), e.getApplicationCode(), e.getMessage());
		} catch (Exception e) {
			LoggerFactory.getLogger(RestHandler.class).warn("Error processing " + target + ": " + e.getMessage(), e);
			Controller.sendError(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorCodes.ERROR_CODE_UNKNOWN, "Internal Error: " + e.getMessage());
		}
	}

	public Controller getController(RequestContext context) throws ControllerException {
		String httpMethod = context.getBaseRequest().getMethod();
		Map<String, Controller> controllers = controllerMap.get(httpMethod);
		if (controllers == null) {
			throw new FileNotFoundException();
		}

		Controller controller = controllers.get(context.getControllerName());
		if (controller == null) {
			throw new FileNotFoundException();
		}
		return controller;
	}

}
