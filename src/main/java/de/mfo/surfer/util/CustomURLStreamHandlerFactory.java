package de.mfo.surfer.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomURLStreamHandlerFactory implements URLStreamHandlerFactory
{
    private static final Logger logger = LoggerFactory.getLogger( CustomURLStreamHandlerFactory.class );

    public URLStreamHandler createURLStreamHandler(String protocol)
    {
        return protocol.equalsIgnoreCase( JsInJarURLStreamHandler.PROTOCOL ) ? new JsInJarURLStreamHandler() : null;
    }
}

class JsInJarURLStreamHandler extends URLStreamHandler
{
    private static final Logger logger = LoggerFactory.getLogger( JsInJarURLStreamHandler.class );

    public static final String PROTOCOL = "jsinjar";

    @Override
    protected URLConnection openConnection(URL url) throws IOException
    {
        // remove the custom protocol handler and possible query string
        String sanitizedUrl = url.toExternalForm().substring((PROTOCOL+":/").length()).split("\\?", 2 )[0];
        return JsInJarURLStreamHandler.class.getClassLoader().getResource( sanitizedUrl ).openConnection();
    }
}
