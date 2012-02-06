package net.batkin.forms.server.db.dataModel.schema.fields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

public class SelectFromOptionsField extends FormField<Integer> {

	private Integer defaultValue;

	private List<OptionValue> optionValues;
	private Map<Integer, OptionValue> valueMap;

	public SelectFromOptionsField(BSONObject obj) throws ServerDataException {
		super(obj, Integer.class);

		this.defaultValue = getIntegerValue(obj, "defaultValue", null);
		this.optionValues = new ArrayList<OptionValue>();
		this.valueMap = new HashMap<Integer, OptionValue>();

		for (BSONObject optionObj : getArrayValue(obj, "optionValues", BSONObject.class)) {
			Integer value = getIntegerValue(optionObj, "value");
			String displayText = getStringValue(optionObj, "displayText");
			OptionValue ov = new OptionValue(value, displayText);
			optionValues.add(ov);
			valueMap.put(value, ov);
		}
	}

	public List<OptionValue> getOptionValues() {
		return optionValues;
	}

	public Map<Integer, OptionValue> getValueMap() {
		return valueMap;
	}

	@Override
	public String getDataTypeName() {
		return "selectFromOptions";
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void addAdditionalData(BSONObject obj) {
		putValue(obj, "defaultValue", defaultValue);

		BasicBSONList valueList = new BasicBSONList();
		for (OptionValue ov : optionValues) {
			BSONObject ovObj = new BasicBSONObject();
			putValue(ovObj, "value", ov.getValue());
			putValue(ovObj, "displayText", ov.getDisplayText());
			valueList.add(ovObj);
		}
		putValue(obj, "optionValues", valueList);
	}

	@Override
	public Object toNativeObject(Integer obj) {
		return obj;
	}

	@Override
	public Integer fromNativeObject(Object obj) {
		return (Integer) obj;
	}

	public class OptionValue {
		private Integer value;
		private String displayText;

		public OptionValue(Integer value, String displayText) {
			this.value = value;
			this.displayText = displayText;
		}

		public Integer getValue() {
			return value;
		}

		public String getDisplayText() {
			return displayText;
		}
	}
}
