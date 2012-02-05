package net.batkin.forms.server.db.dataModel.form.widget;

import java.util.Map;

import org.bson.BSONObject;

import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.exception.ServerDataException;

public class IntegerTextWidget extends FormWidget<Integer, Integer> {

	public IntegerTextWidget(BSONObject obj) throws ServerDataException {
		super(obj);
	}

	@Override
	public FieldData<Integer, Integer> buildFieldData(FormField<Integer, Integer> field) {
		return new FieldData<Integer, Integer>(field) {
			@Override
			public void populateObject(Map<String, String> params) {
				String stringData = params.get(getName());
				if (stringData == null) {
					return;
				}
				try {
					nativeData = Integer.valueOf(stringData);
				} catch (Exception e) {
					setError("invalid integer");
				}
			}

		};
	}

	@Override
	public String getTemplateName() {
		return "textfield";
	}
}
