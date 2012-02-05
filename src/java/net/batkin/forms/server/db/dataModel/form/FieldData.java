package net.batkin.forms.server.db.dataModel.form;

import java.util.Map;

import net.batkin.forms.server.db.dataModel.schema.fields.FieldValidationException;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;

public abstract class FieldData<DbType> {

	private Object nativeData;
	protected FormField<DbType> field;
	private String error;

	public FieldData(FormField<DbType> field) {
		this.field = field;
	}

	public String getError() {
		return error;
	}

	public Object getObject() {
		return nativeData;
	}

	public abstract Object convertObject(Map<String, String[]> params) throws FieldValidationException;

	public boolean populateObject(Map<String, String[]> params) {
		try {
			nativeData = convertObject(params);
			if (nativeData != null) {
				validate();
			} else {
				setDefault();
			}
			return true;
		} catch (FieldValidationException e) {
			error = e.getMessage();
		} catch (Exception e) {
			error = "Invalid value";
		}
		return false;
	}

	public void setDefault() {
		nativeData = field.getDefaultValue();
	}

	public void validate() throws FieldValidationException {

	}
}
