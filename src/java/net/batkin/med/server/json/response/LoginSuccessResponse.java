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
		JsonObject response = UserDetailResponse.buildBasicUserObject(user, true);
		response.add("configuration", toJsonMapToListOfStrings(clientConfig, "name", "values"));

		return response;
	}


}
