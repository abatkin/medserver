package net.batkin.forms.server.db.dataModel.schema.fields;

import net.batkin.forms.server.db.dataModel.schema.fields.FormField.FieldValidator;

public class BasicFieldValidator implements FieldValidator {

	private String functionName;
	private Object[] arguments;

	public BasicFieldValidator(String functionName, Object... arguments) {
		this.functionName = functionName;
		this.arguments = arguments;
	}

	@Override
	public String getFunctionName() {
		return functionName;
	}

	@Override
	public Object[] getArguments() {
		return arguments;
	}

}
