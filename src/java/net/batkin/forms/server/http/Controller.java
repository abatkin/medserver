package net.batkin.forms.server.http;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.http.velocity.TemplateParameters;

public abstract class Controller {

	public enum RequestMethod {
		GET, POST
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ControllerMapping {
		String prefix();

		RequestMethod[] requestMethods();
	}

	public abstract void handle(RequestContext context) throws ControllerException, IOException;

	private Set<RequestMethod> requestMethods;

	public Controller() {
		this.requestMethods = new HashSet<Controller.RequestMethod>();
	}

	public void setRequesetMethods(RequestMethod[] methods) {
		for (RequestMethod method : methods) {
			requestMethods.add(method);
		}
	}

	public boolean canHandle(RequestMethod method) {
		return requestMethods.contains(method);
	}

	public static void sendHtmlResponse(RequestContext context, String template, TemplateParameters params) throws IOException {
		HttpServletResponse response = context.getResponse();
		response.setContentType("text/html");
		renderResponse(context, template, params);
	}

	public static void sendError(RequestContext context, int httpCode, int applicationCode, String message, Throwable t) throws IOException {
		HttpServletResponse response = context.getResponse();
		response.setContentType("text/html");
		response.setStatus(httpCode);
		TemplateParameters params = new TemplateParameters()
			.add("httpCode", Integer.valueOf(httpCode))
			.add("applicationCode", Integer.valueOf(applicationCode))
			.add("message", message)
			.add("ex", t)
		;
		switch (httpCode) {
		case 404:
		case 500:
			renderResponse(context, "/errors/" + httpCode, params);
			break;
		default:
			renderResponse(context, "/errors/500", params);
		}
	}

	private static void renderResponse(RequestContext context, String template, TemplateParameters params) throws IOException {
		context.getRenderer().renderTemplate(template, params, context.getResponse().getWriter());
		context.getBaseRequest().setHandled(true);
	}
}
