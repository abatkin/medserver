package net.batkin.forms.server.exception;

import net.batkin.forms.server.http.ErrorCodes;

public class ServerDataException extends ControllerException {

	public ServerDataException(String message) {
		super(ErrorCodes.ERROR_CODE_INVALID_SERVER_DATA, message);
	}
}
