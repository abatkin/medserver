package net.batkin.med.server.controllers;

import net.batkin.med.server.exception.ClientRequestException;
import net.batkin.med.server.exception.ControllerException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RequestUtility {

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
}
