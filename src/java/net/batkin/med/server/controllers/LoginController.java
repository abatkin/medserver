package net.batkin.med.server.controllers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.batkin.med.server.controllers.exception.LoginFailedException;
import net.batkin.med.server.dataModel.User;
import net.batkin.med.server.db.DBConfigUtility;
import net.batkin.med.server.db.DBUserUtility;
import net.batkin.med.server.exception.ControllerException;

import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class LoginController extends JsonController {

	@Override
	public JsonObject handle(RequestContext context, JsonObject request) throws ControllerException {
		if (request == null) {
			LoggerFactory.getLogger(LoginController.class).warn("No request data");
			throw new LoginFailedException();
		}

		String username = RequestUtility.getStringValue(request, "username");
		User user = DBUserUtility.loadUser(username);

		if (user == null) {
			LoggerFactory.getLogger(LoginController.class).warn("User not found");
			throw new LoginFailedException();
		}

		Map<String, List<String>> clientConfig = DBConfigUtility.loadDbConfig("client");

		String userHost = RequestUtility.getStringValue(request, "userHost", null);
		if (userHost != null) {
			Map<String, List<String>> hostConfig = DBConfigUtility.loadDbConfig("client" + "." + userHost);

			for (Entry<String, List<String>> entry : hostConfig.entrySet()) {
				if (!clientConfig.containsKey(entry.getKey())) {
					clientConfig.put(entry.getKey(), entry.getValue());
				}
			}
		}

		JsonObject response = new JsonObject();

		response.addProperty("username", username);
		response.addProperty("fullName", user.getFullName());
		response.add("permissions", ResponseUtility.toJsonList(user.getPermissions()));
		response.add("preferences", ResponseUtility.toJsonMapToString(user.getPreferences()));
		response.add("configuration", ResponseUtility.toJsonMapToListOfStrings(clientConfig));

		return response;
	}

}
