package de.mfo.surfer.control;

import de.mfo.surfer.control.SceneNodeButton;
import de.mfo.surfer.Main;
import java.io.File;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static de.mfo.surfer.util.L.l;

public class MiscSceneNodeButtonPanel extends Region
{
    private static final Logger logger = LoggerFactory.getLogger( MiscSceneNodeButtonPanel.class );

    PreferenceDialog prefsDialog;
    Credits credits;

    public MiscSceneNodeButtonPanel(
        Consumer< File > fileOpenAction,
        Consumer< File > fileSaveAction,
        Consumer< File > fileExportAction,
        Consumer< PrinterJob > printAction
    )
    {
        setPickOnBounds( false );

        prefsDialog = new PreferenceDialog();
        credits = new Credits();
        credits.setOnMouseClicked( e -> ( ( Group ) credits.getParent() ).getChildren().removeAll( credits ) );

        getChildren().add( createButton( "Preferences", e -> handlePreferences() ) );
        getChildren().add( createButton( "Open_File", e -> handleFileOpen( fileOpenAction ) ) );
        getChildren().add( createButton( "Save_File", e -> handleFileSave( fileSaveAction ) ) );
        getChildren().add( createButton( "Export", e -> handleFileExport( fileExportAction ) ) );
        getChildren().add( createButton( "Print", e -> handlePrint( printAction ) ) );
        getChildren().add( createButton( "Imprint", e -> handleCredits() ) );
        getChildren().add( createButton( "Language", e -> handleChangeLanguage() ) );
    }

    static SceneNodeButton createButton( String suffix, EventHandler< ActionEvent > handler )
    {
        SceneNodeButton result = new SceneNodeButton(
            Main.fxmlLookup( "#Button_" + suffix ),
            Main.fxmlLookup( "#Button_Over_" + suffix ),
            Main.fxmlLookup( "#Button_Pressed_" + suffix )
        );
        result.setOnAction( handler );
        return result;
    }

    static FileChooser.ExtensionFilter createExtensionFilter( String descriptionKey, String... extensions )
    {
        return new FileChooser.ExtensionFilter(
            l( descriptionKey ) + " (" + String.join( ",", extensions ) + ")",
            extensions
        );
    }

    protected void handleFileOpen( Consumer< File > fileOpenAction )
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( l( "loadScene" ) );
        fileChooser.getExtensionFilters().addAll(
            createExtensionFilter( "fileExtensionFilterSURFER", "*.jsurf" ),
            createExtensionFilter( "fileExtensionFilterAll", "*.*" )
        );
        File selectedFile = fileChooser.showOpenDialog( getScene().getWindow() );
        if (selectedFile != null )
            fileOpenAction.accept( selectedFile );
    }

    protected void handleFileSave( Consumer< File > fileSaveAction )
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( l( "saveScene" ) );
        fileChooser.getExtensionFilters().addAll(
            createExtensionFilter( "fileExtensionFilterSURFER", "*.jsurf" ),
            createExtensionFilter( "fileExtensionFilterAll", "*.*" )
        );
        File selectedFile = fileChooser.showSaveDialog( getScene().getWindow() );
        if (selectedFile != null )
            fileSaveAction.accept( selectedFile );
    }

    protected void handleFileExport( Consumer< File > fileExportAction )
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( l( "exportImage" ) );
        fileChooser.getExtensionFilters().addAll(
            createExtensionFilter( "fileExtensionFilterPNG", "*.png" ),
            createExtensionFilter( "fileExtensionFilterJPG", "*.jpg", "*.jpeg" )
        );
        File selectedFile = fileChooser.showSaveDialog( getScene().getWindow() );
        if (selectedFile != null )
            fileExportAction.accept( selectedFile );
    }

    protected void handlePrint( Consumer< PrinterJob > printAction )
    {
        PrinterJob job = PrinterJob.createPrinterJob();
        if( job != null )
        {
            printAction.accept( job );
            job.endJob();
        }
    }

    protected void handleCredits()
    {
        ( ( Group ) getParent() ).getChildren().add( credits );
    }

    protected void handlePreferences()
    {
        new PreferenceDialog().showAndWait();
    }

    protected void handleChangeLanguage()
    {
        logger.warn( "TODO: handleChangeLanguage" );
    }
}
