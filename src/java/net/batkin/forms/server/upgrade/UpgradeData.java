package net.batkin.forms.server.upgrade;

import net.batkin.forms.server.exception.ServerDataException;

import org.json.JSONException;

public abstract class UpgradeData {

	public abstract void apply() throws ServerDataException, JSONException;
}
