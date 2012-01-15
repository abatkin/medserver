package net.batkin.med.server.json.response;

import java.util.List;

import com.google.gson.JsonObject;

public class ConfigListResponse extends ClientResponse {

	private List<String> configNames;

	public ConfigListResponse(List<String> configNames) {
		this.configNames = configNames;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.add("configNames", toJsonList(configNames));
		return obj;
	}

}
