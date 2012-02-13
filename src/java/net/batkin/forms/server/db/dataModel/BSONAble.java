package net.batkin.forms.server.db.dataModel;

import org.bson.BSONObject;

public interface BSONAble {
	BSONObject toBson();
}
