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
        return protocol.equalsIgnoreCase( FileInJarURLStreamHandler.PROTOCOL ) ? new FileInJarURLStreamHandler() : null;
    }
}

class FileInJarURLStreamHandler extends URLStreamHandler
{
    private static final Logger logger = LoggerFactory.getLogger( FileInJarURLStreamHandler.class );

    public static final String PROTOCOL = "fileinjar";

    @Override
    protected URLConnection openConnection(URL url) throws IOException
    {
        // remove the custom protocol handler and possible query string
        String sanitizedUrl = url.toExternalForm().substring((PROTOCOL+":/").length()).split("\\?", 2 )[0];
        return FileInJarURLStreamHandler.class.getClassLoader().getResource( sanitizedUrl ).openConnection();
    }
}
