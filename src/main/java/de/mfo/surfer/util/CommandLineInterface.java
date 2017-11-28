package de.mfo.surfer.util;

import de.mfo.surfer.BuildConfig;
import javafx.beans.property.BooleanProperty;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import static de.mfo.surfer.util.LogLevel.*;

// TODO: order of cli options
// TODO: one positional argument for .jsurf file that should be rendered on startup
// TODO: option for template SVG file
// TODO: maybe options for all fields in Preferences.Generic
public class CommandLineInterface {

    private static final Logger logger = LoggerFactory.getLogger( CommandLineInterface.class );

    public enum Feature {
        PRINT( Preferences.Kiosk.showPrintButtonProperty(), false ),
        LOAD( Preferences.Kiosk.showLoadButtonProperty(), false ),
        SAVE( Preferences.Kiosk.showSaveButtonProperty(), false ),
        EXPORT( Preferences.Kiosk.showExportButtonProperty(), false ),
        SETTINGS( Preferences.Kiosk.showSettingsButtonProperty(), false ),
        FULLSCREEN( Preferences.Kiosk.fullScreenProperty(), false );

        private BooleanProperty enabledProperty;
        private boolean negate;

        private Feature(BooleanProperty enabledProperty, boolean negate)
        {
            this.enabledProperty = enabledProperty;
            this.negate = negate;
        }

        public void enable() { enabledProperty.set( !negate ); }
        public void disable() { enabledProperty.set( negate ); }
        public void setEnabled( boolean enabled ) { enabledProperty.set( enabled ^ negate ); };

    }

    private static Map<Option,Consumer<String[]>> optionsAndActions;
    private static Options options;
    private static int HELP_WIDTH = 103;

    public static Map<Option,Consumer<String[]>> getOptionsAndActions() {
        if( optionsAndActions == null ) {
            Option help = Option.builder().longOpt("help").desc("display this help text and exit").build();
            Option version = Option.builder().longOpt("version").desc("print program version and exit").build();
            Option disable = Option.builder().longOpt("disable").hasArgs().valueSeparator(',').desc("disable certain features").build();
            Option enable = Option.builder().longOpt("enable").hasArgs().valueSeparator(',').desc("enable certain features").build();
            Option fullscreen = Option.builder("f").longOpt("fullscreen").desc("run in full screen mode").build();
            Option kiosk = Option.builder().longOpt("kiosk").desc("run in kiosk mode").build();
            Option verbose = Option.builder("v").longOpt("verbose").desc("increase verbosity level").build();

            options = new Options();
            options.addOption( help );
            options.addOption( version );
            options.addOption( fullscreen );
            options.addOption( kiosk );
            options.addOption( enable );
            options.addOption( disable );
            options.addOption( verbose );

            optionsAndActions = new TreeMap<>(Comparator.comparing(o -> (o.getOpt() != null ? o.getOpt() : o.getLongOpt())));
            optionsAndActions.put( help, v -> printHelp() );
            optionsAndActions.put( version, v -> printVersion() );
            optionsAndActions.put( disable, v -> Arrays.stream(v).forEach( CommandLineInterface::disableFeature ) );
            optionsAndActions.put( enable, v -> Arrays.stream(v).forEach( CommandLineInterface::enableFeature ) );
            optionsAndActions.put( fullscreen, v -> Preferences.Kiosk.fullScreenProperty().setValue( true ) );
            optionsAndActions.put( kiosk, v -> enableKiosk() );
            optionsAndActions.put( verbose, v -> increaseVerbosityLevel() );
        }
        return optionsAndActions;
    }

    public static Options getOptions()
    {
        if( options == null )
            getOptionsAndActions();
        return options;
    }

    public static void parse(String[] args )
        throws ParseException
    {
        CommandLine cl = new DefaultParser().parse( getOptions(), args, true );

        for( Option o : cl.getOptions() )
            logger.debug( "{}", o );

        Arrays.stream( cl.getOptions() ).forEach( o -> getOptionsAndActions().get( o ).accept( o.getValues() ) );
    }

    public static void printHelp() {
        printHelp( new PrintWriter( System.out ), 0 );
    }

    public static void printHelpOnError() {
        printHelp( new PrintWriter( System.err ), -1 );
    }

    public static void printHelp( PrintWriter writer, int exitCode ) {
        final HelpFormatter formatter = new HelpFormatter();
        final String cmd_line_syntax = BuildConfig.NAME;
        final String help_header = "\n" + BuildConfig.NAME + " is a renderer for real algebraic surfaces.\n\n";
        final String help_footer = "";

        formatter.printHelp(
            writer,
            HELP_WIDTH,
            cmd_line_syntax,
            help_header,
            getOptions(),
            formatter.getLeftPadding(),
            formatter.getDescPadding(),
            help_footer, true );

        writer.flush();

        System.exit( exitCode );
    }

    public static void printVersion() {
        System.out.println( BuildConfig.NAME + " " + BuildConfig.VERSION );
        System.exit( 0 );
    }

    public static void enableKiosk() {
        Feature.FULLSCREEN.enable();
        Feature.LOAD.disable();
        Feature.SAVE.disable();
        Feature.EXPORT.disable();
        Feature.SETTINGS.disable();
    }

    public static void enableFeature( String feature ) { setFeatureEnabled( feature, true ); }
    public static void disableFeature( String feature ) { setFeatureEnabled( feature, false ); }
    public static void setFeatureEnabled( String feature, boolean enabled )
    {
        try {
            Feature.valueOf( feature.toUpperCase() ).setEnabled( enabled );
        }
        catch( IllegalArgumentException iae ) {
            System.err.println("Unknown feature: " + feature);
            System.exit(-1);
        }
    }

    public static void increaseVerbosityLevel()
    {
        switch( Preferences.Developer.logLevelProperty().get() ) {
            case OFF:
                Preferences.Developer.logLevelProperty().set( ERROR );
                break;
            case ERROR:
                Preferences.Developer.logLevelProperty().set( WARN );
                break;
            case WARN:
                Preferences.Developer.logLevelProperty().set( INFO );
                break;
            case INFO:
                Preferences.Developer.logLevelProperty().set( DEBUG );
                break;
            case DEBUG:
                Preferences.Developer.logLevelProperty().set( TRACE );
                break;
            case TRACE:
            default:
                Preferences.Developer.logLevelProperty().set( ALL );
                break;
        }
    }
}
