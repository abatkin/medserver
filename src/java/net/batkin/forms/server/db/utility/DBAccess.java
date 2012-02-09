package net.batkin.forms.server.db.utility;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.batkin.forms.server.configuration.Configuration;
import net.batkin.forms.server.configuration.ConfigurationLoader;
import net.batkin.forms.server.configuration.ConfigurationOption;
import net.batkin.forms.server.db.dataModel.Config;
import net.batkin.forms.server.exception.ServerDataException;
import net.batkin.forms.server.util.ConfigBuilder;

import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class DBAccess {

	public static final int NEXT_DATABASE_VERSION = 1;

	private static DB db;

	public static void connect() throws Exception {
		Configuration config = Configuration.getInstance();
		String dbHost = config.getValue(ConfigurationOption.CONFIG_DB_HOST, "127.0.0.1");
		int dbPort = config.getIntegerValue(ConfigurationOption.CONFIG_DB_PORT, -1);
		Mongo mongo;
		if (dbPort > 0) {
			mongo = new Mongo(dbHost, dbPort);
		} else {
			mongo = new Mongo(dbHost);
		}
		String dbName = config.getValue(ConfigurationOption.CONFIG_DB_NAME, "formsdb");
		db = mongo.getDB(dbName);
	}

	public static DB getDb() {
		return db;
	}

	public static DBCollection getCollection(String collectionName) {
		return db.getCollection(collectionName);
	}

	public static void initDatabase() throws ServerDataException {
		DatabaseCollection.initCollections(db);
	}

	public static void upgradeDatabase() throws ServerDataException, IOException {
		Configuration config = Configuration.getInstance();
		Config dbConfig = Config.loadByName("server");
		if (dbConfig == null) {
			dbConfig = Config.createAndSaveNewConfig("server", new HashMap<String, List<String>>());
		}

		int currentVersion = config.getIntegerValue(ConfigurationOption.CONFIG_DB_CURRENT_VERSION, 0);
		LoggerFactory.getLogger(DBAccess.class).warn("Database is at version [" + currentVersion + "], should be [" + NEXT_DATABASE_VERSION + "]");

		String upgradeDir = ConfigBuilder.getResourceDirectory("upgrade", ConfigurationOption.CONFIG_DB_UPGRADES_DIR);
		for (int version = currentVersion; version < NEXT_DATABASE_VERSION; version++) {
			File upgradeFile = new File(upgradeDir, "version-" + version + ".properties");
			if (upgradeFile.exists()) {
				performUpgrade(dbConfig, upgradeFile, version);
			} else {
				LoggerFactory.getLogger(DBAccess.class).warn("No upgrade file found for database version [" + version + "]");
			}
		}
	}

	private static void performUpgrade(Config config, File upgradeFile, int version) throws IOException {
		Properties props = new Properties();
		props.load(new FileReader(upgradeFile));

		Map<String, List<String>> newValues = ConfigurationLoader.parseProperties(props);
		for (Entry<String, List<String>> entry : newValues.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			config.put(key, values);
		}

		config.put(ConfigurationOption.CONFIG_DB_CURRENT_VERSION, Collections.singletonList("" + (version + 1)));
		config.save();
		LoggerFactory.getLogger(DBAccess.class).warn("Database upgraded to version [" + (version + 1) + "]");
	}
}
