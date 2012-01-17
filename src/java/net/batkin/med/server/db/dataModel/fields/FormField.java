package net.batkin.med.server.db.dataModel.fields;

import net.batkin.med.server.db.dataModel.DbDataModel;
import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public abstract class FormField<T> extends DbDataModel {

	private ObjectId objectId;
	private String fieldName;
	private String displayName;
	private String helpText;
	private boolean isSearchable;

	protected FormField(BSONObject obj) throws ServerDataException {
		this.objectId = getObjectIdValue(obj, "_id");
		this.fieldName = getStringValue(obj, "name");
		this.displayName = getStringValue(obj, "displayName");
		this.helpText = getStringValue(obj, "helpText");
		this.isSearchable = getBooleanValue(obj, "searchable");
	}

	@Override
	public DBObject toDbObject() {
		BasicDBObject obj = new BasicDBObject();
		obj.append("_id", objectId);
		obj.append("name", fieldName);
		obj.append("displayName", displayName);
		obj.append("helpText", helpText);
		obj.append("defaultValue", getDefaultValue());
		obj.append("dataType", getDataTypeName());
		addAdditionalData(obj);
		return obj;
	}

	@Override
	public ObjectId getObjectId() {
		return objectId;
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

	public boolean isSearchable() {
		return isSearchable;
	}

	public abstract String getDataTypeName();

	public abstract T getDefaultValue();

	public abstract void addAdditionalData(BSONObject obj);

	public static FormField<?> parseField(BSONObject fieldObj) throws ServerDataException {
		String dataType = getStringValue(fieldObj, "dataType");
		if (dataType.equals("string")) {
			return new StringField(fieldObj);
		} else {
			throw new ServerDataException("Unknown field type " + dataType);
		}
	}
}
