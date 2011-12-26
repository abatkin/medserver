package net.batkin.med.server.exception;

import net.batkin.med.server.http.ErrorCodes;

public class ServerDataException extends ControllerException {

	public ServerDataException(String message) {
		super(ErrorCodes.ERROR_CODE_INVALID_SERVER_DATA, message);
	}
}
