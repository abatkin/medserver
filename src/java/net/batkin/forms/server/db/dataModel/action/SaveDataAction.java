package net.batkin.forms.server.db.dataModel.action;

import net.batkin.forms.server.db.dataModel.form.FieldData;
import net.batkin.forms.server.db.dataModel.form.FormResult;
import net.batkin.forms.server.db.dataModel.schema.FormSchema;
import net.batkin.forms.server.db.dataModel.schema.fields.FormField;
import net.batkin.forms.server.db.utility.DBAccess;
import net.batkin.forms.server.db.utility.DatabaseCollection;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

import com.mongodb.BasicDBObject;

public class SaveDataAction extends Action {

	private String collectionName;

	public SaveDataAction(BSONObject obj) throws ServerDataException {
		super(obj);
		this.collectionName = getStringValue(obj, "collectionName");
	}

	public SaveDataAction(String stepName, String collectionName) {
		super(stepName);
		this.collectionName = collectionName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	@Override
	public String getActionName() {
		return "savedata";
	}

	@Override
	public void process(ActionData data) {
		BasicDBObject obj = new BasicDBObject();
		obj.append("formName", data.getLayout().getFormName());
		obj.append("submittedAt", data.getSubmittedAt());
		obj.append("submissionId", data.getSubmissionId());

		BasicBSONList valueList = buildDbValueList(data.getSchema(), data.getResult());
		obj.append("values", valueList);

		DatabaseCollection collection = new DatabaseCollection(DBAccess.getDb(), collectionName);
		collection.saveDbObject(obj);
	}

	public static BasicBSONList buildDbValueList(FormSchema schema, FormResult result) {
		BasicBSONList valueList = new BasicBSONList();
		for (FormField<?> field : schema.getFieldList()) {
			FieldData<?> value = result.getValue(field.getFieldName());
			Object nativeObject = value.getObject();
			Object dbObj = field.convertToDb(nativeObject);

			BSONObject valueObj = new BasicBSONObject();
			valueObj.put("name", field.getFieldName());
			valueObj.put("value", dbObj);

			valueList.add(valueObj);
		}

		return valueList;
	}
}
