package net.batkin.forms.server.upgrade;

import java.util.ArrayList;
import java.util.List;

import net.batkin.forms.server.db.dataModel.Config;
import net.batkin.forms.server.exception.ServerDataException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ManagedTypeUpgradeData extends UpgradeData {

	private List<ManagedType> ensureTypes;

	public ManagedTypeUpgradeData(JSONObject obj) throws JSONException {
		this.ensureTypes = new ArrayList<ManagedType>();

		JSONArray ets = obj.optJSONArray("ensureTypes");
		if (ets != null) {
			int numEts = ets.length();
			for (int i = 0; i < numEts; i++) {
				JSONObject etObj = ets.getJSONObject(i);
				ManagedType mt = new ManagedType(etObj);
				ensureTypes.add(mt);
			}
		}
	}

	@Override
	public void apply() throws ServerDataException, JSONException {
		Config config = ServerUpgrader.createOrFindConfig("server");

		boolean somethingChanged = false;

		for (ManagedType mt : ensureTypes) {
			for (ManagedType.ManagedTypeInfo mti : mt.getTypeInfos()) {
				if (ensureExists(config, mt.getTypeName() + ".types", mti.getName())) {
					somethingChanged = true;
				}
				if (ensureEquals(config, mt.getTypeName() + ".type." + mti.getName(), mti.getJavaClass())) {
					somethingChanged = true;
				}
			}
		}

		if (somethingChanged) {
			config.save();
		}
	}

	private boolean ensureEquals(Config config, String key, String value) {
		if (config.containsKey(key)) {
			List<String> values = config.get(key);
			if (values.size() == 1 && values.contains(value)) {
				return false;
			} else {
				values.clear();
				values.add(value);
				return true;
			}
		} else {
			List<String> values = new ArrayList<String>();
			values.add(value);
			config.put(key, values);
			return true;
		}
	}

	private boolean ensureExists(Config config, String key, String value) {
		if (config.containsKey(key)) {
			List<String> values = config.get(key);
			if (values.contains(value)) {
				return false;
			} else {
				values.add(value);
				return true;
			}
		} else {
			List<String> values = new ArrayList<String>();
			values.add(value);
			config.put(key, values);
			return true;
		}
	}

	private class ManagedType {
		private String typeName;
		private List<ManagedTypeInfo> typeInfos;

		public ManagedType(JSONObject obj) throws JSONException {
			this.typeName = obj.getString("typeName");
			this.typeInfos = new ArrayList<ManagedTypeInfo>();

			JSONArray mtis = obj.optJSONArray("values");

			if (mtis != null) {
				for (int i = 0; i < mtis.length(); i++) {
					typeInfos.add(new ManagedTypeInfo(mtis.getJSONObject(i)));
				}
			}
		}

		public List<ManagedTypeInfo> getTypeInfos() {
			return typeInfos;
		}

		public String getTypeName() {
			return typeName;
		}

		private class ManagedTypeInfo {

			private String name;
			private String javaClass;

			public ManagedTypeInfo(JSONObject obj) throws JSONException {
				this.name = obj.getString("name");
				this.javaClass = obj.getString("javaClass");
			}

			public String getName() {
				return name;
			}

			public String getJavaClass() {
				return javaClass;
			}
		}
	}
}
