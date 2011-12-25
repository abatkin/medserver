package net.batkin.med.server.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

public abstract class Controller {

	public abstract void handle(String[] parts, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

}
