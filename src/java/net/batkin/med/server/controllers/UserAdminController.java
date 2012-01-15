package net.batkin.med.server.controllers;

import java.util.List;
import java.util.Set;

import net.batkin.med.server.db.dataModel.User;
import net.batkin.med.server.db.utility.DBAccess.DatabaseCollection;
import net.batkin.med.server.exception.ClientRequestException;
import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.exception.FileNotFoundException;
import net.batkin.med.server.exception.ServerDataException;
import net.batkin.med.server.http.ErrorCodes;
import net.batkin.med.server.http.RequestContext;
import net.batkin.med.server.http.Controller.ControllerMapping;
import net.batkin.med.server.http.Controller.RequestMethod;
import net.batkin.med.server.json.request.UpdateUserPermissionsRequest;
import net.batkin.med.server.json.request.UpdateUserRequest;
import net.batkin.med.server.json.response.UserDetailResponse;
import net.batkin.med.server.json.response.UserListResponse;

import com.google.gson.JsonObject;

@ControllerMapping(prefix = "users", requestMethods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
public class UserAdminController extends LoggedInController {

	@Override
	public JsonObject handle(RequestContext context, User user, JsonObject request) throws ControllerException {
		ensurePermission(user, Permissions.Admin);

		switch (context.getRequestMethod()) {
		case GET:
			return doGet(context, user);
		case POST:
			return doPost(context, user, request);
		case DELETE:
			return doDelete(context, user, request);
		default:
			throw new FileNotFoundException();
		}
	}

	private JsonObject doPost(RequestContext context, User user, JsonObject request) throws ControllerException {
		String[] parts = context.getParts();
		if (parts.length == 3) {
			String username = parts[1];
			String action = parts[2];

			if (action.equals("updateInfo")) {
				return updateUserInfo(username, request);
			} else if (action.equals("updatePermissions")) {
				return updatePermissions(username, request);
			}
		}
		throw new FileNotFoundException();
	}

	private JsonObject updatePermissions(String username, JsonObject request) throws ControllerException {
		UpdateUserPermissionsRequest uupRequest = new UpdateUserPermissionsRequest(request);
		if (username.equals(uupRequest.getUsername())) {
			throw new ClientRequestException("URI username and request username mismatch");
		}

		User user = User.loadUserByUsername(username);
		if (user == null) {
			throw new FileNotFoundException();
		}

		String addPermission = uupRequest.getAddPermission();
		String removePermission = uupRequest.getRemovePermission();
		Set<String> permissions = user.getPermissions();
		if (addPermission != null) {
			permissions.add(addPermission);
		}
		if (removePermission != null) {
			permissions.remove(removePermission);
		}

		DatabaseCollection.Users.saveObject(user);

		return new UserDetailResponse(user).toJson();
	}

	private JsonObject updateUserInfo(String username, JsonObject request) throws ControllerException {
		UpdateUserRequest urRequest = new UpdateUserRequest(request);
		if (username.equals(urRequest.getUsername())) {
			throw new ClientRequestException("URI username and request username mismatch");
		}

		User user = User.loadUserByUsername(username);
		if (user != null) {
			user.setFullName(urRequest.getFullName());
			DatabaseCollection.Users.saveObject(user);
		} else {
			user = User.createAndSaveNewUser(username, urRequest.getFullName());
		}

		return new UserDetailResponse(user).toJson();
	}

	private JsonObject doDelete(RequestContext context, User user, JsonObject request) throws ControllerException {
		String[] parts = context.getParts();
		if (parts.length == 2) {
			String username = parts[1];
			return deleteUser(username);
		}
		throw new FileNotFoundException();
	}

	private JsonObject deleteUser(String username) throws ServerDataException {
		User user = User.loadUserByUsername(username);
		if (user == null) {
			return buildGenericResponse(false, "User [" + username + "] not found", ErrorCodes.ERROR_CODE_USER_NOT_FOUND);
		}
		DatabaseCollection.Users.deleteObject(user);
		return buildSuccessResponse();
	}

	private JsonObject doGet(RequestContext context, User user) throws ControllerException {
		String[] parts = context.getParts();
		if (parts.length == 1) {
			return listUsers();
		} else {
			String username = parts[1];
			return showUser(username);
		}
	}

	private JsonObject showUser(String username) throws ControllerException {
		User user = User.loadUserByUsername(username);
		if (user == null) {
			throw new FileNotFoundException();
		}
		return new UserDetailResponse(user).toJson();
	}

	private JsonObject listUsers() {
		List<User> users = User.listUsers();
		return new UserListResponse(users).toJson();
	}

}
