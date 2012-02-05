package net.batkin.forms.server.db.dataModel.schema.fields;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public abstract class FormField<DbClass> extends DbDataModel {

	private Class<DbClass> dbClass;
	private String fieldName;

	protected FormField(BSONObject obj, Class<DbClass> dbClass) throws ServerDataException {
		this.fieldName = getStringValue(obj, "name");
		this.dbClass = dbClass;
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

	public Class<DbClass> getDbClass() {
		return dbClass;
	}

	public Object convertFromDb(DbClass obj) {
		if (dbClass.isInstance(obj)) {
			return toNativeObject(obj);
		}
		throw new ClassCastException("Field " + getFieldName() + " must be of type " + dbClass.getName() + ", found " + obj.getClass().getName());
	}

	public DbClass convertToDb(Object obj) {
		return fromNativeObject(obj);
	}

	public abstract String getDataTypeName();

	public abstract Object getDefaultValue();

	public abstract void addAdditionalData(BSONObject obj);

	public abstract Object toNativeObject(DbClass obj);

	public abstract DbClass fromNativeObject(Object obj);

	public static FormField<?> parseField(BSONObject fieldObj) throws ControllerException {
		String dataType = getStringValue(fieldObj, "dataType");
		return FieldManager.getInstance().getField(dataType, fieldObj);
	}
}
