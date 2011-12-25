package net.batkin.med.server.controllers;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.slf4j.LoggerFactory;

import net.batkin.med.server.ErrorCodes;
import net.batkin.med.server.RestHandler;
import net.batkin.med.server.exception.ClientRequestException;
import net.batkin.med.server.exception.ControllerException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public abstract class JsonController extends Controller {

	public static final Gson gson = new Gson();

	public abstract JsonObject handle(String[] parts, JsonObject request) throws ControllerException;

	@Override
	public void handle(String[] parts, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json");

		JsonObject jsonRequest = readRequest(request);

		JsonObject obj;
		try {
			obj = handle(parts, jsonRequest);
		} catch (ControllerException e) {
			sendError(baseRequest, response, e.getHttpCode(), e.getApplicationCode(), e.getMessage());
			return;
		} catch (Exception e) {
			LoggerFactory.getLogger(RestHandler.class).warn("Error processing controller " + parts[0] + ": " + e.getMessage(), e);
			sendError(baseRequest, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorCodes.ERROR_CODE_UNKNOWN, "Internal Error: " + e.getMessage());
			return;
		}

		if (obj != null) {
			sendResponse(baseRequest, response, obj);
		} else {
			JsonObject message = new JsonObject();
			message.addProperty("success", Boolean.TRUE);
			sendResponse(baseRequest, response, message);
		}
		return;
	}

	public static JsonObject readRequest(HttpServletRequest request) throws IOException {
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

	public static void sendResponse(Request baseRequest, HttpServletResponse response, JsonObject message) throws IOException {
		gson.toJson(message, response.getWriter());
		baseRequest.setHandled(true);
	}

	public static void sendError(Request baseRequest, HttpServletResponse response, int httpCode, int applicationCode, String message) throws IOException {
		JsonObject responseMessage = new JsonObject();
		responseMessage.addProperty("success", Boolean.FALSE);

		JsonObject errorInfo = new JsonObject();
		errorInfo.addProperty("code", Integer.valueOf(applicationCode));
		errorInfo.addProperty("message", message);
		responseMessage.add("error", errorInfo);

		response.setStatus(httpCode);

		sendResponse(baseRequest, response, responseMessage);
	}

	public static String getStringValue(JsonObject obj, String name) throws ControllerException {
		JsonElement el = obj.get(name);
		if (el == null) {
			throw new ClientRequestException("Missing key " + name);
		}
		return el.getAsString();
	}

	public static String getStringValue(JsonObject obj, String name, String defaultValue) throws ControllerException {
		JsonElement el = obj.get(name);
		if (el == null) {
			return defaultValue;
		}
		return el.getAsString();
	}

	public static JsonElement toJsonMapToListOfStrings(Map<String, List<String>> map) {
		JsonObject obj = new JsonObject();

		if (map != null) {
			for (Entry<String, List<String>> entry : map.entrySet()) {
				obj.add(entry.getKey(), toJsonList(entry.getValue()));
			}
		}

		return obj;
	}

	public static JsonElement toJsonMapToString(Map<String, String> map) {
		JsonObject obj = new JsonObject();

		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				obj.addProperty(entry.getKey(), entry.getValue());
			}
		}

		return obj;
	}

	public static JsonElement toJsonList(Collection<String> strings) {
		JsonArray array = new JsonArray();

		if (strings != null) {
			for (String value : strings) {
				array.add(new JsonPrimitive(value));
			}
		}

		return array;
	}
}
