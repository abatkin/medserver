package net.batkin.med.server.http;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ResponseUtility {

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
