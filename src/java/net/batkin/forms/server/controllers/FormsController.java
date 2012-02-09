package net.batkin.forms.server.controllers;

import java.io.IOException;

import net.batkin.forms.server.db.dataModel.controllerHandler.ControllerHandler;
import net.batkin.forms.server.db.dataModel.form.FormLayout;
import net.batkin.forms.server.db.dataModel.schema.FormSchema;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.FileNotFoundException;
import net.batkin.forms.server.http.Controller;
import net.batkin.forms.server.http.Controller.ControllerMapping;
import net.batkin.forms.server.http.Controller.RequestMethod;
import net.batkin.forms.server.http.RequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerMapping(prefix = "form", requestMethods = { RequestMethod.GET, RequestMethod.POST })
public class FormsController extends Controller {

	@Override
	public void handle(RequestContext context) throws ControllerException, IOException {
		Logger logger = LoggerFactory.getLogger(FormsController.class);

		String[] parts = context.getParts();
		String action = "show";
		String formName;
		if (parts.length > 1) {
			formName = parts[1];
			if (parts.length > 2) {
				action = parts[2];
			}
		} else {
			throw new FileNotFoundException();
		}

		FormLayout layout = FormLayout.loadByName(formName);
		if (layout == null) {
			logger.warn("Layout not found (formName=" + formName + ")");
			throw new FileNotFoundException();
		}

		String schemaName = layout.getSchemaName();
		FormSchema schema = FormSchema.loadByName(schemaName);
		if (schema == null) {
			logger.warn("Schema not found (formName=" + formName + ", schema=" + schemaName + ")");
			throw new FileNotFoundException();
		}

		ControllerHandler handler = layout.getControllerHandler(action);
		if (handler == null) {
			logger.warn("Handler not found (formName=" + formName + ", schema=" + schemaName + ", handler=" + action + ")");
			throw new FileNotFoundException();
		}

		handler.handle(context, schema, layout);
	}

}
