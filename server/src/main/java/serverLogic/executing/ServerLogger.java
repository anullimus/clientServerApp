package serverLogic.executing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLogger {
    private static final Logger logger = LogManager.getLogger(ServerLogger.class);

    private ServerLogger() {
    }

    public static void logInfoMessage(String format, Object... arg) {
        logger.info(format, arg);
    }

    public static void logDebugMessage(String format, Object... arg) {
        logger.debug(format, arg);
    }

    public static void logErrorMessage(String format, Object... arg) {
        logger.error(format, arg);
    }
}
