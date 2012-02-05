package net.batkin.forms.server.db.dataModel.form.widget;

import java.util.Map;

import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;

public class StringTextWidget extends FormWidget<String> {

	public StringTextWidget(BSONObject obj) throws ServerDataException {
		super(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public FieldData<String> buildFieldData(FormField<?> field) {

		return new FieldData<String>((FormField<String>) field) {

			@Override
			public String convertObject(Map<String, String[]> params) {
				String[] values = params.get(getName());
				if (values != null && values.length > 0) {
					return values[0];
				}
				return null;
			}

		};
	}

	@Override
	public String getTemplateName() {
		return "textfield";
	}
}
