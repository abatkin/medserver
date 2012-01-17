package net.batkin.med.server.db.dataModel.fields;

import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;

public class StringField extends FormField<String> {

	public enum SubType {
		plain, multiline, url, email
	}

	private String defaultValue;
	private int maxLength;
	private SubType subType;

	private int width;
	private int height;

	protected StringField(BSONObject obj) throws ServerDataException {
		super(obj);
		this.defaultValue = getStringValue(obj, "defaultValue");
		this.maxLength = getIntegerValue(obj, "maxLength");

		String subTypeString = getStringValue(obj, "subType");
		try {
			this.subType = SubType.valueOf(subTypeString);
		} catch (Exception e) {
			throw new ServerDataException("Invalid String subType");
		}

		if (subType == SubType.multiline) {
			this.width = getIntegerValue(obj, "width");
			this.height = getIntegerValue(obj, "height");
		}
	}

	@Override
	public void addAdditionalData(BSONObject obj) {
		obj.put("maxLength", Integer.valueOf(maxLength));
		obj.put("subType", subType.toString());

		if (subType == SubType.multiline) {
			obj.put("width", Integer.valueOf(width));
			obj.put("height", Integer.valueOf(height));
		}
	}

	@Override
	public String getDataTypeName() {
		return "string";
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public SubType getSubType() {
		return subType;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
