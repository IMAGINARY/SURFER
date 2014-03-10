package de.mfo.jsurfer.gui;

import java.util.*;
import java.io.*;

public class Options {

    public static final boolean fullScreen;
    public static final boolean hideCursor;
    public static final boolean showPrintButton;
    public static final boolean showLoadButton;
    public static final boolean showSaveButton;
    public static final boolean showExportButton;
    
    public static final List<String> languages;
    
    public static final int clickMode;

    public static final String printMsg;
    public static final String printExportDir;
    public static final String printCmd;

    static
    {
        Properties config = readConfigFile();

        fullScreen = Boolean.parseBoolean( config.getProperty( "fullScreen" ) );
        hideCursor = Boolean.parseBoolean( config.getProperty( "hideCursor" ) );
        showPrintButton = Boolean.parseBoolean( config.getProperty( "showPrintButton" ) );
        showLoadButton = Boolean.parseBoolean( config.getProperty( "showLoadButton" ) );
        showSaveButton = Boolean.parseBoolean( config.getProperty( "showSaveButton" ) );
        showExportButton = Boolean.parseBoolean( config.getProperty( "showExportButton" ) );
        
        languages = Collections.unmodifiableList( Arrays.asList( config.getProperty( "languages" ).replaceAll( " ", "" ).split( "," ) ) );
        
        clickMode = Integer.parseInt( config.getProperty( "clickMode" ) );

        printExportDir = config.getProperty( "printExportDir" );
        printCmd = config.getProperty( "printCmd" );
        printMsg = config.getProperty( "printMsg" );
    }

    private static Properties readConfigFile()
    {
        // load default configuration settings
        Properties defaultConfig = new java.util.Properties();
        try
        {
            defaultConfig.load( Options.class.getResourceAsStream( "config.properties" ) );
        }
        catch( IOException ioe )
        {
            System.err.println( "Unable to load default configuration config.properties" );
        }

        // overwrite with user specific settings
        Properties userConfig = new Properties();
        userConfig.putAll( defaultConfig );

        File userConfigFile = new File( System.getProperty("user.home") + File.separator + ".fxsurfer" ).getAbsoluteFile();
        try
        {
            userConfig.load( new FileInputStream( userConfigFile ) );
        }
        catch( Exception e )
        {
            System.err.println( "Unable to load config file \"" + userConfigFile.getName() + "\"");
            System.err.println( "Writing default config to  \"" + userConfigFile.getName() + "\"");
            try
            {
                FileOutputStream fos = new FileOutputStream(userConfigFile);
                InputStream is = Options.class.getResourceAsStream( "config.properties" );
                int aByte;
                while ((aByte = is.read()) != -1) {
                     fos.write(aByte);
                }
                fos.close();
                is.close();
            }
            catch( Exception e2 )
            {
                System.err.println( "Unable to write default config to \"" + userConfigFile.getName() + "\"");
            }
        }

        // only allow keys listed in the default config
        userConfig.keySet().retainAll( defaultConfig.keySet() );

        // cope with deprecated settings
        {
            String s;

            s = getProperty( "de.mfo.jsurfer.gui.fullscreen" );
            if( s != null )
                userConfig.setProperty( "fullScreen", s ); // note the upper case 'S'

            s = getProperty( "de.mfo.jsurfer.gui.hideCursor" );
            if( s != null )
                userConfig.setProperty( "hideCursor", s );

            s = getProperty( "de.mfo.jsurfer.gui.showLoadSaveButton" );
            if( s != null )
            {
                userConfig.setProperty( "showLoadButton", s );
                userConfig.setProperty( "showSaveButton", s );
            }

            s = getProperty( "de.mfo.jsurfer.gui.showPrintButton" );
            if( s != null )
                userConfig.setProperty( "showPrintButton", s );

            s = getProperty( "de.mfo.jsurfer.gui.showExportButton" );
            if( s != null )
                userConfig.setProperty( "showExportButton", s );

            s = getProperty( "de.mfo.jsurfer.gui.languages" );
            if( s != null )
                userConfig.setProperty( "languages", s );

            s = getProperty( "de.mfo.jsurfer.gui.clickMode" );
            if( s != null )
                userConfig.setProperty( "clickMode", s );
            
            s = getProperty( "de.mfo.jsurfer.gui.printMessage" );
            if( s != null )
                userConfig.setProperty( "printMsg", s );
        }

        return userConfig;
    }

    private static String getProperty( String name )
    {
        try
        {
            return java.lang.System.getProperty( name );
        }
        catch( java.security.AccessControlException e )
        {
            System.err.println( "unable to system property \"" + name + "\" due to " + e );
        }
        return null;
    }
}
