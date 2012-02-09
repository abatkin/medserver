package net.batkin.forms.server.controllers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.batkin.forms.server.db.dataModel.action.Action;
import net.batkin.forms.server.db.dataModel.action.ActionData;
import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.form.FormLayout;
import net.batkin.forms.server.db.dataModel.form.FormResult;
import net.batkin.forms.server.db.dataModel.form.widget.FormWidget;
import net.batkin.forms.server.db.dataModel.schema.FormSchema;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.FileNotFoundException;
import net.batkin.forms.server.http.Controller;
import net.batkin.forms.server.http.Controller.ControllerMapping;
import net.batkin.forms.server.http.Controller.RequestMethod;
import net.batkin.forms.server.http.RequestContext;
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
			FormResult result = buildEmptyFormResult(layout, schema);
			showForm(context, layout, schema, result);
		} else if (action.equals("submit")) {
			saveForm(layout, schema, context);
		} else {
			throw new FileNotFoundException();
		}
	}

	private FormResult buildEmptyFormResult(FormLayout layout, FormSchema schema) {
		FormResult result = new FormResult();

		for (FormWidget<?> widget : layout.getAllWidgets()) {
			String fieldName = widget.getName();
			FormField<?> field = schema.getField(fieldName);
			FieldData<?> fieldData = widget.buildFieldData(field);
			fieldData.setDefault();
			result.addValue(fieldName, fieldData);
		}

		return result;
	}

	private void showForm(RequestContext context, FormLayout layout, FormSchema schema, FormResult result) throws IOException {
		TemplateParameters params = new TemplateParameters();
		params.add("layout", layout);
		params.add("schema", schema);
		params.add("result", result);
		sendHtmlResponse(context, "/show", params);
	}

	private void saveForm(FormLayout layout, FormSchema schema, RequestContext context) throws IOException {
		HttpServletRequest request = context.getRequest();
		Map<String, String[]> parameterMap = request.getParameterMap();

		FormResult result = new FormResult();

		boolean success = true;
		for (FormWidget<?> widget : layout.getAllWidgets()) {
			String fieldName = widget.getName();
			FormField<?> field = schema.getField(fieldName);
			FieldData<?> fieldData = widget.buildFieldData(field);
			if (!fieldData.populateObject(parameterMap)) {
				success = false;
			}
			result.addValue(fieldName, fieldData);
		}

		if (success) {
			ActionData data = new ActionData(schema, layout, result);
			for (Action action : layout.getActions()) {
				action.process(data);
			}

			TemplateParameters params = new TemplateParameters();
			params.add("layout", layout);
			params.add("schema", schema);
			params.add("result", result);
			params.add("message", "Your information have been saved");
			sendHtmlResponse(context, "/saved", params);
		} else {
			showForm(context, layout, schema, result);
		}
	}
}
