package net.batkin.med.server.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Controller {

	public abstract void handle(String[] parts, HttpServletRequest request, HttpServletResponse response) throws IOException;

}
