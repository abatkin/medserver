package net.batkin.forms.server.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.batkin.forms.server.controllers.FormsController;
import net.batkin.forms.server.exception.ConfigurationException;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.FileNotFoundException;
import net.batkin.forms.server.http.Controller.ControllerMapping;
import net.batkin.forms.server.http.Controller.RequestMethod;
import net.batkin.forms.server.http.velocity.Renderer;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.LoggerFactory;

public class RestHandler extends AbstractHandler {

	private Map<String, Controller> controllerMap;
	private Renderer renderer;

	public RestHandler(String urlBase) throws ConfigurationException, IOException {
		controllerMap = new HashMap<String, Controller>();
		renderer = new Renderer(urlBase);
		addController(new FormsController());
	}

	private void addController(Controller controller) throws ConfigurationException {
		ControllerMapping mapping = controller.getClass().getAnnotation(ControllerMapping.class);
		if (mapping == null) {
			throw new ConfigurationException("Controller " + controller.getClass().getName() + " is missing @ControllerMapping");
		}
		RequestMethod[] methods = mapping.requestMethods();
		if (methods == null || methods.length == 0) {
			throw new ConfigurationException("Controller " + controller.getClass().getName() + " is missing @ControllerMapping.methods");
		}
		String urlBase = mapping.prefix();
		if (urlBase == null || urlBase.isEmpty()) {
			throw new ConfigurationException("Controller " + controller.getClass().getName() + " is missing @ControllerMapping.prefix");
		}
		if (controllerMap.containsKey(urlBase)) {
			throw new ConfigurationException("Duplicate mapping for @ControllerMapping.prefix: " + urlBase);
		}
		controller.setRequesetMethods(methods);
		controllerMap.put(urlBase, controller);
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		RequestContext context = new RequestContext(renderer, baseRequest, request, response);
		try {
			context.setTarget(target);
			Controller controller = getController(context);
			controller.handle(context);
		} catch (ControllerException e) {
			if (e.shouldLogStackTrace()) {
				LoggerFactory.getLogger(RestHandler.class).warn("Error processing " + target + ": " + e.getMessage(), e);
			} else {
				LoggerFactory.getLogger(RestHandler.class).warn("Error processing " + target + ": " + e.getMessage());
			}
			Controller.sendError(context, e.getHttpCode(), e.getApplicationCode(), e.getMessage(), e);
		} catch (Exception e) {
			LoggerFactory.getLogger(RestHandler.class).warn("Error processing " + target + ": " + e.getMessage(), e);
			Controller.sendError(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorCodes.ERROR_CODE_UNKNOWN, "Internal Error: " + e.getMessage(), e);
		}
	}

	public Controller getController(RequestContext context) throws ControllerException {
		RequestMethod method = context.getRequestMethod();
		Controller controller = controllerMap.get(context.getControllerName());
		if (controller != null && controller.canHandle(method)) {
			return controller;
		}
		throw new FileNotFoundException();
	}
}
