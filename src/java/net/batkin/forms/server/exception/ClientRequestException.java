package net.batkin.forms.server.exception;

import javax.servlet.http.HttpServletResponse;

import net.batkin.forms.server.http.ErrorCodes;

public class ClientRequestException extends ControllerException {

	public ClientRequestException(String message) {
		super(ErrorCodes.ERROR_CODE_INVALID_CLIENT_REQUEST, message);
	}

	@Override
	public int getHttpCode() {
		return HttpServletResponse.SC_BAD_REQUEST;
	}

}
