package de.mfo.surfer;

import de.mfo.surfer.control.*;

import java.io.IOException;

import javafx.application.Application;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application
{
    private static final Logger logger = LoggerFactory.getLogger( Main.class );

    private static Group fxmlRoot;

    {
        try
        {
            fxmlRoot = ( Group ) new FXMLLoader().load(
                getClass().getResource( "fxml/surfer_touchscreen_1920_x_1080.fxml" )
            );
        }
        catch( IOException ioe )
        {
            throw new RuntimeException( ioe );
        }
    }

    public static < N extends Node > N fxmlLookup( String id )
    {
        return ( N ) fxmlRoot.lookup( id );
    }

    public static void main( String[] args )
    {
        Application.launch( Main.class, args );
    }

    @Override
    public void start( Stage stage ) throws Exception
    {
        stage.setTitle( "SURFER" );

        Group root = new Group();
        Group overlay = new Group();
        root.getChildren().setAll( fxmlRoot, overlay );

        Scene scene = new Scene( root, 192.0 * 6.0, 108.0 * 6.0 );

        Scale scale = new Scale( 1.0, 1.0, 0.0, 0.0 );
        NumberBinding scaleValue = new When(
                scene.widthProperty().multiply( 1080.0 ).greaterThan( scene.heightProperty().multiply( 1920.0 ) )
            )
            .then( scene.heightProperty().divide( 1080.0 ) )
            .otherwise( scene.widthProperty().divide( 1920.0 ) );
        scale.xProperty().bind( scaleValue );
        scale.yProperty().bind( scaleValue );

        root.getTransforms().add( scale );
        root.translateYProperty().bind( scene.heightProperty().subtract( scaleValue.multiply( 1080 ) ) );

        FormulaInputForm fif = new FormulaInputForm();
        SceneNodeSliderPanel snsp = new SceneNodeSliderPanel();

        overlay.getChildren().add( fif );
        overlay.getChildren().add( snsp );

        stage.setScene( scene );
        stage.show();
    }
}
