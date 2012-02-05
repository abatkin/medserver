package net.batkin.forms.server.db.dataModel.schema.fields;

import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;

public class IntegerField extends FormField<Integer> {

	private Integer defaultValue;
	private Integer min;
	private Integer max;

	public IntegerField(BSONObject obj) throws ServerDataException {
		super(obj, Integer.class);
		this.min = getIntegerValue(obj, "defaultValue", null);
		this.min = getIntegerValue(obj, "min", null);
		this.max = getIntegerValue(obj, "max", null);
	}

	@Override
	public String getDataTypeName() {
		return "integer";
	}

	@Override
	public Integer getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void addAdditionalData(BSONObject obj) {
		putValue(obj, "min", min);
		putValue(obj, "max", max);
	}

	@Override
	public Integer toNativeObject(Integer obj) {
		return (Integer) obj;
	}

	@Override
	public Integer fromNativeObject(Object obj) {
		return (Integer) obj;
	}

}
