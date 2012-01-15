package net.batkin.med.server.http;

import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonListCreator {
	public static <T> JsonArray createList(Map<String, T> map, String keyName, JsonValueHandler<T> valueHandler) {
		JsonArray list = new JsonArray();

		if (map != null && !map.isEmpty()) {
			for (Entry<String, T> entry : map.entrySet()) {
				JsonObject obj = new JsonObject();
				obj.addProperty(keyName, entry.getKey());
				valueHandler.populateJsonObject(obj, entry.getValue());
				list.add(obj);
			}
		}

		return list;
	}

	public interface JsonValueHandler<T> {
		void populateJsonObject(JsonObject jsonObject, T valueObject);
	}
}
