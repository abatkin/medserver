package net.batkin.forms.server.db.dataModel.schema.fields;

import net.batkin.forms.server.util.TypeManager;

public class FieldManager extends TypeManager<FormField<?>> {

	public FieldManager() throws Exception {
		super("field");
	}

	private static FieldManager instance;

	public static void configure() throws Exception {
		instance = new FieldManager();
	}

	public static FieldManager getInstance() {
		return instance;
	}
}
