package net.batkin.forms.server.db.dataModel.fields;

import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;

public class IntegerField extends FormField<Integer> {

	private Integer min;
	private Integer max;

	protected IntegerField(BSONObject obj) throws ServerDataException {
		super(obj);
		this.min = getIntegerValue(obj, "min", null);
		this.max = getIntegerValue(obj, "max", null);
	}

	@Override
	public String getDataTypeName() {
		return "integer";
	}

	@Override
	public Integer getDefaultValue() {
		return Integer.valueOf(0);
	}

	@Override
	public void addAdditionalData(BSONObject obj) {
		putValue(obj, "min", min);
		putValue(obj, "max", max);
	}

}
