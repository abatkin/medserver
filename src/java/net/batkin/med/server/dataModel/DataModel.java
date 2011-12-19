package net.batkin.med.server.dataModel;

import java.util.ArrayList;
import java.util.List;

import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;

public abstract class DataModel {
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

	public static BSONObject getObjectValue(BSONObject obj, String name) throws ServerDataException {
		if (!obj.containsField(name)) {
			throw new ServerDataException("Missing BSON attribute (BSONObject) " + name);
		}
		Object property = obj.get(name);
		if (!(property instanceof String)) {
			throw new ServerDataException("BSON Attribute " + name + " should be a BSONObject");
		}
		return (BSONObject) property;
	}
}
