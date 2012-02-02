package net.batkin.forms.server.http.velocity;


import net.batkin.forms.server.http.velocity.Helpers.RawHtml;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

public class Escaper implements ReferenceInsertionEventHandler {

	@Override
	public Object referenceInsert(String reference, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof RawHtml) {
			return ((RawHtml)value).getValue();
		} else {
			return StringEscapeUtils.escapeHtml(value.toString());
		}
	}

}
