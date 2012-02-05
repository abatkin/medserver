package net.batkin.forms.server.db.dataModel.schema.fields;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.batkin.forms.server.configuration.Configuration;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;

public class FieldManager {

	private static FieldManager instance;

	public static void configure(Configuration config) throws Exception {
		instance = new FieldManager(config);
	}

	public static FieldManager getInstance() {
		return instance;
	}

	private Map<String, FieldCreator> fieldMap;

	public FieldManager(Configuration config) throws Exception {
		this.fieldMap = new HashMap<String, FieldManager.FieldCreator>();
		for (String fieldType : config.getValues("field.types")) {
			String className = config.getValue("field.type." + fieldType);
			@SuppressWarnings("unchecked")
			Class<FormField<?, ?>> fieldClass = (Class<FormField<?, ?>>) Class.forName(className);
			Constructor<FormField<?, ?>> constructor = fieldClass.getConstructor(BSONObject.class);
			FieldCreator creator = new FieldCreator(fieldType, constructor);
			fieldMap.put(fieldType, creator);
		}
	}

	public FormField<?, ?> getField(String fieldType, BSONObject bson) throws ControllerException {
		FieldCreator creator = fieldMap.get(fieldType);
		if (creator == null) {
			throw new ServerDataException("Unknown field type " + fieldType);
		}
		return creator.newInstance(bson);
	}

	private class FieldCreator {

		private String fieldType;
		private Constructor<FormField<?, ?>> constructor;

		public FieldCreator(String fieldType, Constructor<FormField<?, ?>> constructor) {
			this.fieldType = fieldType;
			this.constructor = constructor;
		}

		public FormField<?, ?> newInstance(BSONObject obj) throws ControllerException {
			try {
				return constructor.newInstance(obj);
			} catch (InvocationTargetException e) {
				Throwable target = e.getTargetException();
				if (target instanceof ControllerException) {
					throw ((ControllerException) target);
				}
				throw new ServerDataException("Error constructing field of type " + fieldType + " : " + e.getMessage());
			} catch (Exception e) {
				throw new ServerDataException("Error constructing field of type " + fieldType + " : " + e.getMessage());
			}
		}
	}
}
