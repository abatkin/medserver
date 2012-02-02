package net.batkin.forms.server.http;

import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class AppTemplateLoader extends ResourceLoader {

	@Override
	public void init(ExtendedProperties props) {
	}

	@Override
	public long getLastModified(Resource resource) {
		return 0;
	}

	@Override
	public InputStream getResourceStream(String filename) throws ResourceNotFoundException {
		if (!filename.startsWith("/")) {
			filename = "/" + filename;
		}
		filename = "/templates" + filename;
		if (!filename.endsWith(".vm")) {
			filename += ".vm";
		}
		InputStream is = AppTemplateLoader.class.getResourceAsStream(filename);
		return is;
	}

	@Override
	public boolean isSourceModified(Resource resource) {
		return false;
	}
}
