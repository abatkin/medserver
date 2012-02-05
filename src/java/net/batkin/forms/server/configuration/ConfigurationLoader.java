package net.batkin.forms.server.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.batkin.forms.server.configuration.Configuration.ConfigurationSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationLoader {
	public static Map<String, List<String>> parseProperties(Properties properties) {
		Map<String, List<String>> newValues = new HashMap<String, List<String>>();

		Set<String> multiNames = new HashSet<String>();
		Set<String> singleNames = new HashSet<String>();

		for (Object keyObj : properties.keySet()) {
			String key = (String) keyObj;
			int lastPeriod = key.lastIndexOf(".");
			if (lastPeriod > 0) {
				String trailingValue = key.substring(lastPeriod + 1);
				try {
					Integer.parseInt(trailingValue);
					multiNames.add(key.substring(0, lastPeriod));
					continue;
				} catch (Exception e) {
					// Ignore
				}
			}
			singleNames.add(key);
		}

		for (String key : singleNames) {
			String value = properties.getProperty(key);
			newValues.put(key, Collections.singletonList(value));
		}

		for (String key : multiNames) {
			List<String> valueList = new ArrayList<String>();
			int i = 0;
			while (true) {
				String tempKey = key + "." + i;
				if (properties.containsKey(tempKey)) {
					String value = properties.getProperty(tempKey);
					valueList.add(value);
				} else {
					break;
				}
				i++;
			}
			newValues.put(key, valueList);
		}
		return newValues;
	}

	public static void loadProperties(Properties props) {
		Map<String, List<String>> newValues = parseProperties(props);
		Configuration.getInstance().addValues(ConfigurationSource.ConfigFile, newValues);
	}

	public static void loadCommandLine(String[] args) {
		Map<String, List<String>> newValues = new HashMap<String, List<String>>();

		List<String> previousValues = null;
		for (int i = 0; i < args.length; i++) {
			String currentValue = args[i];
			if (currentValue.startsWith("--")) {
				int equalsIndex = currentValue.indexOf("=");
				if (equalsIndex > 1) {
					String key = currentValue.substring(2, equalsIndex);
					String value = currentValue.substring(equalsIndex + 1);
					previousValues = addToMap(newValues, key, value);
				} else {
					String key = currentValue.substring(2);
					previousValues = addToMap(newValues, key, null);
				}
			} else {
				if (previousValues != null) {
					previousValues.add(currentValue);
				} else {
					// Ignore?
				}
			}
		}

		Configuration.getInstance().addValues(ConfigurationSource.CommandLine, newValues);
	}

	private static List<String> addToMap(Map<String, List<String>> map, String key, String value) {
		List<String> values = map.get(key);
		if (values == null) {
			values = new ArrayList<String>();
			map.put(key, values);
		}
		if (value != null) {
			values.add(value);
		}
		return values;
	}

	public static void dumpConfiguration() {
		Configuration config = Configuration.getInstance();
		List<String> keys = new ArrayList<String>(config.getKeys());
		Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);

		Collections.sort(keys);
		for (String key : keys) {
			for (String value : config.getValues(key)) {
				logger.info("[CONFIG] [" + config.getSource(key) + "]: " + key + "=" + value);
			}
		}
	}
}
