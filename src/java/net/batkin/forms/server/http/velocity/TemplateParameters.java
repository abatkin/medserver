package net.batkin.forms.server.http.velocity;

import java.util.HashMap;

public class TemplateParameters extends HashMap<String, Object> {
	public TemplateParameters add(String key, Object value) {
		put(key, value);
		return this;
	}
}
