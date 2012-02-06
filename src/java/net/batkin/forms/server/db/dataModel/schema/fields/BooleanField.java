package net.batkin.forms.server.db.dataModel.schema.fields;

import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;

public class BooleanField extends FormField<Boolean> {

	private Boolean defaultValue;

	public BooleanField(BSONObject obj) throws ServerDataException {
		super(obj, Boolean.class);
		this.defaultValue = getBooleanValue(obj, "defaultValue", Boolean.FALSE);
	}

	@Override
	public void addAdditionalData(BSONObject obj) {
		putValue(obj, "defaultValue", defaultValue);
	}

	@Override
	public String getDataTypeName() {
		return "boolean";
	}

	@Override
	public Boolean getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Boolean toNativeObject(Boolean obj) {
		return obj;
	}

	@Override
	public Boolean fromNativeObject(Object obj) {
		return (Boolean) obj;
	}
}
