package net.batkin.forms.server.db.dataModel.form.widget;

import java.util.Map;

import org.bson.BSONObject;

import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.exception.ServerDataException;

public class FloatTextWidget extends FormWidget<Float, Float> {

	public FloatTextWidget(BSONObject obj) throws ServerDataException {
		super(obj);
	}

	@Override
	public FieldData<Float, Float> buildFieldData(FormField<Float, Float> field) {
		return new FieldData<Float, Float>(field) {
			@Override
			public void populateObject(Map<String, String> params) {
				String stringData = params.get(getName());
				if (stringData == null) {
					return;
				}
				try {
					nativeData = Float.valueOf(stringData);
				} catch (Exception e) {
					setError("invalid floating point number");
				}
			}

		};
	}

	@Override
	public String getTemplateName() {
		return "textfield";
	}
}
