package net.batkin.forms.server.db.dataModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.batkin.forms.server.db.utility.BsonListCreator;
import net.batkin.forms.server.db.utility.BsonListCreator.BsonValueHandler;
import net.batkin.forms.server.db.utility.DatabaseCollection;
import net.batkin.forms.server.db.utility.MapValueParser;
import net.batkin.forms.server.db.utility.MapValueParser.MapValueHandler;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.BasicBSONList;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Config extends DbDataModel implements Map<String, List<String>> {

	private ObjectId id;
	private String name;
	private Map<String, List<String>> values;

	private Config(DBObject config) throws ServerDataException {
		this.id = getObjectIdValue(config, "_id");
		this.name = getStringValue(config, "configName");
		this.values = MapValueParser.getArrayMap(config, "values", "name", CONFIG_VALUE_PARSER);
	}

	private Config(ObjectId id, String name, Map<String, List<String>> values) {
		this.id = id;
		this.name = name;
		this.values = values;
	}

	public static Config createAndSaveNewConfig(String name, Map<String, List<String>> values) {
		Config config = new Config(new ObjectId(), name, values);
		DatabaseCollection.Configs.saveObject(config);
		return config;
	}

	public DBObject toDbObject() {
		DBObject obj = new BasicDBObject();

		obj.put("_id", id);
		obj.put("configName", name);
		obj.put("values", valuesToBson());

		return obj;
	}

	private BasicBSONList valuesToBson() {
		return BsonListCreator.createList(values, "name", new BsonValueHandler<List<String>>() {
			@Override
			public void populateBsonObject(BSONObject bsonObject, List<String> valueObject) {
				BasicBSONList list = new BasicBSONList();
				list.addAll(valueObject);
				bsonObject.put("values", list);
			}
		});
	}

	@Override
	public ObjectId getObjectId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return values.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values.containsValue(value);
	}

	@Override
	public List<String> get(Object key) {
		return values.get(key);
	}

	@Override
	public List<String> put(String key, List<String> value) {
		return values.put(key, value);
	}

	@Override
	public List<String> remove(Object key) {
		return values.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends List<String>> m) {
		values.putAll(m);
	}

	@Override
	public void clear() {
		values.clear();
	}

	@Override
	public Set<String> keySet() {
		return values.keySet();
	}

	@Override
	public Collection<List<String>> values() {
		return values.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, List<String>>> entrySet() {
		return values.entrySet();
	}

	public void replaceValues(Map<String, List<String>> newValues) {
		clear();
		putAll(newValues);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Config)) {
			return false;
		}
		Config other = (Config) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	private static final MapValueHandler<List<String>> CONFIG_VALUE_PARSER = new MapValueHandler<List<String>>() {
		@Override
		public List<String> getValue(BSONObject obj) throws ServerDataException {
			return DbDataModel.getArrayValue(obj, "values", String.class);
		}

		@Override
		public Map<String, List<String>> getMissingMap() throws ServerDataException {
			return null;
		}
	};

	public static Config loadByName(String configName) throws ServerDataException {
		DBObject config = DatabaseCollection.Configs.findByString("configName", configName);
		if (config == null) {
			return null;
		}

		return new Config(config);
	}

	public static List<String> listConfigNames() {
		List<String> configNames = new ArrayList<String>();

		DBCursor cursor = DatabaseCollection.Configs.findWithFields(new BasicDBObject(), "configName");
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Object configNameObj = obj.get("configName");
			if (configNameObj != null && configNameObj instanceof String) {
				String configName = (String) configNameObj;
				if (!configName.isEmpty()) {
					configNames.add(configName);
				}
			}
		}

		return configNames;
	}

}
