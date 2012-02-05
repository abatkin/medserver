package net.batkin.forms.server.db.dataModel.schema.fields;

import java.util.Map;

import net.batkin.forms.server.db.dataModel.form.FieldData;

public abstract class SimpleFieldData<T> extends FieldData<T, T> {

	public SimpleFieldData(FormField<T, T> field) {
		super(field);
	}

	@Override
	public void populateObject(Map<String, String> params) {
		String stringValue = params.get(field.getFieldName());
		if (stringValue == null || stringValue.equals("")) {
			nativeData = field.getDefaultValue();
		} else {
			nativeData = convertData(stringValue);
		}
	}

	protected abstract T convertData(String stringValue);

}
