package net.batkin.forms.server.db.dataModel.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import net.batkin.forms.server.db.dataModel.BSONAble;
import net.batkin.forms.server.db.dataModel.form.FormLayout;
import net.batkin.forms.server.db.dataModel.form.FormResult;
import net.batkin.forms.server.db.dataModel.schema.FormSchema;
import net.batkin.forms.server.db.utility.FormUtility;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

public class ActionData {

	private Map<String, Object> extraProperties;

	private static AtomicLong counter = new AtomicLong((new java.util.Random()).nextLong());

	private FormSchema schema;
	private FormLayout layout;
	private FormResult result;
	private Date submittedAt;
	private String submissionId;

	public ActionData(FormSchema schema, FormLayout layout, FormResult result) throws IOException {
		this.extraProperties = new HashMap<String, Object>();
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

	public void setProperty(String key, byte[] value) {
		extraProperties.put(key, value);
	}

	public void setProperty(String key, Boolean value) {
		extraProperties.put(key, value);
	}

	public void setProperty(String key, String value) {
		extraProperties.put(key, value);
	}

	public void setProperty(String key, Integer value) {
		extraProperties.put(key, value);
	}

	public void setProperty(String key, Float value) {
		extraProperties.put(key, value);
	}

	public void setProperty(String key, Date value) {
		extraProperties.put(key, value);
	}

	public void setProperty(String key, BSONObject value) {
		extraProperties.put(key, value);
	}

	public void setProperty(String key, BSONAble value) {
		extraProperties.put(key, value);
	}

	public Object getProperty(String key) {
		return extraProperties.get(key);
	}

	public boolean hasProperty(String key) {
		return extraProperties.containsKey(key);
	}

	public Set<String> propertyNames() {
		return extraProperties.keySet();
	}
}
