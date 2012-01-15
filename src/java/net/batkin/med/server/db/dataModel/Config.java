package net.batkin.med.server.db.dataModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.batkin.med.server.db.utility.BsonListCreator;
import net.batkin.med.server.db.utility.BsonListCreator.BsonValueHandler;
import net.batkin.med.server.db.utility.DBAccess.DatabaseCollection;
import net.batkin.med.server.db.utility.MapValueParser;
import net.batkin.med.server.db.utility.MapValueParser.MapValueHandler;
import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;
import org.bson.types.ObjectId;

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

	public BSONObject toBson() {
		BSONObject obj = new BasicBSONObject();

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
		DBObject config = DbDataModel.findDbObject(DatabaseCollection.Configs, "configName", configName, false);
		if (config == null) {
			return null;
		}

		return new Config(config);
	}
}
