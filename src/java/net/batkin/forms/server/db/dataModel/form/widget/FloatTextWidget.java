package net.batkin.forms.server.db.dataModel.form.widget;

import java.util.Map;

import org.bson.BSONObject;

import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.schema.fields.FieldValidationException;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.exception.ServerDataException;

public class FloatTextWidget extends FormWidget<Float> {

	public FloatTextWidget(BSONObject obj) throws ServerDataException {
		super(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public FieldData<Float> buildFieldData(FormField<?> field) {
		return new FieldData<Float>((FormField<Float>) field) {

			@Override
			public Float convertObject(Map<String, String[]> params) throws FieldValidationException {
				String[] values = params.get(getName());
				if (values == null || values.length == 0) {
					return null;
				}

				String stringData = values[0];
				try {
					return Float.valueOf(stringData);
				} catch (Exception e) {
					throw new FieldValidationException("invalid floating point number");
				}
			}
		};
	}

	@Override
	public String getTemplateName() {
		return "textfield";
	}
}
