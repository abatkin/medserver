package net.batkin.med.server.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public abstract class Controller {

	public static final Gson gson = new Gson();

	public abstract void handle(String[] parts, HttpServletRequest request, HttpServletResponse response) throws IOException;

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
