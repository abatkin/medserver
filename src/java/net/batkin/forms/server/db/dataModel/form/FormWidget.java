package net.batkin.forms.server.db.dataModel.form;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.exception.NotImplementedException;
import net.batkin.forms.server.exception.ServerDataException;

public class FormWidget extends DbDataModel {

	private String name;
	private String title;
	private String helpText;

	public FormWidget(BSONObject obj) throws ServerDataException {
		name = getStringValue(obj, "name");
		title = getStringValue(obj, "title");
		helpText = getStringValue(obj, "helpText", null);
	}

	public FormWidget(String name, String title, String helpText) {
		this.name = name;
		this.title = title;
		this.helpText = helpText;
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

}
