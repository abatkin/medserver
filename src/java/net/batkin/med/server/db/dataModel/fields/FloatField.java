package net.batkin.med.server.db.dataModel.fields;

import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;

public class FloatField extends FormField<Float> {

	private Float min;
	private Float max;

	protected FloatField(BSONObject obj) throws ServerDataException {
		super(obj);
		this.min = getOptionalFloatValue(obj, "min");
		this.max = getOptionalFloatValue(obj, "max");
	}

	@Override
	public String getDataTypeName() {
		return "float";
	}

	@Override
	public Float getDefaultValue() {
		return Float.valueOf(0.0f);
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
