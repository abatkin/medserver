package net.batkin.forms.server.db.dataModel.form.widget;

import java.util.Map;

import org.bson.BSONObject;

import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.schema.fields.FieldValidationException;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.exception.ServerDataException;

public class CheckboxWidget extends FormWidget<Boolean> {

	public CheckboxWidget(BSONObject obj) throws ServerDataException {
		super(obj);
	}

	@Override
	public String getTemplateName() {
		return "checkbox";
	}

	@SuppressWarnings("unchecked")
	@Override
	public FieldData<Boolean> buildFieldData(FormField<?> field) {
		return new FieldData<Boolean>((FormField<Boolean>) field) {
			@Override
			public Object convertObject(Map<String, String[]> params) throws FieldValidationException {
				String stringData = getOneValue(params, getName());
				if (stringData == null) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(!stringData.isEmpty());
			}
		};
	}
}
