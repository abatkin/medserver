package net.batkin.med.server.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.batkin.med.server.db.dataModel.Config;
import net.batkin.med.server.exception.ServerDataException;

import org.slf4j.LoggerFactory;

public class ConfigBuilder {
	public static void mergeConfig(Map<String, List<String>> existingConfig, Map<String, List<String>> configToAdd) {
		if (configToAdd == null || configToAdd.isEmpty()) {
			return;
		}

		for (Entry<String, List<String>> entry : configToAdd.entrySet()) {
			if (existingConfig.containsKey(entry.getKey())) {
				continue;
			}
			existingConfig.put(entry.getKey(), entry.getValue());
		}
	}

	public static Map<String, List<String>> buildConfigFromDatabase(String... configNames) {
		Map<String, List<String>> theConfig = new HashMap<String, List<String>>();
		for (String configName : configNames) {
			try {
				Config thisConfig = Config.loadByName(configName);
				if (thisConfig == null) {
					continue;
				}

				mergeConfig(theConfig, thisConfig);
			} catch (ServerDataException e) {
				LoggerFactory.getLogger(ConfigBuilder.class).warn(e.getMessage(), e);
				// Ignore
			}
		}
		return theConfig;
	}
}
