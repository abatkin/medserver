package net.batkin.med.server.json.response;

import net.batkin.med.server.db.dataModel.Config;

import com.google.gson.JsonObject;

public class ConfigDataResponse extends ClientResponse {

	private Config config;

	public ConfigDataResponse(Config config) {
		this.config = config;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("configName", config.getName());
		obj.add("values", toJsonMapToListOfStrings(config, "name", "values"));
		return obj;
	}

}
