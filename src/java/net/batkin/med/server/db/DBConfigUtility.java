package net.batkin.med.server.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.batkin.med.server.db.DBAccess.DatabaseCollection;

import org.bson.BSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class DBConfigUtility {
	public static Map<String, List<String>> loadDbConfig(String configName) {
		DBCollection c = DBAccess.getCollection(DatabaseCollection.Configs);
		DBObject server = new BasicDBObject();
		server.put("configName", configName);
		DBObject obj = c.findOne(server);
		if (obj == null) {
			return null;
		}

		Object configObj = obj.get("values");
		if (!(configObj instanceof BSONObject)) {
			return null;
		}

		BSONObject config = (BSONObject) configObj;

		Map<String, List<String>> newValues = new HashMap<String, List<String>>();
		for (String key : config.keySet()) {
			Object val = config.get(key);
			if (val instanceof String) {
				newValues.put(key, Collections.singletonList((String) val));
			} else if (val instanceof List) {
				List<String> values = new ArrayList<String>();
				for (Object listObj : (List<?>) val) {
					values.add(listObj.toString());
				}
				newValues.put(key, values);
			}
		}

		return newValues;
	}
}
