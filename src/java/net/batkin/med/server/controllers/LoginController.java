package net.batkin.med.server.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import net.batkin.med.server.controllers.exception.LoginFailedException;
import net.batkin.med.server.db.dataModel.User;
import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.http.JsonController;
import net.batkin.med.server.http.RequestContext;
import net.batkin.med.server.http.Controller.ControllerMapping;
import net.batkin.med.server.http.Controller.RequestMethod;
import net.batkin.med.server.json.request.ClientRequest;
import net.batkin.med.server.json.request.LoginRequest;
import net.batkin.med.server.json.response.LoginSuccessResponse;
import net.batkin.med.server.session.SessionManager;
import net.batkin.med.server.util.ConfigBuilder;

import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

@ControllerMapping(prefix = "login", requestMethods = { RequestMethod.POST })
public class LoginController extends JsonController {

	@Override
	public JsonObject handle(RequestContext context, JsonObject request) throws ControllerException {
		LoginRequest loginRequest = new LoginRequest(request);

		User user = User.loadUserByUsername(loginRequest.getUsername());

		if (user == null) {
			LoggerFactory.getLogger(LoginController.class).warn("User [" + loginRequest.getUsername() + "] not found");
			throw new LoginFailedException();
		}

		String userHost = ClientRequest.getStringValue(request, "userHost", "all");
		Map<String, List<String>> clientConfig = ConfigBuilder.buildConfigFromDatabase("client." + userHost, "client");
		String sessionId = SessionManager.getInstance().createSession(user);
		context.getResponse().addCookie(new Cookie("SessionId", sessionId));

		return new LoginSuccessResponse(user, clientConfig).toJson();
	}

}
