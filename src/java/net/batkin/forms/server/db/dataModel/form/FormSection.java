package net.batkin.forms.server.db.dataModel.form;

import java.util.ArrayList;
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
	private List<FormWidget> widgets;

	public FormSection(String title, String instructions, List<FormWidget> widgets) {
		this.title = title;
		this.instructions = instructions;
		this.widgets = widgets;
	}

	public FormSection(BSONObject obj) throws ServerDataException {
		title = getStringValue(obj, "title");
		instructions = getStringValue(obj, "instructions", null);
		widgets = new ArrayList<FormWidget>();
		for (BSONObject widgetObj : getArrayValue(obj, "fields", BSONObject.class)) {
			widgets.add(new FormWidget(widgetObj));
		}
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

	public List<FormWidget> getWidgets() {
		return widgets;
	}

}
