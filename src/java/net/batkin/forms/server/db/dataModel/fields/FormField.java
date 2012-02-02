package net.batkin.forms.server.db.dataModel.fields;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public abstract class FormField<T> extends DbDataModel {

	private String fieldName;
	private String displayName;
	private String helpText;

	protected FormField(BSONObject obj) throws ServerDataException {
		this.fieldName = getStringValue(obj, "name");
		this.displayName = getStringValue(obj, "displayName");
		this.helpText = getStringValue(obj, "helpText");
	}

	@Override
	public DBObject toDbObject() {
		BasicDBObject obj = new BasicDBObject();
		putValue(obj, "name", fieldName);
		putValue(obj, "displayName", displayName);
		putValue(obj, "helpText", helpText);
		putValue(obj, "defaultValue", getDefaultValue());
		putValue(obj, "dataType", getDataTypeName());
		addAdditionalData(obj);
		return obj;
	}

	@Override
	public ObjectId getObjectId() {
		throw new RuntimeException("FormFields do not have ObjectIds");
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getHelpText() {
		return helpText;
	}
	
	public abstract String getDataTypeName();

	public abstract T getDefaultValue();

	public abstract void addAdditionalData(BSONObject obj);

	public static FormField<?> parseField(BSONObject fieldObj) throws ServerDataException {
		String dataType = getStringValue(fieldObj, "dataType");
		if (dataType.equals("string")) {
			return new StringField(fieldObj);
		} else if (dataType.equals("integer")) {
			return new IntegerField(fieldObj);
		} else if (dataType.equals("float")) {
			return new FloatField(fieldObj);
		} else {
			throw new ServerDataException("Unknown field type " + dataType);
		}
	}
}
