package net.batkin.forms.server.db.dataModel.controllerHandler;

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
import net.batkin.forms.server.exception.ServerDataException;
import net.batkin.forms.server.http.Controller;
import net.batkin.forms.server.http.RequestContext;
import net.batkin.forms.server.http.velocity.TemplateParameters;

import org.bson.BSONObject;
import org.slf4j.LoggerFactory;

public class SubmitFormControllerHandler extends ControllerHandler {

	public SubmitFormControllerHandler(BSONObject obj) throws ServerDataException {
		super(obj);
	}

	public SubmitFormControllerHandler() {
		super("submit");
	}

	@Override
	public String getUrlSuffix() {
		return "submit";
	}

	@Override
	public void handle(RequestContext context, FormSchema schema, FormLayout layout) throws ControllerException, IOException {
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

		outer: if (success) {
			ActionData data = new ActionData(schema, layout, result);
			for (Action action : layout.getActions()) {
				try {
					action.process(data);
				} catch (Exception e) {
					// TODO: Show error
					LoggerFactory.getLogger(SubmitFormControllerHandler.class).warn("Error saving data: " + e.getMessage(), e);
					break outer;
				}
			}

			TemplateParameters params = new TemplateParameters();
			params.add("layout", layout);
			params.add("schema", schema);
			params.add("result", result);
			params.add("message", "Your information have been saved");
			Controller.sendHtmlResponse(context, "/saved", params);
			return;
		}

		ShowFormControllerHandler.showForm(context, layout, schema, result);

	}

}
