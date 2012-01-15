package net.batkin.med.server.controllers;

import javax.servlet.http.Cookie;

import net.batkin.med.server.controllers.exception.InvalidSessionException;
import net.batkin.med.server.controllers.exception.NotLoggedInException;
import net.batkin.med.server.db.dataModel.User;
import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.http.JsonController;
import net.batkin.med.server.http.RequestContext;
import net.batkin.med.server.session.SessionManager;

import com.google.gson.JsonObject;

public abstract class LoggedInController extends JsonController {

	@Override
	public JsonObject handle(RequestContext context, JsonObject request) throws ControllerException {
		String sessionId = getSessionId(context);
		if (sessionId == null) {
			throw new NotLoggedInException();
		}

		User user = SessionManager.getInstance().validateSession(sessionId);
		if (user == null) {
			throw new InvalidSessionException();
		}

		return handle(context, user, request);
	}

	private String getSessionId(RequestContext context) {
		Cookie[] cookies = context.getRequest().getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("SessionId")) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public abstract JsonObject handle(RequestContext context, User user, JsonObject request) throws ControllerException;

}
