package net.batkin.forms.server.http.velocity;

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

}
