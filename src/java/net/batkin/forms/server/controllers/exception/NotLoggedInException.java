package net.batkin.forms.server.controllers.exception;

import javax.servlet.http.HttpServletResponse;

import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.http.ErrorCodes;

public class NotLoggedInException extends ControllerException {
	public NotLoggedInException() {
		super(ErrorCodes.ERROR_CODE_NOT_LOGGED_IN, "Not Logged In");
	}

	@Override
	public int getHttpCode() {
		return HttpServletResponse.SC_UNAUTHORIZED;
	}

	@Override
	public boolean shouldLogStackTrace() {
		return false;
	}
}
