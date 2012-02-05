package net.batkin.forms.server.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.batkin.forms.server.configuration.Configuration;
import net.batkin.forms.server.configuration.ConfigurationOption;
import net.batkin.forms.server.configuration.Configuration.ConfigurationSource;
import net.batkin.forms.server.db.dataModel.Config;
import net.batkin.forms.server.exception.ServerDataException;

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

	public static String getResourceDirectory(String name, String configKey) throws IOException {
		Configuration config = Configuration.getInstance();
		String configDir = config.getValue(configKey, null);

		if (configDir == null) {
			String rootDir = config.getValue(ConfigurationOption.CONFIG_APP_ROOT_DIR, null);
			if (rootDir != null) {
				return new File(rootDir, name).getCanonicalPath();
			}
			File inDevel = new File("./src/webapp");
			if (inDevel.isDirectory()) {
				configDir = new File(inDevel, name).getCanonicalPath();
			} else {
				configDir = new File("./" + name).getCanonicalPath();
			}
		}

		config.addValues(ConfigurationSource.Computed, configKey, Collections.singletonList(configDir));

		LoggerFactory.getLogger(ConfigBuilder.class).info("Using directory [" + configDir + "] for " + name + " files");
		return configDir;
	}
}
