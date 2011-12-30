package net.batkin.med.server.db.utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.batkin.med.server.dataModel.DataModel;
import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;

public class MapValueParser {

	public static final SimpleMapValueCreator<String> OPTIONAL_STRING_VALUE_CREATOR = new SimpleMapValueCreator<String>("value", String.class);

	public static Map<String, String> getStringArrayMap(BSONObject parent, String name) throws ServerDataException {
		return getArrayMap(parent, name, "name", OPTIONAL_STRING_VALUE_CREATOR);
	}

	public interface MapValueHandler<T> {
		T getValue(BSONObject obj) throws ServerDataException;

		Map<String, T> getMissingMap() throws ServerDataException;
	}

	public static <T> Map<String, T> getArrayMap(BSONObject parentObj, String name, String keyName, MapValueHandler<T> valueCreator) throws ServerDataException {
		Map<String, T> valueMap = new HashMap<String, T>();

		List<BSONObject> valueObjects = DataModel.getOptionalArrayValue(parentObj, name, BSONObject.class);
		if (valueObjects == null) {
			return valueCreator.getMissingMap();
		}

		for (BSONObject valueObject : valueObjects) {
			String key = DataModel.getStringValue(valueObject, keyName);
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

		public T getValue(BSONObject obj) throws ServerDataException {
			Object objValue = obj.get(valueName);
			if (objValue == null) {
				throw new ServerDataException("Unable to find value " + valueName);
			}
			if (!valueClass.isInstance(objValue)) {
				throw new ServerDataException("Value (" + valueName + ") for must be of class " + valueClass.getSimpleName());
			}
			T realValue = valueClass.cast(objValue);
			return realValue;
		}

		@Override
		public Map<String, T> getMissingMap() throws ServerDataException {
			return new HashMap<String, T>();
		}
	}

}
