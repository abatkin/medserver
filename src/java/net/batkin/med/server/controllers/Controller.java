package net.batkin.med.server.controllers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.batkin.med.server.exception.ClientRequestException;
import net.batkin.med.server.exception.ControllerException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class Controller {

	public abstract JsonObject handle(String[] parts, JsonObject request) throws ControllerException;

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
