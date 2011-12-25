package net.batkin.med.server.http;

import java.io.IOException;

import net.batkin.med.server.exception.ControllerException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public abstract class Controller {

	public static final Gson gson = new Gson();

	public abstract void handle(RequestContext context) throws ControllerException;

	public static void sendResponse(RequestContext context, JsonObject message) throws IOException {
		gson.toJson(message, context.getResponse().getWriter());
		context.getBaseRequest().setHandled(true);
	}

	public static void sendError(RequestContext context, int httpCode, int applicationCode, String message) throws IOException {
		JsonObject responseMessage = buildGenericResponse(false, message, applicationCode);
		context.getResponse().setStatus(httpCode);
		sendResponse(context, responseMessage);
	}

	public static JsonObject buildGenericResponse(boolean success, String message, int applicationCode) {
		JsonObject response = new JsonObject();
		response.addProperty("success", Boolean.valueOf(success));

		if (message != null) {
			JsonObject info = new JsonObject();
			info.addProperty("code", Integer.valueOf(applicationCode));
			info.addProperty("message", message);
			response.add("info", info);
		}

		return response;
	}
}
