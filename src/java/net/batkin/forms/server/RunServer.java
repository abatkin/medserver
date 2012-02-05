package net.batkin.forms.server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import net.batkin.forms.server.configuration.Configuration;
import net.batkin.forms.server.configuration.Configuration.ConfigurationSource;
import net.batkin.forms.server.configuration.ConfigurationLoader;
import net.batkin.forms.server.configuration.ConfigurationOption;
import net.batkin.forms.server.db.dataModel.Config;
import net.batkin.forms.server.db.dataModel.form.widget.WidgetManager;
import net.batkin.forms.server.db.dataModel.schema.fields.FieldManager;
import net.batkin.forms.server.db.utility.DBAccess;
import net.batkin.forms.server.exception.ConfigurationException;
import net.batkin.forms.server.exception.ServerDataException;
import net.batkin.forms.server.http.RestHandler;
import net.batkin.forms.server.session.SessionManager;
import net.batkin.forms.server.util.ConfigBuilder;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunServer {

	private static Server server;

	public static void main(String[] args) throws Exception {
		try {
			loadInitialConfigurations(args);
			DBAccess.connect();
			DBAccess.initDatabase();

			Configuration config = Configuration.getInstance();
			if (config.containsKey(ConfigurationOption.CONFIG_SERVER_NAME)) {
				mergeDbConfig("server." + config.getValue(ConfigurationOption.CONFIG_SERVER_NAME));
			}
			mergeDbConfig("server");

			DBAccess.upgradeDatabase();
			mergeDbConfig("server"); // To pick up new upgrades

			ConfigurationLoader.dumpConfiguration();
			createServer();
		} catch (ConfigurationException e) {
			fatalError(e.getMessage(), null);
		} catch (Exception e) {
			fatalError("Fatal Startup Error: " + e.getMessage(), e);
		}

		server.join();
		LoggerFactory.getLogger(RunServer.class).warn("Server has been shut down");
	}

	private static void mergeDbConfig(String name) throws ServerDataException {
		Config config = Config.loadByName(name);
		if (config != null) {
			Configuration.getInstance().addValues(ConfigurationSource.createDatabaseSource(name), config);
		}
	}

	private static void loadInitialConfigurations(String[] args) throws ConfigurationException {
		Configuration config = Configuration.getInstance();
		ConfigurationLoader.loadCommandLine(args);

		if (config.containsKey(ConfigurationOption.CONFIG_CONFIG_FILE)) {
			Properties props = loadProperties(config.getValue(ConfigurationOption.CONFIG_CONFIG_FILE));
			ConfigurationLoader.loadProperties(props);
		}
	}

	private static void createServer() throws Exception {
		Logger logger = LoggerFactory.getLogger(RunServer.class);

		Configuration config = Configuration.getInstance();
		int port = config.getIntegerValue(ConfigurationOption.CONFIG_HTTP_SERVER_PORT, 8080);
		logger.info("Starting web server on port " + port);

		server = new Server(port);
		server.setGracefulShutdown(5000);
		server.setStopAtShutdown(true);
		ContextHandlerCollection handler = new ContextHandlerCollection();

		String staticDir = ConfigBuilder.getResourceDirectory("static", ConfigurationOption.CONFIG_STATIC_DIR);
		ContextHandler staticContext = handler.addContext("/static", staticDir);
		staticContext.setHandler(new ResourceHandler());

		ContextHandler formsContext = handler.addContext("/", "/");
		formsContext.setHandler(new RestHandler(""));

		FieldManager.configure(config);
		WidgetManager.configure(config);

		server.setHandler(handler);
		server.start();

		logger.info("Starting Session Manager");
		SessionManager.getInstance().start();

		logger.info("Server started");
	}

	private static Properties loadProperties(String filename) throws ConfigurationException {
		try {
			InputStream istream = new FileInputStream(filename);
			Properties props = new Properties();
			props.load(istream);
			istream.close();
			return props;
		} catch (Exception e) {
			throw new ConfigurationException("Unable to open configuration file " + filename + ": " + e.getMessage());
		}
	}

	private static void fatalError(String message, Exception e) {
		Logger logger = LoggerFactory.getLogger(RunServer.class);
		if (e != null) {
			logger.warn("FATAL ERROR! " + message, e);
		} else {
			logger.warn("FATAL ERROR! " + message);
		}
		System.exit(1);
	}

}