package net.batkin.forms.server.controllers;

import java.io.IOException;

import net.batkin.forms.server.db.dataModel.form.FormLayout;
import net.batkin.forms.server.db.dataModel.schema.FormSchema;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.FileNotFoundException;
import net.batkin.forms.server.http.Controller;
import net.batkin.forms.server.http.RequestContext;
import net.batkin.forms.server.http.Controller.ControllerMapping;
import net.batkin.forms.server.http.Controller.RequestMethod;
import net.batkin.forms.server.http.velocity.TemplateParameters;

@ControllerMapping(prefix = "form", requestMethods = { RequestMethod.GET, RequestMethod.POST })
public class FormsController extends Controller {

	@Override
	public void handle(RequestContext context) throws ControllerException, IOException {
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
			throw new FileNotFoundException();
		}

		String schemaName = layout.getSchemaName();
		FormSchema schema = FormSchema.loadByName(schemaName);
		if (schema == null) {
			throw new FileNotFoundException();
		}

		if (action.equals("show")) {
			TemplateParameters params = new TemplateParameters();
			params.add("layout", layout);
			params.add("schema", schema);
			sendHtmlResponse(context, "/show", params);
		} else {
			throw new FileNotFoundException();
		}
	}

}
