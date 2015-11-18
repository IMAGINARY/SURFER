package de.mfo.jsurfer;

import org.apache.commons.cli.*;

public class FXMain
{
	public static void main( String[] args )
	{
		String surfer_progname = "surfer";
		String jsurf_filename = "";
    	String output_filename = null;

    	Options options = new Options();

		options.addOption( Option.builder().longOpt( "help" ).desc( "display this help text" ).build() );
		options.addOption( Option.builder().longOpt( "version" ).desc( "print program version" ).build() );

    	CommandLineParser parser = new PosixParser();
		HelpFormatter formatter = new HelpFormatter();
    	String cmd_line_syntax = surfer_progname + " [options] [jsurf_file]\n\n";
    	String help_header = FXMain.class.getPackage().getImplementationTitle() + " is an interactive renderer for real algebraic surfaces.";
    	String help_footer = "";
		try
    	{
    		CommandLine cmd = parser.parse( options, args );

			if( cmd.hasOption( "help" ) )
			{
    			formatter.printHelp( cmd_line_syntax, help_header, options, help_footer );
    			return;
    		}

			if( cmd.hasOption( "version" ) )
			{
    			System.out.println( FXMain.class.getPackage().getImplementationVersion() );
    			return;
    		}

			String[] args_FX = new String[ cmd.getArgs().length + 1 ];
			args_FX[ 0 ] = "MainJavaFXScript=de.mfo.jsurfer.fxgui.Main";
			for( int i = 0; i < cmd.getArgs().length; ++i )
				args_FX[ i + 1 ] = cmd.getArgs()[ i ];
			com.sun.javafx.runtime.main.Main.main( args_FX );
    	}
    	catch( ParseException exp ) {
    	    System.out.println( "Unexpected exception:" + exp.getMessage() );
    	    System.exit( -1 );
    	}
	}
}
