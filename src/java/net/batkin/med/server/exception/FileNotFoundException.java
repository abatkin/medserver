package net.batkin.med.server.exception;

import javax.servlet.http.HttpServletResponse;

import net.batkin.med.server.ErrorCodes;

public class FileNotFoundException extends ControllerException {

	public FileNotFoundException() {
		super(ErrorCodes.ERROR_CODE_NOT_FOUND, "No Controller Found");
	}

	@Override
	public int getHttpCode() {
		return HttpServletResponse.SC_NOT_FOUND;
	}

}
