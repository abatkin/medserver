package net.batkin.med.server.http;

import java.io.IOException;
import java.io.Reader;

import net.batkin.med.server.exception.ControllerException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class JsonController extends Controller {

	public abstract JsonObject handle(RequestContext context, JsonObject request) throws ControllerException;

	@Override
	public void handle(RequestContext context) throws ControllerException {
		context.getResponse().setContentType("application/json");

		try {
			JsonObject jsonRequest = readRequest(context);
			JsonObject obj = handle(context, jsonRequest);
			if (obj == null) {
				obj = buildSuccessResponse();
			}
			sendResponse(context, obj);
		} catch (IOException e) {
			throw new ControllerException(ErrorCodes.ERROR_CODE_UNKNOWN, e.getMessage(), e);
		}
	}

	public JsonObject buildSuccessResponse() {
		return buildGenericResponse(true, null, 0);
	}

	public JsonObject readRequest(RequestContext context) throws IOException {
		Reader reader = context.getRequest().getReader();

		if (reader != null) {
			JsonParser parser = new JsonParser();
			JsonElement el = parser.parse(reader);
			if (el != null && el.isJsonObject()) {
				return el.getAsJsonObject();
			}
		}
		return null;
	}

}
