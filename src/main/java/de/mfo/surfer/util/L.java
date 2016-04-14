package de.mfo.surfer.util;


import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.ObservableMap;
import javafx.collections.FXCollections;
import java.util.HashMap;
import java.util.Locale;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L
{
    private static final Logger logger = LoggerFactory.getLogger( L.class );

    private static ObjectProperty< Locale > locale;

    private static ObservableMap< String, String > localizedNames;

    private static L instance;

    static {
        // initialize the localization map
        localizedNames = FXCollections.observableMap( new HashMap< String, String >() );

        // set default locale
        locale = new SimpleObjectProperty< Locale >( null );

        // bind to locale change
        locale.addListener( ( observable, oldValue, newValue ) ->
            {
                // set as JVM-wide default locale
                Locale.setDefault( newValue );

                // to be localized via external message bundle
                localizedNames.put( "arithmeticOperations", "Arithmetic operations" );
                localizedNames.put( "colors", "Colours" );
                localizedNames.put( "info", "Info" );
                localizedNames.put( "language", "English" );
                localizedNames.put( "parameters", "Parameters" );
                localizedNames.put( "start", "Start" );
                localizedNames.put( "variables", "Variables" );
                localizedNames.put( "zoom", "Zoom" );

                // not to be localized
                localizedNames.put( "a", "a" );
                localizedNames.put( "b", "b" );
                localizedNames.put( "c", "c" );
                localizedNames.put( "d", "d" );

                // TODO: read external message bundles and set localized strings
            }
        );

        locale.setValue( Locale.US );
    }

    private L() {}

    // just in case you need to pass around an object reference
    // intead of directly using the static member functions
    public L getInstance()
    {
        if( instance == null )
            instance = new L();
        return instance;
    }

    public static Locale getLocale()
    {
        return locale.get();
    }

    public static void setLocale( Locale value )
    {
        locale.set( value );
    }

    /**
     * Bind to this property to check for locale changes.
     */
    public static ObjectProperty< Locale > localeProperty()
    {
        return locale;
    }

    public static String l( String key )
    {
        return localizedNames.get( key );
    }

    public static StringBinding lb( String key )
    {
        return Bindings.stringValueAt( localizedNames, key );
    }

    public static StringBinding lb( ObservableStringValue key )
    {
        return Bindings.stringValueAt( localizedNames, key );
    }
}
