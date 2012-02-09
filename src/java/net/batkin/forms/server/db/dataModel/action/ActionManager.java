package net.batkin.forms.server.db.dataModel.action;

import net.batkin.forms.server.configuration.Configuration;
import net.batkin.forms.server.configuration.ConfigurationOption;
import net.batkin.forms.server.util.TypeManager;

public class ActionManager extends TypeManager<Action> {

	public static Action DEFAULT_ACTION;

	public ActionManager() throws Exception {
		super("action");
	}

	private static ActionManager instance;

	public static void configure() throws Exception {
		instance = new ActionManager();

		Configuration config = Configuration.getInstance();
		String stepName = config.getValue(ConfigurationOption.CONFIG_DEFAULT_ACTION_STEP_NAME, "default-save-action");
		String collectionName = config.getValue(ConfigurationOption.CONFIG_DEFAULT_COLLECTION_NAME, "results");

		DEFAULT_ACTION = new SaveDataAction(stepName, collectionName);
	}

	public static ActionManager getInstance() {
		return instance;
	}
}
