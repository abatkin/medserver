package net.batkin.med.server.json.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.batkin.med.server.exception.ClientRequestException;
import net.batkin.med.server.exception.ControllerException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ClientRequest {

	public static String getStringValue(JsonObject obj, String name) throws ControllerException {
		JsonElement el = obj.get(name);
		if (el == null) {
			throw new ClientRequestException("Missing key " + name);
		}
		return el.getAsString();
	}

	public static String getStringValue(JsonObject obj, String name, String defaultValue) throws ControllerException {
		JsonElement el = obj.get(name);
		if (el == null) {
			return defaultValue;
		}
		return el.getAsString();
	}

	public static <T> List<T> getArrayValue(JsonObject obj, String name, Class<T> elementClass) throws ControllerException {
		if (!obj.has(name)) {
			throw new ClientRequestException("Missing JSON attribute (Array) " + name);
		}
		Object property = obj.get(name);
		if (!(property instanceof List<?>)) {
			throw new ClientRequestException("JSON Attribute " + name + " should be an Array");
		}
		List<?> objects = (List<?>) property;
		List<T> values = new ArrayList<T>(objects.size());
		int index = 0;
		for (Object object : objects) {
			if (!elementClass.isInstance(object)) {
				throw new ClientRequestException("JSON Attribute " + name + " index " + index + " must be of class " + elementClass.getName());
			}
			values.add(elementClass.cast(object));
			index++;
		}
		return values;
	}

	public static Map<String, String> getStringArrayMap(JsonObject parent, String name) throws ControllerException {
		return getArrayMap(parent, name, "name", new MapValueHandler<String>() {
			@Override
			public String getValue(JsonObject obj) throws ControllerException {
				return getStringValue(obj, "value");
			}

			@Override
			public Map<String, String> getMissingMap() throws ControllerException {
				return new HashMap<String, String>();
			}
		});
	}

	public interface MapValueHandler<T> {
		T getValue(JsonObject obj) throws ControllerException;

		Map<String, T> getMissingMap() throws ControllerException;
	}

	public static <T> Map<String, T> getArrayMap(JsonObject parentObj, String name, String keyName, MapValueHandler<T> valueCreator) throws ControllerException {
		Map<String, T> valueMap = new HashMap<String, T>();

		List<JsonObject> valueObjects = getArrayValue(parentObj, name, JsonObject.class);
		if (valueObjects == null) {
			return valueCreator.getMissingMap();
		}

		for (JsonObject valueObject : valueObjects) {
			String key = getStringValue(valueObject, keyName);
			T realValue = valueCreator.getValue(valueObject);
			valueMap.put(key, realValue);
		}

		return valueMap;
	}

	public static class SimpleMapValueCreator<T> implements MapValueHandler<T> {

		private Class<T> valueClass;
		private String valueName;

		public SimpleMapValueCreator(String valueName, Class<T> valueClass) {
			this.valueName = valueName;
			this.valueClass = valueClass;
		}

		public T getValue(JsonObject obj) throws ControllerException {
			Object objValue = obj.get(valueName);
			if (objValue == null) {
				throw new ClientRequestException("Unable to find value " + valueName);
			}
			if (!valueClass.isInstance(objValue)) {
				throw new ClientRequestException("Value (" + valueName + ") for must be of class " + valueClass.getSimpleName());
			}
			T realValue = valueClass.cast(objValue);
			return realValue;
		}

		@Override
		public Map<String, T> getMissingMap() throws ControllerException {
			return new HashMap<String, T>();
		}
	}

}
