package net.batkin.med.server.db.dataModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.batkin.med.server.db.utility.DBAccess;
import net.batkin.med.server.db.utility.DBAccess.DatabaseCollection;
import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public abstract class DbDataModel {

	public static ObjectId getObjectIdValue(BSONObject obj, String name) throws ServerDataException {
		if (!obj.containsField(name)) {
			throw new ServerDataException("Missing BSON attribute (ObjectId) " + name);
		}
		Object property = obj.get(name);
		if (!(property instanceof ObjectId)) {
			throw new ServerDataException("BSON Attribute " + name + " should be a ObjectId");
		}
		return (ObjectId) property;
	}

	public static Date getDateValue(BSONObject obj, String name) throws ServerDataException {
		if (!obj.containsField(name)) {
			throw new ServerDataException("Missing BSON attribute (Date) " + name);
		}
		Object property = obj.get(name);
		if (!(property instanceof Date)) {
			throw new ServerDataException("BSON Attribute " + name + " should be a Date");
		}
		return (Date) property;

	}

	public static String getStringValue(BSONObject obj, String name) throws ServerDataException {
		if (!obj.containsField(name)) {
			throw new ServerDataException("Missing BSON attribute (String) " + name);
		}
		Object property = obj.get(name);
		if (!(property instanceof String)) {
			throw new ServerDataException("BSON Attribute " + name + " should be a String");
		}
		return (String) property;
	}

	public static <T> List<T> getOptionalArrayValue(BSONObject obj, String name, Class<T> elementClass) throws ServerDataException {
		if (obj.containsField(name)) {
			return getArrayValue(obj, name, elementClass);
		}
		return null;
	}

	public static <T> List<T> getArrayValue(BSONObject obj, String name, Class<T> elementClass) throws ServerDataException {
		if (!obj.containsField(name)) {
			throw new ServerDataException("Missing BSON attribute (Array) " + name);
		}
		Object property = obj.get(name);
		if (!(property instanceof List<?>)) {
			throw new ServerDataException("BSON Attribute " + name + " should be an Array");
		}
		List<?> objects = (List<?>) property;
		List<T> values = new ArrayList<T>(objects.size());
		int index = 0;
		for (Object object : objects) {
			if (!elementClass.isInstance(object)) {
				throw new ServerDataException("BSON Attribute " + name + " index " + index + " must be of class " + elementClass.getName());
			}
			values.add(elementClass.cast(object));
			index++;
		}
		return values;
	}

	public static BSONObject getOptionalObjectValue(BSONObject obj, String name) throws ServerDataException {
		if (obj.containsField(name)) {
			return getObjectValue(obj, name);
		}
		return null;
	}

	public static BSONObject getObjectValue(BSONObject obj, String name) throws ServerDataException {
		if (!obj.containsField(name)) {
			throw new ServerDataException("Missing BSON attribute (BSONObject) " + name);
		}
		Object property = obj.get(name);
		if (!(property instanceof BSONObject)) {
			throw new ServerDataException("BSON Attribute " + name + " should be a BSONObject");
		}
		return (BSONObject) property;
	}

	public static DBObject findDbObject(DatabaseCollection collection, String key, Object value, boolean required) throws ServerDataException {
		DBCollection c = DBAccess.getCollection(collection);
		DBObject obj = c.findOne(new BasicDBObject(key, value));
		if (obj == null && required) {
			throw new ServerDataException("Unable to find " + collection + "." + key + "=" + value);
		}
		return obj;
	}
}
