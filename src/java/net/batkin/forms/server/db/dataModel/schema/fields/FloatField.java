package net.batkin.forms.server.db.dataModel.schema.fields;

import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;

public class FloatField extends FormField<Float, Float> {

	private Float min;
	private Float max;

	public FloatField(BSONObject obj) throws ServerDataException {
		super(obj, Float.class);
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

	@Override
	public Float toNativeObject(Float obj) {
		return obj;
	}

	@Override
	public Float fromNativeObject(Float obj) {
		return obj;
	}

}
