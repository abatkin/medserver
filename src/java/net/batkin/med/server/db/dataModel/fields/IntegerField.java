package net.batkin.med.server.db.dataModel.fields;

import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;

public class IntegerField extends FormField<Integer> {

	private Integer min;
	private Integer max;

	protected IntegerField(BSONObject obj) throws ServerDataException {
		super(obj);
		this.min = getOptionalIntegerValue(obj, "min");
		this.max = getOptionalIntegerValue(obj, "max");
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
		if (min != null) {
			obj.put("min", min);
		}
		if (max != null) {
			obj.put("max", max);
		}
	}

}
