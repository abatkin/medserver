package net.batkin.forms.server.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.batkin.forms.server.configuration.Configuration;
import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.ServerDataException;

import org.bson.BSONObject;

public class TypeManager<ResultType> {

	private String objType;
	private Map<String, ObjectCreator> creatorMap;

	public TypeManager(String objType) throws Exception {
		this.objType = objType;
		Configuration config = Configuration.getInstance();
		this.creatorMap = new HashMap<String, TypeManager<ResultType>.ObjectCreator>();
		for (String typeName : config.getValues(objType + ".types")) {
			String className = config.getValue(objType + ".type." + typeName);
			@SuppressWarnings("unchecked")
			Class<ResultType> actualResultClass = (Class<ResultType>) Class.forName(className);
			Constructor<ResultType> constructor = actualResultClass.getConstructor(BSONObject.class);
			ObjectCreator creator = new ObjectCreator(typeName, constructor);
			creatorMap.put(typeName, creator);
		}
	}

	public ResultType getObject(String typeName, BSONObject bson) throws ControllerException {
		ObjectCreator creator = creatorMap.get(typeName);
		if (creator == null) {
			throw new ServerDataException("Unknown " + objType + " type " + typeName);
		}
		return creator.newInstance(bson);
	}

	private class ObjectCreator {

		private String typeName;
		private Constructor<ResultType> constructor;

		public ObjectCreator(String typeName, Constructor<ResultType> constructor) {
			this.typeName = typeName;
			this.constructor = constructor;
		}

		public ResultType newInstance(BSONObject obj) throws ControllerException {
			try {
				return constructor.newInstance(obj);
			} catch (InvocationTargetException e) {
				Throwable target = e.getTargetException();
				if (target instanceof ControllerException) {
					throw ((ControllerException) target);
				}
				throw new ServerDataException("Error constructing " + objType + " of type " + typeName + " : " + e.getMessage());
			} catch (Exception e) {
				throw new ServerDataException("Error constructing " + objType + " of type " + typeName + " : " + e.getMessage());
			}
		}
	}
}
