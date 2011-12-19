package net.batkin.med.server;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.batkin.med.server.controllers.Controller;
import net.batkin.med.server.controllers.StatusController;
import net.batkin.med.server.exception.ControllerException;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RestHandler extends AbstractHandler {

	public static final Gson gson = new Gson();

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
			public JsonObject handle(String[] parts, JsonObject request) throws ControllerException {
				LoggerFactory.getLogger(RunServer.class).warn("Server is shutting down");

				JsonObject message = new JsonObject();
				message.addProperty("success", Boolean.TRUE);

				new Thread() {
					public void run() {
						try {
							Thread.sleep(5000);

							getServer().stop();
						} catch (Exception e) {
							LoggerFactory.getLogger(RunServer.class).warn("Error in shutdown: " + e.getMessage(), e);
						}
					};
				}.start();

				return message;
			}
		});
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json");

		Map<String, Controller> controllers = controllerMap.get(baseRequest.getMethod());
		if (controllers != null) {
			String[] parts = target.substring(1).split("/");
			if (parts.length > 0) {
				String controllerName = parts[0];
				Controller controller = controllers.get(controllerName);
				if (controller != null) {
					JsonObject jsonRequest = readRequest(request);

					JsonObject obj;
					try {
						obj = controller.handle(parts, jsonRequest);
					} catch (ControllerException e) {
						sendError(response, e.getHttpCode(), e.getApplicationCode(), e.getMessage());
						return;
					} catch (Exception e) {
						LoggerFactory.getLogger(RestHandler.class).warn("Error processing controller " + controllerName + ": " + e.getMessage(), e);
						sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorCodes.ERROR_CODE_UNKNOWN, "Internal Error: " + e.getMessage());
						return;
					}

					if (obj != null) {
						sendResponse(response, obj);
					}
					baseRequest.setHandled(true);
					return;
				}
			}
		}

		sendError(response, HttpServletResponse.SC_NOT_FOUND, ErrorCodes.ERROR_CODE_NOT_FOUND, "Invalid controller");
		baseRequest.setHandled(true);
	}

	private JsonObject readRequest(HttpServletRequest request) throws IOException {
		Reader reader = request.getReader();

		if (reader != null) {
			JsonParser parser = new JsonParser();
			JsonElement el = parser.parse(reader);
			if (el != null && el.isJsonObject()) {
				return el.getAsJsonObject();
			}
		}
		return null;
	}

	public static void sendResponse(HttpServletResponse response, JsonObject message) throws IOException {
		gson.toJson(message, response.getWriter());
	}

	public static void sendError(HttpServletResponse response, int httpCode, int applicationCode, String message) throws IOException {
		JsonObject responseMessage = new JsonObject();
		responseMessage.addProperty("success", Boolean.FALSE);

		JsonObject errorInfo = new JsonObject();
		errorInfo.addProperty("code", Integer.valueOf(applicationCode));
		errorInfo.addProperty("message", message);
		responseMessage.add("error", errorInfo);

		response.setStatus(httpCode);

		sendResponse(response, responseMessage);
	}
}
