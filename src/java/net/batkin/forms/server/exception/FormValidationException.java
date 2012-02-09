package net.batkin.forms.server.exception;

public class FormValidationException extends Exception {

	private String fieldName;

	public FormValidationException(String message, String fieldName) {
		super(message);
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}
}
