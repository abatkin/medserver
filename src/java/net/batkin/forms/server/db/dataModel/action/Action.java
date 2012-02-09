package net.batkin.forms.server.db.dataModel.action;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.exception.NotImplementedException;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public abstract class Action extends DbDataModel {

	private String stepName;

	protected Action(BSONObject obj) throws ServerDataException {
		stepName = getStringValue(obj, "stepName");
	}

	protected Action(String stepName) {
		this.stepName = stepName;
	}

	public abstract String getActionName();

	public abstract void process(ActionData data) throws Exception;

	public String getStepName() {
		return stepName;
	}

	@Override
	public ObjectId getObjectId() {
		throw new NotImplementedException();
	}

	@Override
	public DBObject toDbObject() {
		throw new NotImplementedException();
	}

}
