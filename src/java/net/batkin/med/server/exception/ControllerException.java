package net.batkin.med.server.exception;

import javax.servlet.http.HttpServletResponse;

public class ControllerException extends Exception {

	private int applicationCode;

	public ControllerException(int applicationCode, String message) {
		super(message);
		this.applicationCode = applicationCode;
	}

	public ControllerException(int applicationCode, String message, Throwable error) {
		super(message, error);
		this.applicationCode = applicationCode;
	}

	public int getApplicationCode() {
		return applicationCode;
	}

	public int getHttpCode() {
		return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	}

	public boolean shouldLogStackTrace() {
		return true;
	}
}
