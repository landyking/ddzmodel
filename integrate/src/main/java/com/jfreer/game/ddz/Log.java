package com.jfreer.game.ddz;

import org.apache.commons.logging.LogFactory;

/**
 * Created by landy on 2015/3/7.
 */
public class Log {
    public static final org.apache.commons.logging.Log logger = LogFactory.getLog(Log.class);

    public static void warn(String s) {
        logger.warn(s);
    }

    public static void info(String s) {
        logger.info(s);
    }

    public static void info(String s, Object... args) {
        logger.info(String.format(s, args));
    }
    public static void debug(String s) {
        logger.debug(s);
    }

    public static void debug(String s, Object... args) {
        logger.debug(String.format(s, args));
    }
}
