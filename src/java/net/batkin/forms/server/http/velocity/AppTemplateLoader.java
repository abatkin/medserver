package net.batkin.forms.server.http.velocity;

import java.io.InputStream;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

public class AppTemplateLoader extends FileResourceLoader {

	@Override
	public InputStream getResourceStream(String filename) throws ResourceNotFoundException {
		if (!filename.endsWith(".vm")) {
			filename = filename + ".vm";
		}
		return super.getResourceStream(filename);
	}
}
