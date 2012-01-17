package net.batkin.med.server.db.dataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.batkin.med.server.db.dataModel.fields.FormField;
import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class FormSchema extends DbDataModel {

	private ObjectId id;
	private String schemaName;
	private int schemaVersion;
	private boolean isActive;
	private List<FormField<?>> fieldList;
	private Map<String, FormField<?>> fieldMap;

	public FormSchema(BSONObject obj) throws ServerDataException {
		this.id = getObjectIdValue(obj, "_id");
		this.schemaName = getStringValue(obj, "schemaName");
		this.schemaVersion = getIntegerValue(obj, "schemaVersion");
		this.isActive = getBooleanValue(obj, "active");
		this.fieldList = new ArrayList<FormField<?>>();
		this.fieldMap = new HashMap<String, FormField<?>>();
		for (BSONObject fieldObj : getArrayValue(obj, "fields", BSONObject.class)) {
			FormField<?> field = FormField.parseField(fieldObj);
			String fieldName = field.getFieldName();
			fieldList.add(field);
			if (fieldMap.containsKey(fieldName)) {
				throw new ServerDataException("Duplicate field " + fieldName + " found");
			}
			fieldMap.put(fieldName, field);
		}
	}

	@Override
	public ObjectId getObjectId() {
		return id;
	}

	@Override
	public DBObject toDbObject() {
		return null;
	}

	public ObjectId getId() {
		return id;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public int getSchemaVersion() {
		return schemaVersion;
	}

	public boolean isActive() {
		return isActive;
	}

}
