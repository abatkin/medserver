package net.batkin.med.server.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatusController extends Controller {

	@Override
	public void handle(String[] parts, HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		response.getWriter().println("Hello");
	}

}
