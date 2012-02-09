package net.batkin.forms.server.db.dataModel.action;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.types.ObjectId;

import net.batkin.forms.server.db.dataModel.form.FormLayout;
import net.batkin.forms.server.db.dataModel.form.FormResult;
import net.batkin.forms.server.db.dataModel.schema.FormSchema;
import net.batkin.forms.server.db.utility.FormUtility;

public class ActionData {

	private static AtomicLong counter = new AtomicLong((new java.util.Random()).nextLong());

	private FormSchema schema;
	private FormLayout layout;
	private FormResult result;
	private Date submittedAt;
	private String submissionId;

	public ActionData(FormSchema schema, FormLayout layout, FormResult result) throws IOException {
		this.schema = schema;
		this.layout = layout;
		this.result = result;
		this.submittedAt = new Date();
		this.submissionId = generateSubmissionId();
	}

	private String generateSubmissionId() throws IOException {
		long threadId = Thread.currentThread().getId();
		long submitTime = submittedAt.getTime();
		long someValue = counter.getAndIncrement();
		int machineData = ObjectId.getGenMachineId();
		return FormUtility.generateChecksum(threadId + ":" + submitTime + ":" + someValue + ":" + machineData);
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

	public String getSubmissionId() {
		return submissionId;
	}
}
