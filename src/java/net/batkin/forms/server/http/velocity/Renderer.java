package net.batkin.forms.server.http.velocity;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import net.batkin.forms.server.configuration.ConfigurationOption;
import net.batkin.forms.server.util.ConfigBuilder;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class Renderer {

	private VelocityEngine ve;
	private VelocityContext rootContext;

	public Renderer(String urlBase) throws IOException {
		String templatePath = ConfigBuilder.getResourceDirectory("templates", ConfigurationOption.CONFIG_TEMPLATE_DIR);

		Properties props = new Properties();
		props.setProperty("input.encoding", "UTF-8");
		props.setProperty("output.encoding", "UTF-8");
		props.setProperty("runtime.log.logsystem.class", VelocitySlf4jLogChute.class.getName());
		props.setProperty("resource.loader", "app");
		props.setProperty("app.resource.loader.class", AppTemplateLoader.class.getName());
		props.setProperty("app.resource.loader.path", templatePath);
		props.setProperty("app.resource.loader.cache", "false");
		props.setProperty("app.resource.loader.modificationCheckInterval", "0");
		props.setProperty("velocimacro.library", "/global/page-components.vm");

		props.setProperty("eventhandler.referenceinsertion.class", Escaper.class.getName());

		ve = new VelocityEngine(props);
		rootContext = new VelocityContext();
		rootContext.put("h", new Helpers(urlBase));
	}

	public void renderTemplate(String templateName, TemplateParameters parameters, Writer writer) {
		VelocityContext vc = new VelocityContext(parameters, rootContext);
		Template template = ve.getTemplate(templateName);
		template.merge(vc, writer);
	}
}
