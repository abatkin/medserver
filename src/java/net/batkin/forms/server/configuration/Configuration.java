package net.batkin.forms.server.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.batkin.forms.server.exception.ConfigurationException;

public class Configuration {

	private static Configuration instance;

	public static void initialize() {
		instance = new Configuration();
	}

	public static Configuration getInstance() {
		return instance;
	}

	private Map<String, ConfigurationValue> values;

	private Configuration() {
		this.values = new HashMap<String, ConfigurationValue>();
	}

	public boolean containsKey(String key) {
		return values.containsKey(key);
	}

	public String getValue(String key) {
		ConfigurationValue valueObject = values.get(key);
		if (valueObject != null) {
			if (valueObject.values.size() > 0) {
				return valueObject.values.get(0);
			}
		}
		return null;
	}

	public String getValue(String key, String defaultValue) {
		ConfigurationValue valueObject = values.get(key);
		if (valueObject != null) {
			if (valueObject.values.size() > 0) {
				return valueObject.values.get(0);
			}
		}
		return defaultValue;
	}

	public int getIntegerValue(String key) throws ConfigurationException {
		try {
			String value = getValue(key);
			if (value != null) {
				return Integer.parseInt(value);
			}
		} catch (Exception e) {
			// Ignore
		}
		throw new ConfigurationException("Invalid or missing int configuration parameter " + key);
	}

	public int getIntegerValue(String key, int defaultValue) {
		try {
			String value = getValue(key);
			if (value != null) {
				return Integer.parseInt(value);
			}
		} catch (Exception e) {
			// Ignore
		}
		return defaultValue;
	}

	public List<String> getValues(String key) {
		ConfigurationValue valueObject = values.get(key);
		if (valueObject != null) {
			return valueObject.values;
		}
		return Collections.emptyList();
	}

	public ConfigurationSource getSource(String key) {
		ConfigurationValue valueObject = values.get(key);
		if (valueObject != null) {
			return valueObject.source;
		}
		return null;
	}

	public Set<String> getKeys() {
		return values.keySet();
	}

	public void addValues(ConfigurationSource source, String key, List<String> newValues) {
		if (values.containsKey(key) && !values.get(key).source.equals(source)) {
			return;
		}
		values.put(key, new ConfigurationValue(key, newValues, source));
	}

	public void addValues(ConfigurationSource source, Map<String, List<String>> newValues) {
		for (Entry<String, List<String>> entry : newValues.entrySet()) {
			addValues(source, entry.getKey(), entry.getValue());
		}
	}

	public static class ConfigurationSource {
		private String name;

		private ConfigurationSource(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof ConfigurationSource) {
				return ((ConfigurationSource) obj).name.equals(name);
			}
			return false;
		}

		public static final ConfigurationSource CommandLine = new ConfigurationSource("CommandLine");
		public static final ConfigurationSource ConfigFile = new ConfigurationSource("ConfigFile");
		public static final ConfigurationSource Computed = new ConfigurationSource("Computed");

		public static ConfigurationSource createDatabaseSource(String name) {
			return new ConfigurationSource("Database<" + name + ">");
		}
	}

	public class ConfigurationValue {
		public final String key;
		public final List<String> values;
		public final ConfigurationSource source;

		public ConfigurationValue(String key, List<String> values, ConfigurationSource source) {
			this.key = key;
			this.values = values;
			this.source = source;
		}
	}

}
