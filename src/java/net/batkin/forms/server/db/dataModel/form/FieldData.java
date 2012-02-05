package net.batkin.forms.server.db.dataModel.form;

import java.util.Map;

import net.batkin.forms.server.db.dataModel.schema.fields.FormField;

public abstract class FieldData<DbType, NativeType> {

	protected NativeType nativeData;
	protected FormField<DbType, NativeType> field;
	private String error;

	public FieldData(FormField<DbType, NativeType> field) {
		this.field = field;
	}

	protected void setError(String message) {
		this.error = message;
	}

	public String getError() {
		return error;
	}

	public NativeType getObject() {
		return nativeData;
	}

	public abstract void populateObject(Map<String, String> params);
}
