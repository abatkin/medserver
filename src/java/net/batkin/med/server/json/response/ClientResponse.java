package net.batkin.med.server.json.response;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.batkin.med.server.http.JsonListCreator;
import net.batkin.med.server.http.JsonListCreator.JsonValueHandler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ClientResponse {

	public static JsonArray toJsonMapToListOfStrings(Map<String, List<String>> map, String keyName, final String valuesName) {
		return JsonListCreator.createList(map, keyName, new JsonValueHandler<List<String>>() {
			@Override
			public void populateJsonObject(JsonObject jsonObject, List<String> valueObject) {
				jsonObject.add(valuesName, toJsonList(valueObject));
			}
		});
	}

	public static JsonArray toJsonMapToString(Map<String, String> map, String keyName, final String valueName) {
		return JsonListCreator.createList(map, keyName, new JsonValueHandler<String>() {
			@Override
			public void populateJsonObject(JsonObject jsonObject, String valueObject) {
				jsonObject.add(valueName, new JsonPrimitive(valueObject));
			}
		});
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
