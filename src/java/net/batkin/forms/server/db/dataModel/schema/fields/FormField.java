package net.batkin.forms.server.db.dataModel.schema.fields;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public abstract class FormField<T> extends DbDataModel {

	private String fieldName;

	protected FormField(BSONObject obj) throws ServerDataException {
		this.fieldName = getStringValue(obj, "name");
	}

	@Override
	public DBObject toDbObject() {
		BasicDBObject obj = new BasicDBObject();
		putValue(obj, "name", fieldName);
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

	public abstract String getDataTypeName();

	public abstract T getDefaultValue();

	public abstract void addAdditionalData(BSONObject obj);

	public static FormField<?> parseField(BSONObject fieldObj) throws ControllerException {
		String dataType = getStringValue(fieldObj, "dataType");
		return FieldManager.getInstance().getField(dataType, fieldObj);
	}
}
