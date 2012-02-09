package net.batkin.forms.server.db.dataModel.controllerHandler;

import net.batkin.forms.server.util.TypeManager;

public class ControllerHandlerManager extends TypeManager<ControllerHandler> {

	public static ControllerHandler SUBMIT_HANDLER = null;
	public static ControllerHandler SHOW_HANDLER = null;

	private static ControllerHandlerManager instance;

	public static void configure() throws Exception {
		instance = new ControllerHandlerManager();
		SUBMIT_HANDLER = new SubmitFormControllerHandler();
		SHOW_HANDLER = new ShowFormControllerHandler();
	}

	public static ControllerHandlerManager getInstance() {
		return instance;
	}

	public ControllerHandlerManager() throws Exception {
		super("controllerHandler");
	}

}
