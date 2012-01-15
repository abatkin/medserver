package net.batkin.med.server.exception;

import javax.servlet.http.HttpServletResponse;

import net.batkin.med.server.controllers.LoggedInController.Permissions;
import net.batkin.med.server.http.ErrorCodes;

public class ForbiddenException extends ControllerException {

	public ForbiddenException(String message) {
		super(ErrorCodes.ERROR_CODE_FORBIDDEN, message);
	}

	@Override
	public int getHttpCode() {
		return HttpServletResponse.SC_FORBIDDEN;
	}

	public static void missingPermission(Permissions permission) throws ForbiddenException {
		throw new ForbiddenException("Missing permission " + permission);
	}
}
