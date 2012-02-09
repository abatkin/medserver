package net.batkin.forms.server.db.dataModel.controllerHandler;

import java.io.IOException;

import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.form.FormLayout;
import net.batkin.forms.server.db.dataModel.form.FormResult;
import net.batkin.forms.server.db.dataModel.form.widget.FormWidget;
import net.batkin.forms.server.db.dataModel.schema.FormSchema;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.exception.ServerDataException;
import net.batkin.forms.server.http.Controller;
import net.batkin.forms.server.http.RequestContext;
import net.batkin.forms.server.http.velocity.TemplateParameters;

import org.bson.BSONObject;

public class ShowFormControllerHandler extends ControllerHandler {

	public ShowFormControllerHandler(BSONObject obj) throws ServerDataException {
		super(obj);
	}

	public ShowFormControllerHandler() {
		super("show");
	}

	@Override
	public String getUrlSuffix() {
		return "show";
	}

	@Override
	public void handle(RequestContext context, FormSchema schema, FormLayout layout) throws IOException {
		FormResult result = buildEmptyFormResult(layout, schema);
		showForm(context, layout, schema, result);

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

	public static void showForm(RequestContext context, FormLayout layout, FormSchema schema, FormResult result) throws IOException {
		TemplateParameters params = new TemplateParameters();
		params.add("layout", layout);
		params.add("schema", schema);
		params.add("result", result);
		Controller.sendHtmlResponse(context, "/show", params);
	}
}
