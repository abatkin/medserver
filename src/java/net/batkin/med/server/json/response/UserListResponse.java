package net.batkin.med.server.json.response;

import java.util.List;

import net.batkin.med.server.db.dataModel.User;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class UserListResponse extends ClientResponse {

	private List<User> users;

	public UserListResponse(List<User> users) {
		this.users = users;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();

		JsonArray userList = new JsonArray();
		for (User user : users) {
			userList.add(UserDetailResponse.buildBasicUserObject(user, false));
		}
		obj.add("users", userList);

		return obj;
	}
}
