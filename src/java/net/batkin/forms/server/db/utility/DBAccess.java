package net.batkin.forms.server.db.utility;

import net.batkin.forms.server.configuration.Configuration;
import net.batkin.forms.server.configuration.ConfigurationOption;
import net.batkin.forms.server.exception.ServerDataException;

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

}
