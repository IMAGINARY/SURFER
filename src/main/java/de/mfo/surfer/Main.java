package de.mfo.surfer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;


public class Main extends Application
{
    public static void main( String[] args )
    {
        Application.launch( Main.class, args );
    }

    @Override
    public void start( Stage stage ) throws Exception
    {
        stage.setTitle( "SURFER" );
        stage.setScene( new Scene( new Label( "Hello SURFER!" ) ) );
        stage.sizeToScene();
        stage.show();
    }
}
