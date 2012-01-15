package net.batkin.med.server.json.response;

import java.util.List;
import java.util.Map;

import net.batkin.med.server.db.dataModel.User;

import com.google.gson.JsonObject;

public class LoginSuccessResponse extends ClientResponse {

	private User user;
	private Map<String, List<String>> clientConfig;

	public LoginSuccessResponse(User user, Map<String, List<String>> clientConfig) {
		this.user = user;
		this.clientConfig = clientConfig;
	}

	public JsonObject toJson() {
		JsonObject response = new JsonObject();

		response.addProperty("username", user.getUsername());
		response.addProperty("fullName", user.getFullName());
		response.add("permissions", toJsonList(user.getPermissions()));
		response.add("preferences", toJsonMapToString(user.getPreferences()));
		response.add("configuration", toJsonMapToListOfStrings(clientConfig));

		return response;
	}
}
