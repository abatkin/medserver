package net.batkin.med.server.controllers.exception;

import javax.servlet.http.HttpServletResponse;

import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.http.ErrorCodes;

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
