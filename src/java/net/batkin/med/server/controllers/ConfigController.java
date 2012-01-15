package net.batkin.med.server.controllers;

import java.util.List;

import net.batkin.med.server.db.dataModel.Config;
import net.batkin.med.server.db.dataModel.User;
import net.batkin.med.server.db.utility.DBAccess;
import net.batkin.med.server.db.utility.DBAccess.DatabaseCollection;
import net.batkin.med.server.exception.ClientRequestException;
import net.batkin.med.server.exception.ControllerException;
import net.batkin.med.server.exception.FileNotFoundException;
import net.batkin.med.server.exception.ServerDataException;
import net.batkin.med.server.http.ErrorCodes;
import net.batkin.med.server.http.RequestContext;
import net.batkin.med.server.json.request.UpdateConfigRequest;
import net.batkin.med.server.json.response.ConfigDataResponse;
import net.batkin.med.server.json.response.ConfigListResponse;

import com.google.gson.JsonObject;

public class ConfigController extends LoggedInController {

	@Override
	public JsonObject handle(RequestContext context, User user, JsonObject request) throws ControllerException {
		ensurePermission(user, Permissions.Admin);

		String[] parts = context.getParts();
		if (parts.length == 1) {
			return listConfigs();
		} else {
			String configName = parts[1];

			String requestMethod = context.getRequest().getMethod();
			if (requestMethod.equals("GET")) {
				return getConfigJson(configName);
			} else if (requestMethod.equals("POST")) {
				return updateConfig(configName, request);
			} else if (requestMethod.equals("DELETE")) {
				return deleteConfig(configName);
			} else {
				throw new FileNotFoundException();
			}
		}
	}

	private JsonObject deleteConfig(String configName) throws ControllerException {
		Config config = Config.loadByName(configName);
		if (config == null) {
			return buildGenericResponse(false, "Config [" + configName + "] not found", ErrorCodes.ERROR_CODE_CONFIG_NOT_FOUND);
		}
		DBAccess.getCollection(DatabaseCollection.Configs).remove(config.toDbObject());
		return buildSuccessResponse();
	}

	private JsonObject updateConfig(String configName, JsonObject request) throws ControllerException {
		UpdateConfigRequest ucRequest = new UpdateConfigRequest(request);
		if (configName.equals(ucRequest.getConfigName())) {
			throw new ClientRequestException("URI configName and request configName mismatch");
		}
		
		Config config = Config.loadByName(configName);
		if (config == null) {
			return buildGenericResponse(false, "Config [" + configName + "] not found", ErrorCodes.ERROR_CODE_CONFIG_NOT_FOUND);
		}
		
		config.replaceValues(ucRequest.getValues());
		
		DBAccess.getCollection(DatabaseCollection.Configs).save(config.toDbObject());
		
		return buildSuccessResponse();
	}

	private JsonObject listConfigs() {
		List<String> configNames = Config.listConfigNames();
		return new ConfigListResponse(configNames).toJson();
	}

	private JsonObject getConfigJson(String configName) throws ServerDataException {
		Config config = Config.loadByName(configName);
		if (config == null) {
			return buildGenericResponse(false, "Config [" + configName + "] not found", ErrorCodes.ERROR_CODE_CONFIG_NOT_FOUND);
		}
		return new ConfigDataResponse(config).toJson();
	}

}
