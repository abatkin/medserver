package net.batkin.forms.server.db.dataModel.form;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.db.dataModel.schema.FormSchema;
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
	private List<FormLink> links;
	private List<FormSection> sections;

	public FormLayout(BSONObject obj) throws ServerDataException {
		id = getObjectIdValue(obj, "_id");
		formName = getStringValue(obj, "formName");
		schemaName = getStringValue(obj, "schemaName");
		title = getStringValue(obj, "title");

		links = new ArrayList<FormLink>();
		for (BSONObject linkObj : getArrayValue(obj, "links", BSONObject.class)) {
			links.add(new FormLink(linkObj));
		}

		sections = new ArrayList<FormSection>();
		for (BSONObject sectionObj : getArrayValue(obj, "sections", BSONObject.class)) {
			FormSection section = new FormSection(sectionObj);
			sections.add(section);
		}
	}

	public void validateFields(FormSchema schema) throws ServerDataException {
		Set<String> fieldNames = new HashSet<String>(schema.getFieldMap().keySet());
		Set<String> handledNames = new HashSet<String>(schema.getFieldMap().keySet());
		for (FormSection section : sections) {
			for (String fieldName : section.getFields()) {
				if (fieldNames.contains(fieldName)) {
					fieldNames.remove(fieldName);
					handledNames.add(fieldName);
				} else if (handledNames.contains(fieldName)) {
					throw new ServerDataException("Duplicate field " + fieldName + " in form " + formName + " for schema " + schemaName);
				} else {
					throw new ServerDataException("Unknown field " + fieldName + " in form " + formName + " for schema " + schemaName);
				}
			}
		}

		// Just in case there is anything left over
		if (!fieldNames.isEmpty()) {
			List<String> fieldList = new ArrayList<String>(fieldNames);
			FormSection section = new FormSection("Additional Information", null, fieldList);
			sections.add(section);
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

	public List<FormLink> getLinks() {
		return links;
	}

	public List<FormSection> getSections() {
		return sections;
	}

}
