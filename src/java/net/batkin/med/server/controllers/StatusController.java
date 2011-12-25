package net.batkin.med.server.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.batkin.med.server.configuration.Configuration;
import net.batkin.med.server.configuration.Configuration.ConfigurationSource;
import net.batkin.med.server.exception.ControllerException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class StatusController extends JsonController {

	@Override
	public JsonObject handle(RequestContext context, JsonObject request) throws ControllerException {
		JsonObject obj = new JsonObject();

		JsonArray settingsObj = new JsonArray();
		Configuration config = Configuration.getInstance();
		List<String> keys = new ArrayList<String>(config.getKeys());

		Collections.sort(keys);
		for (String key : keys) {
			List<String> values = config.getValues(key);
			ConfigurationSource source = config.getSource(key);
			JsonObject configObj = new JsonObject();
			configObj.addProperty("name", key);
			configObj.addProperty("source", source.toString());
			JsonArray valuesArray = new JsonArray();
			for (String value : values) {
				valuesArray.add(new JsonPrimitive(value));
			}
			configObj.add("values", valuesArray);
			settingsObj.add(configObj);
		}
		obj.add("settings", settingsObj);

		return obj;
	}

}
