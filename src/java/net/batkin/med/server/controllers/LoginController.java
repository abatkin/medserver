package net.batkin.med.server.controllers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.batkin.med.server.dataModel.User;
import net.batkin.med.server.db.DBConfigUtility;
import net.batkin.med.server.db.DBUserUtility;
import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.exception.LoginFailedException;

import com.google.gson.JsonObject;

public class LoginController extends Controller {

	@Override
	public JsonObject handle(String[] parts, JsonObject request) throws ControllerException {
		String username = getStringValue(request, "username");
		User user = DBUserUtility.loadUser(username);

		if (user == null) {
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
		
		
	}

}
