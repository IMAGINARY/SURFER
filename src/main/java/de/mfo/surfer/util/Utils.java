package de.mfo.surfer.util;

import java.util.concurrent.Callable;

public class Utils
{
    public static < T > T wrapInRte( Callable< T > callable )
    {
        try
        {
            return callable.call();
        }
        catch( Exception e )
        {
            if( RuntimeException.class.isAssignableFrom( e.getClass() ) )
                throw ( RuntimeException ) e;
            else
                throw new RuntimeException( e );
        }
    }
}
