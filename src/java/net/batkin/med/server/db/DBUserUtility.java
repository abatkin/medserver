package net.batkin.med.server.db;

import net.batkin.med.server.dataModel.User;
import net.batkin.med.server.db.DBAccess.DatabaseCollection;
import net.batkin.med.server.exception.ServerDataException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class DBUserUtility {

	public static User loadUserByUsername(String username) throws ServerDataException {
		DBCollection c = DBAccess.getCollection(DatabaseCollection.Users);
		DBObject userCriteria = new BasicDBObject();
		userCriteria.put("userName", username);
		DBObject obj = c.findOne(userCriteria);
		if (obj == null) {
			return null;
		}

		User user = new User(obj);
		return user;
	}

	public static User loadUserById(ObjectId userId) throws ServerDataException {
		DBCollection c = DBAccess.getCollection(DatabaseCollection.Users);
		DBObject server = new BasicDBObject();
		server.put("_id", userId);
		DBObject obj = c.findOne(server);
		if (obj == null) {
			return null;
		}

		User user = new User(obj);
		return user;
	}
}
