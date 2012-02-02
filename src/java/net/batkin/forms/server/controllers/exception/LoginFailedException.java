package net.batkin.forms.server.controllers.exception;

import javax.servlet.http.HttpServletResponse;

import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.http.ErrorCodes;

public class LoginFailedException extends ControllerException {

	public LoginFailedException() {
		super(ErrorCodes.ERROR_CODE_LOGIN_FAILED, "Login Failed");
	}

	@Override
	public int getHttpCode() {
		return HttpServletResponse.SC_BAD_REQUEST;
	}

	@Override
	public boolean shouldLogStackTrace() {
		return false;
	}
}
