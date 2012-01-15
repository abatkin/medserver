package net.batkin.med.server.json.request;

import net.batkin.med.server.controllers.exception.LoginFailedException;
import net.batkin.med.server.exception.ControllerException;

import com.google.gson.JsonObject;

public class LoginRequest extends ClientRequest {

	private String username;

	public LoginRequest(JsonObject request) throws ControllerException {
		if (request == null) {
			throw new LoginFailedException();
		}

		this.username = getStringValue(request, "username");
	}

	public String getUsername() {
		return username;
	}
}
