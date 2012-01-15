package net.batkin.med.server.json.response;

import com.google.gson.JsonObject;

import net.batkin.med.server.db.dataModel.User;

public class UserDetailResponse extends ClientResponse {

	private User user;

	public UserDetailResponse(User user) {
		this.user = user;
	}

	public JsonObject toJson() {
		return buildBasicUserObject(user, true);
	}

	public static JsonObject buildBasicUserObject(User user, boolean includeDetails) {
		JsonObject response = new JsonObject();

		response.addProperty("username", user.getUsername());
		response.addProperty("fullName", user.getFullName());

		if (includeDetails) {
			response.add("permissions", toJsonList(user.getPermissions()));
			response.add("preferences", toJsonMapToString(user.getPreferences(), "name", "value"));
		}

		return response;
	}
}
