package net.batkin.med.server.json.request;

import net.batkin.med.server.exception.ControllerException;

import com.google.gson.JsonObject;

public class UpdateUserRequest extends ClientRequest {

	private String fullName;
	private String username;

	public UpdateUserRequest(JsonObject request) throws ControllerException {
		this.username = getStringValue(request, "username");
		this.fullName = getStringValue(request, "fullName");
	}

	public String getFullName() {
		return fullName;
	}

	public Object getUsername() {
		return username;
	}

}
