package de.mfo.surfer.control;

import de.mfo.surfer.gallery.Gallery;
import de.mfo.surfer.gallery.GalleryItem;
import de.mfo.surfer.Main;
import de.mfo.surfer.util.L;
import java.util.LinkedList;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

public class GallerySelector extends VBox
{
    LinkedList< Gallery > galleries;
    WritableValue< Node > introPageContainer;
    WritableValue< Node > infoPageContainer;
    ObservableList< Node > galleryIconContainer;
    RenderArea renderArea;
    Main mainWindow;

    public GallerySelector( ObservableList< Node > galleryIconContainer, WritableValue< Node > introPageContainer, WritableValue< Node > infoPageContainer, RenderArea renderArea, Main mainWindow )
    {
        super();

        this.galleryIconContainer = galleryIconContainer;
        this.introPageContainer = introPageContainer;
        this.infoPageContainer = infoPageContainer;
        this.renderArea = renderArea;
        this.mainWindow = mainWindow;

        galleries = new LinkedList<>();
        galleries.add( Gallery.getGallery( java.util.Locale.ENGLISH, Gallery.Type.TUTORIAL ) );
        galleries.add( Gallery.getGallery( java.util.Locale.ENGLISH, Gallery.Type.FANTASY ) );
        galleries.add( Gallery.getGallery( java.util.Locale.ENGLISH, Gallery.Type.RECORD ) );

        galleries.forEach( g -> getChildren().add( prepareGallery( g ) ) );
    }

    private GalleryIcon prepareGallery( Gallery g )
    {
        GalleryIcon icon = g.getGalleryItems().get( 0 ).getIcon();
        icon.setOnMouseClicked( e -> selectGallery( g ) );

        g.getGalleryItems()
            .stream()
            .skip( 1 )
            .forEach( gi -> gi.getIcon().setOnMouseClicked( e -> selectGalleryItem( gi ) ) );
        return icon;
    }

    private void selectGallery( Gallery g )
    {
        introPageContainer.setValue( g.getGalleryItems().get( 0 ).getInfoPage() );
        galleryIconContainer.clear();
        g.getGalleryItems()
            .stream()
            .sequential()
            .skip( 1 )
            .forEach( gi -> galleryIconContainer.add( gi.getIcon() ) );
        mainWindow.setMode( Main.Mode.GALLERY );
    }

    private void selectGalleryItem( GalleryItem gi )
    {
        infoPageContainer.setValue( gi.getInfoPage() );
        try
        {
            renderArea.load( gi.getJsurfURL() );
            mainWindow.setMode( Main.Mode.INFO );
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Unable to surface from gallery: " + gi.getJsurfURL(), e );
        }
    }
}
