package net.batkin.forms.server.upgrade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.batkin.forms.server.exception.ServerDataException;
import net.batkin.forms.server.upgrade.exception.UpgradeException;
import net.batkin.forms.server.util.JsonModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerUpgrade extends JsonModel {

	private String filename;
	private String name;
	private Set<String> dependencies;
	private List<UpgradeData> upgrades;

	public ServerUpgrade(String filename, JSONObject json) throws UpgradeException, JSONException {
		this.filename = filename;
		this.name = json.getString("name");
		this.dependencies = new HashSet<String>();
		this.upgrades = new ArrayList<UpgradeData>();

		JSONArray depends = json.optJSONArray("depdends");
		if (depends != null) {
			int numDepends = depends.length();
			for (int i = 0; i < numDepends; i++) {
				String depName = depends.getString(i);
				dependencies.add(depName);
			}
		}

		JSONArray uds = json.optJSONArray("upgrades");
		if (uds != null) {
			int numUds = uds.length();
			for (int i = 0; i < numUds; i++) {
				JSONObject udObj = uds.getJSONObject(i);
				String udType = udObj.getString("type");

				UpgradeData ud;
				if (udType.equals("config")) {
					ud = new ConfigUpgradeData(udObj);
				} else if (udType.equals("managedTypes")) {
					ud = new ManagedTypeUpgradeData(udObj);
				} else {
					throw new UpgradeException("Upgrade " + name + " contains an unknown upgrade type " + udType + " (" + filename + ")");
				}
				upgrades.add(ud);
			}
		}
	}

	public String getName() {
		return name;
	}

	public Set<String> getDependencies() {
		return dependencies;
	}

	public void apply() throws UpgradeException {
		for (UpgradeData ud : upgrades) {
			try {
				ud.apply();
			} catch (ServerDataException e) {
				throw new UpgradeException("Problem while running upgrade from file " + filename + ": " + e.getMessage(), e);
			} catch (JSONException e) {
				throw new UpgradeException("Problem while running upgrade from file " + filename + ": " + e.getMessage(), e);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ServerUpgrade other = (ServerUpgrade) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
