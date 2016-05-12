package de.mfo.surfer;

import de.mfo.surfer.control.*;
import de.mfo.surfer.gallery.*;
import de.mfo.surfer.util.FXUtils;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
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

    private FormulaInputForm fif;
    private SceneNodeSliderPanel snsp;
    private RenderArea ra;
    private MiscSceneNodeButtonPanel msnbp;
    private ColorPickerPanel cpp;
    private GallerySelector gs;
    private TabPanel tp;
    private Pane galleryIconContainer;
    private Pane introPageContainer;
    private Pane infoPageContainer;

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

            fif = new FormulaInputForm();
            snsp = new SceneNodeSliderPanel();
            ra = new RenderArea();
            msnbp = new MiscSceneNodeButtonPanel(
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
            );
            cpp = new ColorPickerPanel();

            galleryIconContainer = new javafx.scene.layout.TilePane();
            galleryIconContainer.setStyle( "-fx-background-color: red;" );
            FXUtils.resizeRelocateTo( galleryIconContainer, fxmlLookup( "#Gallery_Select" ) );
            introPageContainer = new Pane();
            introPageContainer.setStyle( "-fx-background-color: green;" );
            FXUtils.resizeRelocateTo( introPageContainer, fxmlLookup( "#Gallery_Text" ) );
            infoPageContainer = new Pane();

            gs = new GallerySelector(
                galleryIconContainer,
                introPageContainer,
                infoPageContainer,
                ra,
                this
            );
            tp = new TabPanel(
                gs,
                infoPageContainer,
                cpp
            );
            tp.activeTabIndexProperty().addListener( ( observable, oldValue, newValue ) -> {
                if( newValue.intValue() == 1 || newValue.intValue() == 2 )
                    setMode( Mode.RENDERING );
            } );

            fif.formulaProperty().bindBidirectional( ra.formulaProperty() );
            fif.isValidProperty().bind( ra.isValidProperty() );
            fif.errorMessageProperty().bind( ra.errorMessageProperty() );
            snsp.parametersProperty().bindBidirectional( ra.parametersProperty() );
            cpp.frontColorProperty().bindBidirectional( ra.frontColorProperty() );
            cpp.backColorProperty().bindBidirectional( ra.backColorProperty() );

            overlay.getChildren().add( fif );
            overlay.getChildren().add( msnbp );
            overlay.getChildren().add( snsp );
            overlay.getChildren().add( ra );
            overlay.getChildren().add( tp );
            overlay.getChildren().add( introPageContainer );
            overlay.getChildren().add( galleryIconContainer );

            setMode( Mode.RENDERING );

            stage.setScene( scene );
            stage.show();
        }
        catch( Throwable t )
        {
            handleUncaughtException( Thread.currentThread(), t );
        }
    }

    public enum Mode
    {
        RENDERING, GALLERY, INFO
    }

    public void setMode( Mode mode )
    {
        switch( mode )
        {
            case RENDERING:
                ra.setVisible( true );
                snsp.setVisible( true );
                galleryIconContainer.setVisible( false );
                introPageContainer.setVisible( false );
                break;
            case GALLERY:
                ra.setVisible( false );
                snsp.setVisible( false );
                galleryIconContainer.setVisible( true );
                introPageContainer.setVisible( true );
                tp.setActiveTabIndex( 0 );
                break;
            case INFO:
                ra.setVisible( true );
                snsp.setVisible( true );
                galleryIconContainer.setVisible( false );
                introPageContainer.setVisible( false );
                tp.setActiveTabIndex( 1 );
                break;
        }
    }
}
