package net.batkin.forms.server.upgrade;

import java.util.ArrayList;
import java.util.List;

import net.batkin.forms.server.db.dataModel.Config;
import net.batkin.forms.server.exception.ServerDataException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfigUpgradeData extends UpgradeData {

	private String configName;
	private List<KeyAndValues> ensureValues;

	public ConfigUpgradeData(JSONObject obj) throws JSONException {
		this.configName = obj.getString("configName");
		this.ensureValues = new ArrayList<KeyAndValues>();

		JSONArray evs = obj.optJSONArray("ensureValues");
		if (evs != null) {
			int numEvs = evs.length();
			for (int i = 0; i < numEvs; i++) {
				JSONObject evObj = evs.getJSONObject(i);
				KeyAndValues kav = new KeyAndValues(evObj);
				ensureValues.add(kav);
			}
		}
	}

	@Override
	public void apply() throws ServerDataException {
		Config config = ServerUpgrader.createOrFindConfig(configName);
		for (KeyAndValues kav : ensureValues) {
			List<String> values;
			if (config.containsKey(kav.getKey())) {
				values = config.get(kav.getKey());
			} else {
				values = new ArrayList<String>();
				config.put(kav.getKey(), values);
			}

			for (String value : kav.getValues()) {
				if (values.contains(value)) {
					continue;
				}
				values.add(value);
			}
		}
		config.save();
	}

	private class KeyAndValues {
		private String key;
		private List<String> values;

		public KeyAndValues(JSONObject obj) throws JSONException {
			this.key = obj.getString("key");
			this.values = new ArrayList<String>();

			JSONArray valueArray = obj.getJSONArray("values");
			for (int i = 0; i < valueArray.length(); i++) {
				values.add(valueArray.getString(i));
			}
		}

		public String getKey() {
			return key;
		}

		public List<String> getValues() {
			return values;
		}
	}

}
