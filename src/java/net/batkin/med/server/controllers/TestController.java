package net.batkin.med.server.controllers;

import net.batkin.med.server.db.dataModel.User;
import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.http.RequestContext;

import com.google.gson.JsonObject;

public class TestController extends LoggedInController {

	@Override
	public JsonObject handle(RequestContext context, User user, JsonObject request) throws ControllerException {
		JsonObject obj = new JsonObject();

		obj.addProperty("fullName", user.getFullName());

		return obj;
	}

}
