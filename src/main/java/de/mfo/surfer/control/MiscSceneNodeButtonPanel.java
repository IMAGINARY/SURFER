package de.mfo.surfer.control;

import de.mfo.surfer.control.SceneNodeButton;
import de.mfo.surfer.Main;

import java.awt.*;
import java.io.File;
import java.util.EnumMap;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static de.mfo.surfer.util.L.l;

public class MiscSceneNodeButtonPanel extends Region
{
    public enum ButtonType {
        PREFERENCES( "Preferences" ),
        OPEN( "Open_File" ),
        SAVE( "Save_File" ),
        EXPORT( "Export" ),
        PRINT( "Print" ),
        ABOUT( "Imprint" ),
        LANGUAGE( "Language" );

        private String fxmlName;

        private ButtonType( String fxmlName ) { this.fxmlName = fxmlName; }
        protected String getFXMLName() { return this.fxmlName; };
    }

    private EnumMap< ButtonType, Node > buttons;

    private static final Logger logger = LoggerFactory.getLogger( MiscSceneNodeButtonPanel.class );

    PreferenceDialog prefsDialog;
    Credits credits;
    LanguageSelector languageSelector;

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
        languageSelector = new LanguageSelector();

        buttons = new EnumMap<ButtonType, Node>( ButtonType.class );
        createButton( ButtonType.PREFERENCES, e -> handlePreferences() );
        createButton( ButtonType.OPEN, e -> handleFileOpen( fileOpenAction ) );
        createButton( ButtonType.SAVE , e -> handleFileSave( fileSaveAction ) );
        createButton( ButtonType.EXPORT, e -> handleFileExport( fileExportAction ) );
        createButton( ButtonType.PRINT, e -> handlePrint( printAction ) );
        createButton( ButtonType.ABOUT, e -> handleCredits() );
        // not needed; does not work anyway
        //getChildren().add( createButton( ButtonType.LANGUAGE, e -> handleChangeLanguage() ) );
        buttons.put( ButtonType.LANGUAGE, languageSelector );
        getChildren().add( languageSelector );
    }

    private void createButton( ButtonType buttonType, EventHandler< ActionEvent > handler )
    {
        SceneNodeButton result = new SceneNodeButton(
            Main.fxmlLookup( "#Button_" + buttonType.getFXMLName() ),
            Main.fxmlLookup( "#Button_Over_" + buttonType.getFXMLName() ),
            Main.fxmlLookup( "#Button_Pressed_" + buttonType.getFXMLName() )
        );
        result.setOnAction( handler );
        this.buttons.put( buttonType, result );
        this.getChildren().add( result );
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
        // NOOP: all the work is done by languageSelector
    }

    public void setDisable( boolean disabled, ButtonType firstButtonType, ButtonType... otherButtonTypes )
    {
        buttons.get( firstButtonType ).setDisable( disabled );
        for( ButtonType buttonType : otherButtonTypes )
            buttons.get( buttonType ).setDisable( disabled );
    }
}
