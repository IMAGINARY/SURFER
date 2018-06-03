package de.mfo.surfer;

import de.mfo.surfer.control.*;
import de.mfo.surfer.gallery.*;
import de.mfo.surfer.util.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.function.Consumer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main extends Application
{
    static
    {
        // set log level
        Preferences.Developer.logLevelProperty().get().apply();
        Preferences.Developer.logLevelProperty().addListener( (o,ov,nv) -> nv.apply() );
    }

    private static final Logger logger = LoggerFactory.getLogger( Main.class );

    private static Group fxmlRoot;

    {
        try
        {
            fxmlRoot = ( Group ) FXMLLoader.load(
                getClass().getResource( "/de/mfo/surfer/fxml/surfer_touchscreen_1920_x_1080.fxml" )
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
    private TilePane galleryIconContainer;
    private GalleryInfoPage galleryIntroPage;
    private GalleryInfoPage galleryInfoPage;

    private IdleMonitor idleMonitor;

    private static Main instance; // useful to access in a debugger

    @Override
    public void start( Stage stage ) throws Exception
    {
        // Keep the application reference for debugging purposes
        Main.instance = this;

        // set custom protocol handler, especially to deal with webjar based JS libraries (MathJax, Snap.svg)
        URL.setURLStreamHandlerFactory(new CustomURLStreamHandlerFactory());

        Thread.currentThread().setUncaughtExceptionHandler( Main::handleUncaughtException );

        CommandLineInterface.parse( getParameters().getRaw().toArray( new String[0] ) );

        stage.setTitle( "SURFER" );

        stage.setFullScreenExitHint( "" );
        stage.setFullScreen( Preferences.Kiosk.fullScreenProperty().get() );
        Preferences.Kiosk.fullScreenProperty().addListener( ( o, ov, nv ) -> stage.setFullScreen( nv ) );

        try
        {
            Group scaledOverlay = new Group();
            scaledOverlay.setId( "scaled-overlay" );

            Group overlay = new Group();

            Scene scene = new Scene( new Group( fxmlRoot, overlay, scaledOverlay ), 192.0 * 6.0, 108.0 * 6.0 );
            scene.getStylesheets().add( Main.class.getResource( "/de/mfo/surfer/css/style.css" ).toExternalForm() );

            // hide mouse cursor if necessary
            Runnable setCursor = () -> {
                logger.debug("Set mouse cursor to {}", Preferences.Kiosk.hideCursorProperty().get() ? Cursor.NONE : Cursor.DEFAULT);
                scene.setCursor(Cursor.DEFAULT);
                Platform.runLater(() -> scene.setCursor(Preferences.Kiosk.hideCursorProperty().get() ? Cursor.NONE : Cursor.DEFAULT));
            };
            Preferences.Kiosk.hideCursorProperty().addListener((o,ov,nv)->setCursor.run());
            stage.focusedProperty().addListener((o,ov,nv)->{if(nv) setCursor.run();});
            scene.setOnMouseEntered(e->setCursor.run());

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

            scaledOverlay.getTransforms().add( scale );
            scaledOverlay.translateYProperty().bind( scene.heightProperty().subtract( scaleValue.multiply( 1080 ) ) );

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
                        setMode( Mode.COLORS );
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
                () -> {
                        // TODO: render in a background thread and send the result to the print dialog when it is ready (maybe use a low resolution while rendering)
                        File tempFile = Utils.wrapInRte( () -> Files.createTempFile("SURFER-", ".png" ) ).toFile();
                        tempFile.deleteOnExit();
                        ra.export( tempFile, 512 );

                        new PrintDialog( stage, fif.getFormula(), snsp.getParameters(), tempFile.toURI().toString() ).showAndWait();
                    }
            );
            cpp = new ColorPickerPanel();

            galleryIconContainer = new javafx.scene.layout.TilePane();
            galleryIconContainer.getStyleClass().add( "galleryIconContainer" );
            FXUtils.resizeRelocateTo( galleryIconContainer, fxmlLookup( "#Gallery_Select" ) );

            ImageView galleryIntroCanvas = new ImageView();
            galleryIntroPage = new GalleryInfoPage( galleryIntroCanvas );
            FXUtils.resizeRelocateTo( galleryIntroPage, fxmlLookup( "#Gallery_Text" ) );

            ImageView galleryInfoCanvas = new ImageView();
            galleryInfoPage = new GalleryInfoPage( galleryInfoCanvas );

            gs = new GallerySelector(
                galleryIconContainer.getChildren(),
                galleryIntroPage,
                galleryInfoPage,
                ra,
                this
            );
            tp = new TabPanel(
                gs,
                galleryInfoPage,
                cpp
            );
            tp.activeTabIndexProperty().addListener( ( observable, oldValue, newValue ) -> {
                switch( newValue.intValue() ) {
                    case 0: setMode( Mode.GALLERY ); break;
                    case 1: setMode( Mode.INFO ); break;
                    case 2: setMode( Mode.COLORS ); break;
                }
            } );

            ra.load( Preferences.General.initialJSurfFileProperty().get() );

            fif.formulaProperty().bindBidirectional( ra.formulaProperty() );
            fif.isValidProperty().bind( ra.isValidProperty() );
            fif.errorMessageProperty().bind( ra.errorMessageProperty() );
            snsp.parametersProperty().bindBidirectional( ra.parametersProperty() );
            cpp.frontColorProperty().bindBidirectional( ra.frontColorProperty() );
            cpp.backColorProperty().bindBidirectional( ra.backColorProperty() );

            scaledOverlay.getChildren().add( fif );
            scaledOverlay.getChildren().add( msnbp );
            scaledOverlay.getChildren().add( snsp );
            scaledOverlay.getChildren().add( tp );
            scaledOverlay.getChildren().add( galleryIntroPage );
            scaledOverlay.getChildren().add( galleryIconContainer );

            overlay.getChildren().add( galleryIntroCanvas );
            overlay.getChildren().add( galleryInfoCanvas );
            overlay.getChildren().add( ra );

            gs.selectGallery( Gallery.Type.values()[ 0 ] );
            setMode( Mode.COLORS );

            stage.setScene( scene );
            stage.show();

            idleMonitor = new IdleMonitor(Duration.INDEFINITE,this::onIdleTimeOut,true);
            idleMonitor.register(scene, Event.ANY);
            idleMonitor.idleTimeOutProperty().bind(Preferences.Kiosk.idleTimeOutProperty());
        }
        catch( Throwable t )
        {
            handleUncaughtException( Thread.currentThread(), t );
        }
    }

    public void onIdleTimeOut()
    {
        gs.selectGallery( Gallery.Type.values()[ 0 ] );
        setMode( Mode.GALLERY );
    }

    public enum Mode
    {
        RENDERING, GALLERY, INFO, COLORS;
    }

    public void setMode( Mode mode )
    {
        Consumer< Boolean > disableButtons = disable -> msnbp.setDisable( disable,
            MiscSceneNodeButtonPanel.ButtonType.SAVE,
            MiscSceneNodeButtonPanel.ButtonType.EXPORT,
            MiscSceneNodeButtonPanel.ButtonType.PRINT
        );

        switch( mode )
        {
            case RENDERING:
                ra.setVisible( true );
                snsp.setVisible( true );
                galleryIconContainer.setVisible( false );
                galleryIntroPage.setVisible( false );
                disableButtons.accept( false );
                fif.setDisable( false );
                break;
            case GALLERY:
                ra.setVisible( false );
                snsp.setVisible( false );
                galleryIconContainer.setVisible( true );
                galleryIntroPage.setVisible( true );
                disableButtons.accept( true );
                fif.setDisable( true );
                tp.setActiveTabIndex( 0 );
                break;
            case INFO:
                ra.setVisible( true );
                snsp.setVisible( true );
                galleryIconContainer.setVisible( false );
                galleryIntroPage.setVisible( false );
                disableButtons.accept( false );
                fif.setDisable( false );
                tp.setActiveTabIndex( 1 );
                break;
            case COLORS:
                ra.setVisible( true );
                snsp.setVisible( true );
                galleryIconContainer.setVisible( false );
                galleryIntroPage.setVisible( false );
                disableButtons.accept( false );
                fif.setDisable( false );
                tp.setActiveTabIndex( 2 );
                break;
        }
    }
}
