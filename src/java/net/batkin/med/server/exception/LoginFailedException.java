package net.batkin.med.server.exception;

import javax.servlet.http.HttpServletResponse;

import net.batkin.med.server.ErrorCodes;

public class LoginFailedException extends ControllerException {

	public LoginFailedException() {
		super(ErrorCodes.ERROR_CODE_LOGIN_FAILED, "Login Failed");
	}

	@Override
	public int getHttpCode() {
		return HttpServletResponse.SC_BAD_REQUEST;
	}

}
