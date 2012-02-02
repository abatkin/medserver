package net.batkin.forms.server.db.dataModel.form;

import net.batkin.forms.server.db.dataModel.DbDataModel;
import net.batkin.forms.server.exception.NotImplementedException;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class FormLink extends DbDataModel {

	public enum LinkType {
		ExternalLink, DataLink
	}

	private String name;
	private String displayText;
	private LinkType linkType;
	private String displayData;
	private String destinationUrl;

	public FormLink(BSONObject obj) throws ServerDataException {
		name = getStringValue(obj, "name");
		displayText = getStringValue(obj, "displayText");
		String linkTypeString = getStringValue(obj, "linkType");
		try {
			linkType = LinkType.valueOf(linkTypeString);
		} catch (Exception e) {
			throw new ServerDataException("Invalid value for linkType: " + linkTypeString);
		}
		displayData = getStringValue(obj, "displayData", null);
		destinationUrl = getStringValue(obj, "destinationUrl", null);
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

	public String getDisplayText() {
		return displayText;
	}

	public LinkType getLinkType() {
		return linkType;
	}

	public String getDisplayData() {
		return displayData;
	}

	public String getDestinationUrl() {
		return destinationUrl;
	}

}
