package net.batkin.forms.server.http.velocity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;

public class Helpers {

	private String urlBase;

	public Helpers(String urlBase) {
		this.urlBase = urlBase;
	}

	public static class RawHtml {
		private Object value;

		public RawHtml(Object value) {
			this.value = value;
		}

		public Object getValue() {
			return value;
		}
	}

	public Object raw(Object input) {
		return new RawHtml(input);
	}

	public String url(String appPath) {
		if (!appPath.startsWith("/")) {
			appPath = "/" + appPath;
		}
		return urlBase + appPath;
	}

	public RawHtml js(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		String url = url("/static/js" + path);
		return new RawHtml("<script src=\"" + url + "\"></script>");
	}

	public RawHtml css(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		String url = url("/static/css" + path);
		return new RawHtml("<link href=\"" + url + "\" rel=\"stylesheet\"/>");
	}

	public String stringifyException(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}

	public String jsArgList(Object[] args) throws IOException {
		if (args == null || args.length == 0) {
			return "";
		}

		boolean first = true;
		StringWriter writer = new StringWriter();

		for (Object arg : args) {
			if (!first) {
				writer.write(",");
			}

			if (arg instanceof Number || arg instanceof Boolean) {
				writer.write(arg.toString());
			} else {
				writer.write('"');
				StringEscapeUtils.escapeJavaScript(writer, arg.toString());
				writer.write('"');
			}

			first = false;
		}

		return writer.toString();
	}
}
