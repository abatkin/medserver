package net.batkin.forms.server.controllers.exception;

import javax.servlet.http.HttpServletResponse;

import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.http.ErrorCodes;

public class InvalidSessionException extends ControllerException {
	public InvalidSessionException() {
		super(ErrorCodes.ERROR_CODE_INVALID_SESSION, "Invalid Session");
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
