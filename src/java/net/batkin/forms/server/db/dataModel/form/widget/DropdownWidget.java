package net.batkin.forms.server.db.dataModel.form.widget;

import java.util.Map;

import org.bson.BSONObject;

import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.schema.fields.FieldValidationException;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.exception.ServerDataException;

public class DropdownWidget extends FormWidget<Integer> {

	public DropdownWidget(BSONObject obj) throws ServerDataException {
		super(obj);
	}

	@Override
	public String getTemplateName() {
		return "dropdown";
	}

	@SuppressWarnings("unchecked")
	@Override
	public FieldData<Integer> buildFieldData(FormField<?> field) {
		return new FieldData<Integer>((FormField<Integer>) field) {
			@Override
			public Object convertObject(Map<String, String[]> params) throws FieldValidationException {
				String stringData = getOneValue(params, getName());
				if (stringData == null) {
					return null;
				}
				try {
					return Integer.valueOf(stringData);
				} catch (Exception e) {
					throw new FieldValidationException("invalid value", field);
				}

			}
		};
	}
}
