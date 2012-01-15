package net.batkin.med.server.json.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.batkin.med.server.exception.ControllerException;

import com.google.gson.JsonObject;

public class UpdateConfigRequest extends ClientRequest {

	private String configName;
	private Map<String, List<String>> values;

	public UpdateConfigRequest(JsonObject request) throws ControllerException {
		this.configName = getStringValue(request, "configName");
		this.values = getArrayMap(request, "values", "name", new MapValueHandler<List<String>>() {
			@Override
			public List<String> getValue(JsonObject obj) throws ControllerException {
				return getArrayValue(obj, "values", String.class);
			}

			@Override
			public Map<String, List<String>> getMissingMap() throws ControllerException {
				return new HashMap<String, List<String>>();
			}
		});
	}

	public String getConfigName() {
		return configName;
	}

	public Map<String, List<String>> getValues() {
		return values;
	}
}
