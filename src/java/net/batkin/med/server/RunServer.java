package net.batkin.med.server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.batkin.med.server.DBAccess.DatabaseCollection;
import net.batkin.med.server.configuration.Configuration;
import net.batkin.med.server.configuration.Configuration.ConfigurationSource;
import net.batkin.med.server.configuration.ConfigurationException;
import net.batkin.med.server.configuration.ConfigurationLoader;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class RunServer {

	private static Server server;

	public static void main(String[] args) throws Exception {
		try {
			loadInitialConfigurations(args);
			DBAccess.connect();
			DBAccess.createDatabase();
			fetchDbConfig();
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

	private static void fetchDbConfig() {
		DBCollection c = DBAccess.getCollection(DatabaseCollection.Configs);
		DBObject server = new BasicDBObject();
		server.put("serverName", "server");
		DBObject obj = c.findOne(server);
		if (obj == null) {
			return;
		}

		Map<String, List<String>> newValues = new HashMap<String, List<String>>();
		for (String key : obj.keySet()) {
			Object val = obj.get(key);
			if (val instanceof String) {
				newValues.put(key, Collections.singletonList((String) val));
			} else if (val instanceof List) {
				List<String> values = new ArrayList<String>();
				for (Object listObj : (List<?>) val) {
					values.add(listObj.toString());
				}
				newValues.put(key, values);
			}
		}

		Configuration.getInstance().addValues(ConfigurationSource.Database, newValues);
	}

	private static void loadInitialConfigurations(String[] args) throws ConfigurationException {
		Configuration config = Configuration.getInstance();
		ConfigurationLoader.parseCommandLine(args);

		if (config.containsKey("config")) {
			Properties props = loadProperties(config.getValue("config"));
			ConfigurationLoader.parseProperties(props);
		}
	}

	private static void createServer() throws Exception {
		Logger logger = LoggerFactory.getLogger(RunServer.class);

		int port = Configuration.getInstance().getIntegerValue("port", 8080);
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
