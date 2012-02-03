package net.batkin.forms.server.db.utility;

import net.batkin.forms.server.configuration.Configuration;
import net.batkin.forms.server.configuration.ConfigurationOption;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class DBAccess {

	private static DB db;

	public static void connect() throws Exception {
		if (db != null) {
			throw new RuntimeException("Already connected to database");
		}
		Configuration config = Configuration.getInstance();
		String dbHost = config.getValue(ConfigurationOption.CONFIG_DB_HOST, "127.0.0.1");
		int dbPort = config.getIntegerValue(ConfigurationOption.CONFIG_DB_PORT, -1);
		Mongo mongo;
		if (dbPort > 0) {
			mongo = new Mongo(dbHost, dbPort);
		} else {
			mongo = new Mongo(dbHost);
		}
		String dbName = config.getValue(ConfigurationOption.CONFIG_DB_NAME, "meddb");
		db = mongo.getDB(dbName);
	}

	public static DBCollection getCollection(String collectionName) {
		return db.getCollection(collectionName);
	}

	public static void initDatabase() {
		DatabaseCollection.initCollectiond(db);
	}
}
