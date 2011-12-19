package net.batkin.med.server.controllers;

import com.google.gson.JsonObject;

public abstract class Controller {

	public abstract JsonObject handle(String[] parts, JsonObject request) throws ControllerException;

}
