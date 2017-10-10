package de.mfo.surfer.util;

import de.mfo.surfer.gallery.Gallery;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
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

                localizedNames.put( "loadScene", "Load scene" );
                localizedNames.put( "saveScene", "Save scene" );
                localizedNames.put( "exportImage", "Export image" );
                localizedNames.put( "fileExtensionFilterSURFER", "SURFER files" );
                localizedNames.put( "fileExtensionFilterPNG", "PNG files" );
                localizedNames.put( "fileExtensionFilterJPG", "JPEG files" );
                localizedNames.put( "fileExtensionFilterAll", "All files" );

                localizedNames.put( "preferences", "Preferences" );

                // labels to be used in the preferences window
                for( Class<?> cls : Preferences.class.getDeclaredClasses() )
                {
                    if( Modifier.isStatic( cls.getModifiers() ) )
                    {
                        String prefix = Preferences.class.getSimpleName() + "." + cls.getSimpleName();
                        localizedNames.put( prefix, cls.getSimpleName() );
                        for( Method m : cls.getDeclaredMethods() )
                        {
                            if( Modifier.isStatic( m.getModifiers() ) && m.getName().endsWith( "Property" ) )
                            {
                                String propertyName = m.getName().substring( 0, m.getName().length() - "Property".length() );
                                String key = prefix + "." + propertyName;
                                localizedNames.put( key, propertyName );
                            }
                        }
                    }
                }

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

    private static TreeSet< Locale > availableLocales;
    public static Set< Locale > getAvailableLocales()
    {
        if( availableLocales == null )
        {
            availableLocales = new TreeSet<>(
                new Comparator< Locale >()
                {
                    @Override
                    public int compare( Locale l1, Locale l2 )
                    {
                        int lComp = l( l1, "language" ).compareTo( l( l2, "language" ) );
                        if( lComp == 0 )
                            return l1.toString().compareTo( l2.toString() );
                        else
                            return lComp;
                    }
                }
            );
            availableLocales.addAll( Gallery.getAvailableLocales() );
        }
        return availableLocales;
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

    public static String l( Locale locale, String key )
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
