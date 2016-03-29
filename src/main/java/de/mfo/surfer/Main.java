package de.mfo.surfer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.transform.Scale;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;

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

        Scene scene = new Scene( fxmlRoot, 192.0 * 6.0, 108.0 * 6.0 );

        Scale scale = new Scale( 1.0, 1.0, 0.0, 0.0 );
        NumberBinding scaleValue = new When(
                scene.widthProperty().multiply( 1080.0 ).greaterThan( scene.heightProperty().multiply( 1920.0 ) )
            )
            .then( scene.heightProperty().divide( 1080.0 ) )
            .otherwise( scene.widthProperty().divide( 1920.0 ) );
        scale.xProperty().bind( scaleValue );
        scale.yProperty().bind( scaleValue );

        fxmlRoot.getTransforms().add( scale );
        fxmlRoot.translateYProperty().bind( scene.heightProperty().subtract( scaleValue.multiply( 1080 ) ) );

        stage.setScene( scene );
        stage.show();
    }
}
