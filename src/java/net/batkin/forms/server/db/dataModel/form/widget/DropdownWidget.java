package net.batkin.forms.server.db.dataModel.form.widget;

import java.util.Map;

import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.schema.fields.FieldValidationException;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;

public class DropdownWidget extends FormWidget<Integer> {

	private String selectOneText;
	private boolean selectOne;

	public DropdownWidget(BSONObject obj) throws ServerDataException {
		super(obj);
		selectOne = getBooleanValue(obj, "selectOne", Boolean.FALSE).booleanValue();
		selectOneText = getStringValue(obj, "selectOneText", "Select One...");
	}

	@Override
	public String getTemplateName() {
		return "dropdown";
	}

	public boolean getSelectOne() {
		return selectOne;
	}

	public String getSelectOneText() {
		return selectOneText;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FieldData<Integer> buildFieldData(FormField<?> field) {
		return new FieldData<Integer>((FormField<Integer>) field) {
			@Override
			public Object convertObject(Map<String, String[]> params) throws FieldValidationException {
				String stringData = getOneValue(params, getName());
				if (stringData == null || stringData.equals("")) {
					return null;
				}
				try {
					Integer integerValue = Integer.valueOf(stringData);
					return integerValue;
				} catch (Exception e) {
					throw new FieldValidationException("invalid value", field);
				}
			}

		};
	}
}
