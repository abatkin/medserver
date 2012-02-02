package net.batkin.forms.server.http;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocitySlf4jLogChute implements LogChute {

	private Logger logger;

	public VelocitySlf4jLogChute() {
		logger = LoggerFactory.getLogger(VelocitySlf4jLogChute.class);
	}

	@Override
	public void init(RuntimeServices rs) throws Exception {
	}

	@Override
	public void log(int level, String message) {
		switch (level) {
		case LogChute.WARN_ID:
			logger.warn(message);
			break;
		case LogChute.ERROR_ID:
			logger.error(message);
			break;
		case LogChute.INFO_ID:
			logger.info(message);
			break;
		case LogChute.DEBUG_ID:
			logger.debug(message);
			break;
		case LogChute.TRACE_ID:
			logger.trace(message);
			break;
		default:
			logger.info(message);
			break;
		}
	}

	@Override
	public void log(int level, String message, Throwable t) {
		switch (level) {
		case LogChute.WARN_ID:
			logger.warn(message, t);
			break;
		case LogChute.ERROR_ID:
			logger.error(message, t);
			break;
		case LogChute.INFO_ID:
			logger.info(message, t);
			break;
		case LogChute.DEBUG_ID:
			logger.debug(message, t);
			break;
		case LogChute.TRACE_ID:
			logger.trace(message, t);
			break;
		default:
			logger.info(message, t);
			break;
		}
	}

	@Override
	public boolean isLevelEnabled(int level) {
		switch (level) {
		case LogChute.ERROR_ID:
			return logger.isErrorEnabled();
		case LogChute.WARN_ID:
			return logger.isWarnEnabled();
		case LogChute.INFO_ID:
			return logger.isInfoEnabled();
		case LogChute.DEBUG_ID:
			return logger.isDebugEnabled();
		case LogChute.TRACE_ID:
			return logger.isTraceEnabled();
		default:
			return false;
		}
	}

}
