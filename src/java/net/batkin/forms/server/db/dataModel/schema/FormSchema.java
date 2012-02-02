package net.batkin.forms.server.db.dataModel.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.db.utility.DBAccess.DatabaseCollection;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.BasicBSONList;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FormSchema extends DbDataModel {

	private ObjectId id;
	private String schemaName;
	private boolean isActive;
	private List<FormField<?>> fieldList;
	private Map<String, FormField<?>> fieldMap;

	public FormSchema(BSONObject obj) throws ServerDataException {
		this.id = getObjectIdValue(obj, "_id");
		this.schemaName = getStringValue(obj, "schemaName");
		this.isActive = getBooleanValue(obj, "active", Boolean.TRUE).booleanValue();
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
		BasicDBObject obj = new BasicDBObject();
		putValue(obj, "_id", id);
		putValue(obj, "schemaName", schemaName);
		putValue(obj, "active", Boolean.valueOf(isActive));

		BasicBSONList bsonFieldList = new BasicBSONList();
		for (FormField<?> field : fieldList) {
			bsonFieldList.add(field.toDbObject());
		}
		putValue(obj, "fields", bsonFieldList);

		return obj;
	}

	public ObjectId getId() {
		return id;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public boolean isActive() {
		return isActive;
	}

	public List<FormField<?>> getFieldList() {
		return fieldList;
	}

	public Set<String> getFieldNames() {
		return new HashSet<String>(fieldMap.keySet());
	}

	public FormField<?> getField(String name) {
		return fieldMap.get(name);
	}

	public static FormSchema loadByName(String formName) throws ServerDataException {
		DBObject schemaData = DatabaseCollection.Schemas.findByString("schemaName", formName);
		if (schemaData == null) {
			return null;
		}

		return new FormSchema(schemaData);
	}

}
