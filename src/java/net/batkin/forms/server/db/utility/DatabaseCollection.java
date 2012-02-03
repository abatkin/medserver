package net.batkin.forms.server.db.utility;

import net.batkin.forms.server.db.dataModel.DbDataModel;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class DatabaseCollection {

	public static DatabaseCollection Users;
	public static DatabaseCollection Configs;
	public static DatabaseCollection Schemas;
	public static DatabaseCollection LookupTables;
	public static DatabaseCollection Changelogs;
	public static DatabaseCollection FormsData;
	public static DatabaseCollection Sessions;
	public static DatabaseCollection Layouts;

	private String name;

	public DatabaseCollection(DB database, String name) {
		this.name = name;
	}

	public DBObject findById(ObjectId id) {
		return findByCriteria(new BasicDBObject("_id", id));
	}

	public DBObject findByString(String key, String value) {
		return findByCriteria(new BasicDBObject(key, value));
	}

	public DBObject findByCriteria(BasicDBObject criteria) {
		return getCollection().findOne(criteria);
	}

	public void deleteObject(DbDataModel obj) {
		getCollection().remove(new BasicDBObject("_id", obj.getObjectId()));
	}

	public void saveObject(DbDataModel obj) {
		getCollection().save(obj.toDbObject());
	}

	public void saveDbObject(DBObject obj) {
		getCollection().save(obj);
	}

	public WriteResult removeByQuery(BasicDBObject query) {
		return getCollection().remove(query);
	}

	public DBCursor findWithFields(BasicDBObject query, String... fields) {
		BasicDBObject fieldList = new BasicDBObject();
		for (String field : fields) {
			fieldList.put(field, Integer.valueOf(1));
		}
		return getCollection().find(query, fieldList);
	}

	protected DBCollection getCollection() {
		return DBAccess.getCollection(name);
	}

	public static void initCollectiond(DB db) {
		Users = buildCollection(db, "users", new String[] { "username" });
		Configs = buildCollection(db, "configs", new String[] { "configName" });
		Schemas = buildCollection(db, "schemas", new String[] { "schemaName" });
		LookupTables = buildCollection(db, "lookuptables", new String[] { "tableName" });
		Changelogs = buildCollection(db, "changelogs", new String[] { "userId", "timestamp", "patientId" });
		FormsData = buildCollection(db, "formsdata", new String[] { "schemaName" });
		Sessions = buildCollection(db, "sessions", null);
		Layouts = buildCollection(db, "layouts", new String[] { "formName" });
	}

	public static DatabaseCollection buildCollection(DB database, String collectionName, String[] indexes) {
		DatabaseCollection coll = new DatabaseCollection(database, collectionName);
		if (indexes != null) {
			DBCollection dbColl = coll.getCollection();
			for (String indexName : indexes) {
				dbColl.ensureIndex(indexName);
			}
		}
		return coll;
	}
}
