package de.mfo.jsurfer;

public class FXMain
{
	public static void main( String[] args )
	{
		String[] args_FX = new String[ args.length + 1 ];
		args_FX[ 0 ] = "MainJavaFXScript=de.mfo.jsurfer.fxgui.Main";
		for( int i = 0; i < args.length; ++i )
			args_FX[ i + 1 ] = args[ i ];
		com.sun.javafx.runtime.main.Main.main( args_FX );
	}
}
