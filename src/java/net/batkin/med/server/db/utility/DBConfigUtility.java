package net.batkin.med.server.db.utility;

import java.util.List;
import java.util.Map;

import net.batkin.med.server.dataModel.DataModel;
import net.batkin.med.server.db.DBAccess.DatabaseCollection;
import net.batkin.med.server.db.utility.MapValueParser.MapValueHandler;
import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;

import com.mongodb.DBObject;

public class DBConfigUtility {

	private static final MapValueHandler<List<String>> CONFIG_VALUE_PARSER = new MapValueHandler<List<String>>() {
		@Override
		public List<String> getValue(BSONObject obj) throws ServerDataException {
			return DataModel.getArrayValue(obj, "values", String.class);
		}

		@Override
		public Map<String, List<String>> getMissingMap() throws ServerDataException {
			return null;
		}
	};

	public static Map<String, List<String>> loadDbConfig(String configName) throws ServerDataException {
		DBObject config = DataModel.findDbObject(DatabaseCollection.Configs, "configName", configName, false);
		if (config == null) {
			return null;
		}

		return MapValueParser.getArrayMap(config, "values", "name", CONFIG_VALUE_PARSER);
	}
}
