package net.batkin.med.server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.batkin.med.server.configuration.Configuration;
import net.batkin.med.server.configuration.Configuration.ConfigurationSource;
import net.batkin.med.server.configuration.ConfigurationLoader;
import net.batkin.med.server.configuration.ConfigurationOption;
import net.batkin.med.server.db.DBAccess;
import net.batkin.med.server.db.DBConfigUtility;
import net.batkin.med.server.exception.ConfigurationException;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunServer {

	private static Server server;

	public static void main(String[] args) throws Exception {
		try {
			loadInitialConfigurations(args);
			DBAccess.connect();
			DBAccess.createDatabase();

			Configuration config = Configuration.getInstance();
			if (config.containsKey(ConfigurationOption.CONFIG_SERVER_NAME)) {
				mergeDbConfig("server." + config.getValue(ConfigurationOption.CONFIG_SERVER_NAME));
			}
			mergeDbConfig("server");

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

	private static void mergeDbConfig(String name) {
		Map<String, List<String>> newValues = DBConfigUtility.loadDbConfig(name);
		Configuration.getInstance().addValues(ConfigurationSource.createDatabaseSource(name), newValues);
	}

	private static void loadInitialConfigurations(String[] args) throws ConfigurationException {
		Configuration config = Configuration.getInstance();
		ConfigurationLoader.parseCommandLine(args);

		if (config.containsKey(ConfigurationOption.CONFIG_CONFIG_FILE)) {
			Properties props = loadProperties(config.getValue(ConfigurationOption.CONFIG_CONFIG_FILE));
			ConfigurationLoader.parseProperties(props);
		}
	}

	private static void createServer() throws Exception {
		Logger logger = LoggerFactory.getLogger(RunServer.class);

		int port = Configuration.getInstance().getIntegerValue(ConfigurationOption.CONFIG_HTTP_SERVER_PORT, 8080);
		logger.info("Starting web server on port " + port);

		server = new Server(port);
		server.setGracefulShutdown(5000);
		server.setStopAtShutdown(true);
		server.setHandler(new RestHandler());
		server.start();

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
