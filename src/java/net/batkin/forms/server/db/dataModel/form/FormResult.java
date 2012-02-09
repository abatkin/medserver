package net.batkin.forms.server.db.dataModel.form;

import java.util.HashMap;
import java.util.Map;

import net.batkin.forms.server.exception.FormValidationException;

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

	public Integer getResultIntegetValue(String name, boolean required, Integer defaultValue) throws FormValidationException {
		return getResultObjectValue(name, required, defaultValue, Integer.class);
	}

	public String getResultStringValue(String name, boolean required, String defaultValue) throws FormValidationException {
		return getResultObjectValue(name, required, defaultValue, String.class);
	}

	public <T> T getResultObjectValue(String name, boolean required, T defaultValue, Class<T> valueClass) throws FormValidationException {
		FieldData<?> fieldData = values.get(name);
		if (fieldData == null) {
			if (required) {
				throw new FormValidationException("required", name);
			} else {
				return defaultValue;
			}
		}

		Object objectValue = fieldData.getObject();
		if (objectValue == null) {
			if (required) {
				throw new FormValidationException("required", name);
			} else {
				return defaultValue;
			}
		}

		if (valueClass.isInstance(objectValue)) {
			return valueClass.cast(objectValue);
		} else {
			if (required) {
				throw new FormValidationException("required", name);
			} else {
				return defaultValue;
			}
		}
	}
}
