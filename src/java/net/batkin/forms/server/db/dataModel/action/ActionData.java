package net.batkin.forms.server.db.dataModel.action;

import java.util.Date;

import net.batkin.forms.server.db.dataModel.form.FormLayout;
import net.batkin.forms.server.db.dataModel.form.FormResult;
import net.batkin.forms.server.db.dataModel.schema.FormSchema;

public class ActionData {

	private FormSchema schema;
	private FormLayout layout;
	private FormResult result;
	private Date submittedAt;

	public ActionData(FormSchema schema, FormLayout layout, FormResult result) {
		this.schema = schema;
		this.layout = layout;
		this.result = result;
		this.submittedAt = new Date();
	}

	public FormSchema getSchema() {
		return schema;
	}

	public FormLayout getLayout() {
		return layout;
	}

	public FormResult getResult() {
		return result;
	}

	public Date getSubmittedAt() {
		return submittedAt;
	}
}
