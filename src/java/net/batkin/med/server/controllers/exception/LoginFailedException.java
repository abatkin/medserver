package net.batkin.med.server.controllers.exception;

import javax.servlet.http.HttpServletResponse;

import net.batkin.med.server.ErrorCodes;
import net.batkin.med.server.exception.ControllerException;

public class LoginFailedException extends ControllerException {

	public LoginFailedException() {
		super(ErrorCodes.ERROR_CODE_LOGIN_FAILED, "Login Failed");
	}

	@Override
	public int getHttpCode() {
		return HttpServletResponse.SC_BAD_REQUEST;
	}

}
