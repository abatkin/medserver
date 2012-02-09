package net.batkin.forms.server.db.dataModel.schema.fields;

public class FieldValidationException extends Exception {

	private FormField<?> field;

	public FieldValidationException(String message, FormField<?> field) {
		super(message);
		this.field = field;
	}

	public FormField<?> getField() {
		return field;
	}
}
