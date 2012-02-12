package net.batkin.forms.server.db.dataModel.action;

import net.batkin.forms.server.http.velocity.TemplateParameters;

public interface RenderAware {

	void populateContext(TemplateParameters params);

	String getTemplateName();
}
