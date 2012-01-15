package net.batkin.med.server.json.request;

import net.batkin.med.server.exception.ControllerException;

import com.google.gson.JsonObject;

public class UpdateUserPermissionsRequest extends ClientRequest {

	private String username;
	private String addPermission;
	private String removePermission;

	public UpdateUserPermissionsRequest(JsonObject request) throws ControllerException {
		this.username = getStringValue(request, "username");
		this.addPermission = getStringValue(request, "addPermission", null);
		this.removePermission = getStringValue(request, "removePermission", null);
	}

	public String getUsername() {
		return username;
	}

	public String getAddPermission() {
		return addPermission;
	}

	public String getRemovePermission() {
		return removePermission;
	}

}
