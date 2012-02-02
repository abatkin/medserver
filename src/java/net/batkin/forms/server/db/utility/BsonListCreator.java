package net.batkin.forms.server.db.utility;

import java.util.Map;
import java.util.Map.Entry;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

public class BsonListCreator {

	public static BasicBSONList createSimpleList(Map<String, String> map) {
		return BsonListCreator.createList(map, "name", new BsonValueHandler<String>() {
			@Override
			public void populateBsonObject(BSONObject bsonObject, String valueObject) {
				bsonObject.put("value", valueObject);
			}
		});
	}

	public static <T> BasicBSONList createList(Map<String, T> map, String keyName, BsonValueHandler<T> valueHandler) {
		BasicBSONList list = new BasicBSONList();

		for (Entry<String, T> entry : map.entrySet()) {
			BSONObject obj = new BasicBSONObject();
			obj.put(keyName, entry.getKey());
			valueHandler.populateBsonObject(obj, entry.getValue());
			list.add(obj);
		}

		return list;
	}

	public interface BsonValueHandler<T> {
		void populateBsonObject(BSONObject bsonObject, T valueObject);
	}
}
