package de.mfo.surfer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class WebConsole
{
    private final Logger consoleLogger;
    private static final HashMap<Class,WebConsole> consoles = new HashMap<>();

    public void log( Object o )
    {
        consoleLogger.debug( "{}", o );
    }

    private WebConsole( Class clazz ) {
        consoleLogger = LoggerFactory.getLogger( clazz );
    }

    public static WebConsole get() {

        return get( WebConsole.class );
    }

    public static WebConsole get( Class clazz ) {

        if( !consoles.containsKey( clazz ) )
            consoles.put( clazz, new WebConsole( clazz ) );
        return consoles.get( clazz );
    }
}
