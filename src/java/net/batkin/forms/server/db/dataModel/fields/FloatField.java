package net.batkin.forms.server.db.dataModel.fields;

import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;

public class FloatField extends FormField<Float> {

	private Float min;
	private Float max;

	protected FloatField(BSONObject obj) throws ServerDataException {
		super(obj);
		this.min = getFloatValue(obj, "min", null);
		this.max = getFloatValue(obj, "max", null);
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
		putValue(obj, "min", min);
		putValue(obj, "max", max);
	}

}
