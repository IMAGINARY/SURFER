package de.mfo.surfer.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public enum LogLevel {
    OFF( Level.OFF ),
    ERROR( Level.ERROR ),
    WARN( Level.WARN ),
    INFO( Level.INFO ),
    DEBUG( Level.DEBUG ),
    TRACE( Level.TRACE ),
    ALL( Level.ALL );

    private Level level;
    LogLevel( Level level )
    {
        this.level = level;
    }

    public void apply()
    {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(level);
    }

    public static LogLevel getCurrentLogLevel()
    {
        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        return LogLevel.valueOf( root.getLevel().toString() );
    }
}
