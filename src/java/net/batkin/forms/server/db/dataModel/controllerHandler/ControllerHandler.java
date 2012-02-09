package net.batkin.forms.server.db.dataModel.controllerHandler;

import java.io.IOException;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.db.dataModel.form.FormLayout;
import net.batkin.forms.server.db.dataModel.schema.FormSchema;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.NotImplementedException;
import net.batkin.forms.server.exception.ServerDataException;
import net.batkin.forms.server.http.RequestContext;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public abstract class ControllerHandler extends DbDataModel {

	private String controllerHandlerType;

	protected ControllerHandler(BSONObject obj) throws ServerDataException {
		controllerHandlerType = getStringValue(obj, "controllerHandlerType");
	}

	protected ControllerHandler(String controllerHandlerType) {
		this.controllerHandlerType = controllerHandlerType;
	}

	public String getControllerHandlerType() {
		return controllerHandlerType;
	}

	public abstract String getUrlSuffix();

	public abstract void handle(RequestContext context, FormSchema schema, FormLayout layout) throws ControllerException, IOException;

	@Override
	public ObjectId getObjectId() {
		throw new NotImplementedException();
	}

	@Override
	public DBObject toDbObject() {
		throw new NotImplementedException();
	}

}
