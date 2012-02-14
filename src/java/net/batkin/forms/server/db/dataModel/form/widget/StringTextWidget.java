package net.batkin.forms.server.db.dataModel.form.widget;

import java.util.Map;

import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.schema.fields.FieldValidationException;
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
				String stringValue = getOneValue(params, getName());
				if (stringValue != null) {
					return stringValue.trim();
				}
				return null;
			}

			@Override
			public void validate() throws FieldValidationException {
				super.validate();

				if (field.getRequired() && "".equals(nativeData)) {
					throw new FieldValidationException("required", field);
				}
			}
		};
	}

	@Override
	public String getTemplateName() {
		return "textfield";
	}
}
