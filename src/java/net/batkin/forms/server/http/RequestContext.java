package net.batkin.forms.server.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.batkin.forms.server.exception.ControllerException;
import net.batkin.forms.server.exception.FileNotFoundException;
import net.batkin.forms.server.http.Controller.RequestMethod;

import org.eclipse.jetty.server.Request;

public class RequestContext {
	private Renderer renderer;
	private String sessionId;
	private RequestMethod requestMethod;
	private String[] parts;
	private Request baseRequest;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public RequestContext(Renderer renderer, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
		this.renderer = renderer;
		this.baseRequest = baseRequest;
		this.request = request;
		this.response = response;
	}

	public void setTarget(String target) throws ControllerException {
		String[] parts = target.substring(1).split("/");
		if (parts.length < 1) {
			throw new FileNotFoundException();
		}
		this.parts = parts;

		try {
			this.requestMethod = RequestMethod.valueOf(request.getMethod());
		} catch (Exception e) {
			throw new FileNotFoundException();
		}
	}

	public String getControllerName() {
		return parts[0];
	}

	public String[] getParts() {
		return parts;
	}

	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	public Request getBaseRequest() {
		return baseRequest;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public Renderer getRenderer() {
		return renderer;
	}
}
