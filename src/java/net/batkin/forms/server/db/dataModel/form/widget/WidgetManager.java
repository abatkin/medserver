package net.batkin.forms.server.db.dataModel.form.widget;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.batkin.forms.server.configuration.Configuration;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;

public class WidgetManager {

	private static WidgetManager instance;

	public static void configure(Configuration config) throws Exception {
		instance = new WidgetManager(config);
	}

	public static WidgetManager getInstance() {
		return instance;
	}

	private Map<String, WidgetCreator> widgetMap;

	public WidgetManager(Configuration config) throws Exception {
		this.widgetMap = new HashMap<String, WidgetManager.WidgetCreator>();
		for (String widgetType : config.getValues("widget.types")) {
			String className = config.getValue("widget.type." + widgetType);
			@SuppressWarnings("unchecked")
			Class<FormWidget<?, ?>> widgetClass = (Class<FormWidget<?, ?>>) Class.forName(className);
			Constructor<FormWidget<?, ?>> constructor = widgetClass.getConstructor(BSONObject.class);
			WidgetCreator creator = new WidgetCreator(widgetType, constructor);
			widgetMap.put(widgetType, creator);
		}
	}

	public FormWidget<?, ?> getWidget(String widgetType, BSONObject bson) throws ControllerException {
		WidgetCreator creator = widgetMap.get(widgetType);
		if (creator == null) {
			throw new ServerDataException("Unknown widget type " + widgetType);
		}
		return creator.newInstance(bson);
	}

	private class WidgetCreator {

		private String widgetType;
		private Constructor<FormWidget<?, ?>> constructor;

		public WidgetCreator(String widgetType, Constructor<FormWidget<?, ?>> constructor) {
			this.widgetType = widgetType;
			this.constructor = constructor;
		}

		public FormWidget<?, ?> newInstance(BSONObject obj) throws ControllerException {
			try {
				return constructor.newInstance(obj);
			} catch (InvocationTargetException e) {
				Throwable target = e.getTargetException();
				if (target instanceof ControllerException) {
					throw ((ControllerException) target);
				}
				throw new ServerDataException("Error constructing widget of type " + widgetType + " : " + e.getMessage());
			} catch (Exception e) {
				throw new ServerDataException("Error constructing widget of type " + widgetType + " : " + e.getMessage());
			}
		}
	}
}
