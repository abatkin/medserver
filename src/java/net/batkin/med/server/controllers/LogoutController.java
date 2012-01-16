package net.batkin.med.server.controllers;

import net.batkin.med.server.db.dataModel.User;
import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.http.RequestContext;
import net.batkin.med.server.http.Controller.ControllerMapping;
import net.batkin.med.server.http.Controller.RequestMethod;
import net.batkin.med.server.session.SessionManager;

import com.google.gson.JsonObject;

@ControllerMapping(prefix = "logout", requestMethods = { RequestMethod.POST })
public class LogoutController extends LoggedInController {

	@Override
	public JsonObject handle(RequestContext context, User user, JsonObject request) throws ControllerException {
		String sessionId = context.getSessionId();
		SessionManager.getInstance().invalidateSession(sessionId);
		return buildSuccessResponse();
	}

}
