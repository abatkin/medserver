package net.batkin.forms.server.db.dataModel.form.widget;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.NotImplementedException;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public abstract class FormWidget<DbType> extends DbDataModel {

	private String name; // Link to FormField
	private String title;
	private String helpText;
	private String widgetType; // Discriminator for FormWidget hierarchy

	protected FormWidget(BSONObject obj) throws ServerDataException {
		widgetType = getStringValue(obj, "widgetType");
		name = getStringValue(obj, "name");
		title = getStringValue(obj, "title");
		helpText = getStringValue(obj, "helpText", null);
	}

	@Override
	public ObjectId getObjectId() {
		throw new NotImplementedException();
	}

	@Override
	public DBObject toDbObject() {
		throw new NotImplementedException();
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public String getHelpText() {
		return helpText;
	}

	public String getWidgetType() {
		return widgetType;
	}

	public abstract String getTemplateName();

	public abstract FieldData<DbType> buildFieldData(FormField<?> field);

	@SuppressWarnings("unchecked")
	public static <DbType, NativeType> FormWidget<DbType> parseWidget(BSONObject widgetObj) throws ControllerException {
		String widgetType = getStringValue(widgetObj, "widgetType");
		return (FormWidget<DbType>) WidgetManager.getInstance().getWidget(widgetType, widgetObj);
	}

}
