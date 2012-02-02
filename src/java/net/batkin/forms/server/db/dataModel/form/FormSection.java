package net.batkin.forms.server.db.dataModel.form;

import java.util.List;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.exception.NotImplementedException;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class FormSection extends DbDataModel {

	private String title;
	private String instructions;
	private List<String> fields;

	public FormSection(String title, String instructions, List<String> fields) {
		this.title = title;
		this.instructions = instructions;
		this.fields = fields;
	}

	public FormSection(BSONObject obj) throws ServerDataException {
		title = getStringValue(obj, "title");
		instructions = getStringValue(obj, "instructions", null);
		this.fields = getArrayValue(obj, "fields", String.class);
	}

	@Override
	public ObjectId getObjectId() {
		throw new NotImplementedException();
	}

	@Override
	public DBObject toDbObject() {
		throw new NotImplementedException();
	}

	public String getTitle() {
		return title;
	}

	public String getInstructions() {
		return instructions;
	}

	public List<String> getFields() {
		return fields;
	}

}
