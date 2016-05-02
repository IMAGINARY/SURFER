package de.mfo.surfer;

import de.mfo.surfer.control.*;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
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

    public static void handleUncaughtException( Thread thread, Throwable throwable )
    {
        if( Platform.isFxApplicationThread() )
        {
            logger.error( "Uncaught exception in JavaFX application thread", throwable );
            Platform.runLater(
                () ->
                new ExceptionDialog(
                    Alert.AlertType.ERROR,
                    "Uncaught exception in JavaFX application thread",
                    "Please report the details of the error to the developers this software.",
                    throwable
                ).showAndWait()
            );
        }
        else
        {
            throw new RuntimeException( throwable );
        }
    }

    @Override
    public void start( Stage stage ) throws Exception
    {
        Thread.currentThread().setUncaughtExceptionHandler( Main::handleUncaughtException );

        stage.setTitle( "SURFER" );

        try
        {
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
            RenderArea ra = new RenderArea();
            MiscSceneNodeButtonPanel msnbp = new MiscSceneNodeButtonPanel(
                f ->
                {
                    logger.debug( "Open {}", f );
                    try
                    {
                        ra.load( f );
                    }
                    catch( Exception e )
                    {
                        new ExceptionDialog( Alert.AlertType.ERROR, "Error loading " + f.toString(), e ).showAndWait();
                    }
                },
                f ->
                {
                    try
                    {
                        ra.store( f );
                    }
                    catch( Exception e )
                    {
                        new ExceptionDialog( Alert.AlertType.ERROR, "Error saving to " + f.toString(), e ).showAndWait();
                    }
                },
                f ->
                {
                    try
                    {
                        ra.export( f );
                    }
                    catch( Exception e )
                    {
                        new ExceptionDialog( Alert.AlertType.ERROR, "Error exporting to " + f.toString(), e ).showAndWait();
                    }
                },
                p -> logger.debug( "Printing ..." )
            TabPanel tp = new TabPanel(
                new javafx.scene.layout.Pane( new javafx.scene.control.Label( "gallery" ) ),
                new javafx.scene.layout.Pane( new javafx.scene.control.Label( "info" ) ),
                new javafx.scene.layout.Pane( new javafx.scene.control.Label( "color" ) )
            );

            fif.formulaProperty().bindBidirectional( ra.formulaProperty() );
            fif.isValidProperty().bind( ra.isValidProperty() );
            fif.errorMessageProperty().bind( ra.errorMessageProperty() );
            snsp.parametersProperty().bindBidirectional( ra.parametersProperty() );

            overlay.getChildren().add( fif );
            overlay.getChildren().add( msnbp );
            overlay.getChildren().add( snsp );
            overlay.getChildren().add( ra );
            overlay.getChildren().add( tp );

            stage.setScene( scene );
            stage.show();
        }
        catch( Throwable t )
        {
            handleUncaughtException( Thread.currentThread(), t );
        }
    }
}
