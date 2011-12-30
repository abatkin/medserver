package net.batkin.med.server.db;

import net.batkin.med.server.configuration.Configuration;
import net.batkin.med.server.configuration.ConfigurationOption;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class DBAccess {

	private static DB db;

	public enum DatabaseCollection {
		Users("users", new String[] {"username"}),
		Configs("configs", new String[] {"configName"}),
		Schemas("schemas", new String[] {"schemaName"}),
		LookupTables("lookuptables", new String[] {"tableName"}),
		Changelogs("changelogs", new String[] {"userId", "timestamp", "patientId"}),
		Patients("patients", new String[] {"patientName"}),
		Forms("forms", new String[] {"patientId"}),
		Sessions("sessions", new String[0]);

		private String name;
		private String[] indexes;

		DatabaseCollection(String name, String[] indexes) {
			this.name = name;
			this.indexes = indexes;
		}
	}

	public static void createDatabase() {
		for (DatabaseCollection collection : DatabaseCollection.values()) {
			DBCollection mongoCollection = db.getCollection(collection.name);
			for (String indexName : collection.indexes) {
				mongoCollection.ensureIndex(indexName);
			}
		}
	}

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

	public static DBCollection getCollection(DatabaseCollection collection) {
		return db.getCollection(collection.name);
	}

	public static DB getConnection() {
		return db;
	}
}