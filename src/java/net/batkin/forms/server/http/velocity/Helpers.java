package net.batkin.forms.server.http.velocity;

public class Helpers {

	public static class RawHtml {
		private Object value;

		public RawHtml(Object value) {
			this.value = value;
		}

		public Object getValue() {
			return value;
		}
	}

	public static Object raw(Object input) {
		return new RawHtml(input);
	}
}
