package net.batkin.forms.server.db.dataModel.form.widget;

import net.batkin.forms.server.util.TypeManager;

public class WidgetManager extends TypeManager<FormWidget<?>> {

	private static WidgetManager instance;

	public static void configure() throws Exception {
		instance = new WidgetManager();
	}

	public static WidgetManager getInstance() {
		return instance;
	}

	public WidgetManager() throws Exception {
		super("widget");
	}

}
