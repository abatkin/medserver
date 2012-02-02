package net.batkin.forms.server.db.dataModel.schema.fields;

import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;

public class StringField extends FormField<String> {

	public enum SubType {
		plain, multiline, url, email
	}

	private String defaultValue;
	private Integer maxLength;
	private SubType subType;

	private Integer width;
	private Integer height;

	protected StringField(BSONObject obj) throws ServerDataException {
		super(obj);
		this.defaultValue = getStringValue(obj, "defaultValue", "");
		this.maxLength = getIntegerValue(obj, "maxLength", null);

		String subTypeString = getStringValue(obj, "subType", "plain");
		try {
			this.subType = SubType.valueOf(subTypeString);
		} catch (Exception e) {
			throw new ServerDataException("Invalid String subType");
		}

		if (subType == SubType.multiline) {
			this.width = getIntegerValue(obj, "width", null);
			this.height = getIntegerValue(obj, "height", null);
		}
	}

	@Override
	public void addAdditionalData(BSONObject obj) {
		putValue(obj, "maxLength", maxLength);
		putValue(obj, "subType", subType.toString());

		if (subType == SubType.multiline) {
			putValue(obj, "width", width);
			putValue(obj, "height", height);
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

	public Integer getMaxLength() {
		return maxLength;
	}

	public SubType getSubType() {
		return subType;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}
}
