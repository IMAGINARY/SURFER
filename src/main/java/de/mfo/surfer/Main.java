package de.mfo.surfer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

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
        Parent fxmlRoot = ( Parent ) new FXMLLoader().load(
            getClass().getResource( "fxml/surfer_touchscreen_1920_x_1080.fxml" )
        );
        stage.setScene( new Scene( fxmlRoot ) );
        stage.sizeToScene();
        stage.show();
    }
}
