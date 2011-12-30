package net.batkin.med.server.dataModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.batkin.med.server.db.utility.MapValueParser;
import net.batkin.med.server.exception.ServerDataException;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

public class User extends DataModel {
	private ObjectId id;
	private String username;
	private String fullName;
	private Set<String> permissions;
	private Map<String, String> preferences;

	public User(BSONObject obj) throws ServerDataException {
		this.id = getObjectIdValue(obj, "_id");
		this.username = getStringValue(obj, "userName");
		this.fullName = getStringValue(obj, "fullName");
		this.permissions = parsePermissions(getOptionalArrayValue(obj, "permissions", String.class));
		this.preferences = MapValueParser.getStringArrayMap(obj, "preferences");
	}

	private Set<String> parsePermissions(List<String> permissions) {
		if (permissions != null) {
			return new HashSet<String>(permissions);
		} else {
			return new HashSet<String>();
		}
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}

	public Map<String, String> getPreferences() {
		return preferences;
	}

	public void setPreferences(Map<String, String> preferences) {
		this.preferences = preferences;
	}

	public String getUsername() {
		return username;
	}

	public ObjectId getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}