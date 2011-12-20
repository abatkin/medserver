package net.batkin.med.server.controllers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.LoggerFactory;

import net.batkin.med.server.dataModel.User;
import net.batkin.med.server.db.DBConfigUtility;
import net.batkin.med.server.db.DBUserUtility;
import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.exception.LoginFailedException;

import com.google.gson.JsonObject;

public class LoginController extends Controller {

	@Override
	public JsonObject handle(String[] parts, JsonObject request) throws ControllerException {
		if (request == null) {
			LoggerFactory.getLogger(LoginController.class).warn("No request data");
			throw new LoginFailedException();
		}
		
		String username = getStringValue(request, "username");
		User user = DBUserUtility.loadUser(username);

		if (user == null) {
			LoggerFactory.getLogger(LoginController.class).warn("User not found");
			throw new LoginFailedException();
		}

		Map<String, List<String>> clientConfig = DBConfigUtility.loadDbConfig("client");

		String userHost = getStringValue(request, "userHost", null);
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
		response.add("permissions", toJsonList(user.getPermissions()));
		response.add("preferences", toJsonMapToString(user.getPreferences()));
		response.add("configuration", toJsonMapToListOfStrings(clientConfig));

		return response;
	}



}
