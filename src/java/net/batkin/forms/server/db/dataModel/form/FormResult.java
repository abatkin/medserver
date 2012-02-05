package net.batkin.forms.server.db.dataModel.form;

import java.util.HashMap;
import java.util.Map;

public class FormResult {

	private boolean hasErrors;
	private Map<String, FieldData<?>> values;

	public FormResult() {
		values = new HashMap<String, FieldData<?>>();
	}

	public void addValue(String name, FieldData<?> value) {
		values.put(name, value);
		if (value.getError() != null) {
			hasErrors = true;
		}
	}

	public FieldData<?> getValue(String fieldName) {
		return values.get(fieldName);
	}

	public boolean hasErrors() {
		return hasErrors;
	}
}
