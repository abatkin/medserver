package net.batkin.forms.server.db.dataModel.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.db.dataModel.action.Action;
import net.batkin.forms.server.db.dataModel.action.ActionManager;
import net.batkin.forms.server.db.dataModel.controllerHandler.ControllerHandler;
import net.batkin.forms.server.db.dataModel.controllerHandler.ControllerHandlerManager;
import net.batkin.forms.server.db.dataModel.form.widget.FormWidget;
import net.batkin.forms.server.db.dataModel.schema.FormSchema;
import net.batkin.forms.server.db.utility.DatabaseCollection;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.NotImplementedException;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class FormLayout extends DbDataModel {

	private ObjectId id;
	private String formName;
	private String schemaName;
	private String title;
	private String description;
	private List<FormLink> links;
	private List<FormSection> sections;
	private List<FormWidget<?>> allWidgets;
	private List<Action> actions;
	private Map<String, ControllerHandler> handlers;

	public FormLayout(BSONObject obj) throws ControllerException {
		id = getObjectIdValue(obj, "_id");
		formName = getStringValue(obj, "formName");
		schemaName = getStringValue(obj, "schemaName");
		description = getStringValue(obj, "description", null);
		title = getStringValue(obj, "title");

		links = new ArrayList<FormLink>();
		for (BSONObject linkObj : getArrayValue(obj, "links", BSONObject.class)) {
			links.add(new FormLink(linkObj));
		}

		allWidgets = new ArrayList<FormWidget<?>>();
		sections = new ArrayList<FormSection>();
		for (BSONObject sectionObj : getArrayValue(obj, "sections", BSONObject.class)) {
			FormSection section = new FormSection(sectionObj);
			sections.add(section);

			allWidgets.addAll(section.getWidgets());
		}

		actions = new ArrayList<Action>();
		List<BSONObject> actionObjs = getOptionalArrayValue(obj, "actions", BSONObject.class);
		ActionManager actionManager = ActionManager.getInstance();
		if (actionObjs == null || actionObjs.isEmpty()) {
			actions.add(ActionManager.DEFAULT_ACTION);
		} else {
			for (BSONObject actionObj : actionObjs) {
				String actionType = getStringValue(actionObj, "actionType");
				Action action = actionManager.getObject(actionType, actionObj);
				actions.add(action);
			}
		}

		handlers = new HashMap<String, ControllerHandler>();
		List<BSONObject> handlerObjs = getOptionalArrayValue(obj, "handlers", BSONObject.class);
		handlers.put("show", ControllerHandlerManager.SHOW_HANDLER);
		handlers.put("submit", ControllerHandlerManager.SUBMIT_HANDLER);
		ControllerHandlerManager handlerManager = ControllerHandlerManager.getInstance();
		if (handlerObjs != null && !handlerObjs.isEmpty()) {
			for (BSONObject handlerObj : handlerObjs) {
				String handlerType = getStringValue(handlerObj, "controllerHandlerType");
				ControllerHandler handler = handlerManager.getObject(handlerType, handlerObj);
				handlers.put(handler.getUrlSuffix(), handler);
			}
		}
	}

	public ControllerHandler getControllerHandler(String name) {
		return handlers.get(name);
	}

	public void validateFields(FormSchema schema) throws ServerDataException {
		Set<String> fieldNames = schema.getFieldNames();
		Set<String> handledNames = new HashSet<String>();
		for (FormSection section : sections) {
			for (FormWidget<?> widget : section.getWidgets()) {
				String fieldName = widget.getName();
				if (fieldNames.contains(fieldName)) {
					fieldNames.remove(fieldName);
					handledNames.add(fieldName);
				} else if (handledNames.contains(fieldName)) {
					throw new ServerDataException("Duplicate widget " + fieldName + " in form " + formName + " for schema " + schemaName);
				} else {
					throw new ServerDataException("Unknown widget " + fieldName + " in form " + formName + " for schema " + schemaName);
				}
			}
		}
	}

	@Override
	public ObjectId getObjectId() {
		return id;
	}

	@Override
	public DBObject toDbObject() {
		throw new NotImplementedException();
	}

	public String getFormName() {
		return formName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public List<FormLink> getLinks() {
		return links;
	}

	public List<FormSection> getSections() {
		return sections;
	}

	public List<FormWidget<?>> getAllWidgets() {
		return allWidgets;
	}

	public List<Action> getActions() {
		return actions;
	}

	public static FormLayout loadByName(String formName) throws ControllerException {
		DBObject formData = DatabaseCollection.Layouts.findByString("formName", formName);
		if (formData == null) {
			return null;
		}

		return new FormLayout(formData);
	}
}
