package net.batkin.forms.server.db.dataModel.form.widget;

import java.util.Map;

import org.bson.BSONObject;

import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.exception.ServerDataException;

public class StringTextWidget extends FormWidget<String, String> {

	public StringTextWidget(BSONObject obj) throws ServerDataException {
		super(obj);
	}

	@Override
	public FieldData<String, String> buildFieldData(FormField<String, String> field) {
		return new FieldData<String, String>(field) {
			@Override
			public void populateObject(Map<String, String> params) {
				nativeData = params.get(getName());
			}
		};
	}

	@Override
	public String getTemplateName() {
		return "textfield";
	}
}
