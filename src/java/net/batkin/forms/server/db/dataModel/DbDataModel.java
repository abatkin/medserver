package net.batkin.forms.server.db.dataModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public abstract class DbDataModel {

	public abstract ObjectId getObjectId();

	public abstract DBObject toDbObject();

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
		Date value = getDateValue(obj, name, null);
		if (value == null) {
			throw new ServerDataException("BSON Attribute " + name + " (Date) is missing");
		}
		return value;
	}

	public static Date getDateValue(BSONObject obj, String name, Date defaultValue) throws ServerDataException {
		if (!obj.containsField(name)) {
			return defaultValue;
		}
		Object property = obj.get(name);
		if (!(property instanceof Date)) {
			throw new ServerDataException("BSON Attribute " + name + " should be a Date");
		}
		return (Date) property;

	}

	public static String getStringValue(BSONObject obj, String name) throws ServerDataException {
		String value = getStringValue(obj, name, null);
		if (value == null) {
			throw new ServerDataException("BSON Attribute " + name + " (String) is missing");
		}
		return value;
	}

	public static String getStringValue(BSONObject obj, String name, String defaultValue) throws ServerDataException {
		if (!obj.containsField(name)) {
			return defaultValue;
		}
		Object property = obj.get(name);
		if (!(property instanceof String)) {
			throw new ServerDataException("BSON Attribute " + name + " should be a String");
		}
		return (String) property;
	}

	public static Integer getIntegerValue(BSONObject obj, String name) throws ServerDataException {
		Integer value = getIntegerValue(obj, name, null);
		if (value == null) {
			throw new ServerDataException("BSON Attribute " + name + " (Integer) is missing");
		}
		return value;
	}

	public static Integer getIntegerValue(BSONObject obj, String name, Integer defaultValue) throws ServerDataException {
		if (!obj.containsField(name)) {
			return defaultValue;
		}
		Object property = obj.get(name);
		if (!(property instanceof Integer)) {
			throw new ServerDataException("BSON Attribute " + name + " should be an Integer");
		}
		return (Integer) property;
	}

	public static Float getFloatValue(BSONObject obj, String name) throws ServerDataException {
		Float value = getFloatValue(obj, name, null);
		if (value == null) {
			throw new ServerDataException("BSON Attribute " + name + " (Float) is missing");
		}
		return value;
	}

	public static Float getFloatValue(BSONObject obj, String name, Float defaultValue) throws ServerDataException {
		if (!obj.containsField(name)) {
			return defaultValue;
		}
		Object property = obj.get(name);
		if (!(property instanceof Float)) {
			throw new ServerDataException("BSON Attribute " + name + " should be a Float");
		}
		return (Float) property;
	}

	public static Boolean getBooleanValue(BSONObject obj, String name) throws ServerDataException {
		Boolean value = getBooleanValue(obj, name, null);
		if (value == null) {
			throw new ServerDataException("BSON Attribute " + name + " (Boolean) is missing");
		}
		return value;
	}

	public static Boolean getBooleanValue(BSONObject obj, String name, Boolean defaultValue) throws ServerDataException {
		if (!obj.containsField(name)) {
			return defaultValue;
		}
		Object property = obj.get(name);
		if (!(property instanceof Boolean)) {
			throw new ServerDataException("BSON Attribute " + name + " should be a Boolean");
		}
		return ((Boolean) property);
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
	
	public static void putValue(BSONObject obj, String key, Object val) {
		if (val == null) {
			return;
		}
		obj.put(key, val);
	}
}
