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
    GalleryInfoPage introPage; /* TODO: move infoPage node into creating class and remove the container */
    GalleryInfoPage infoPage; /* TODO: move infoPage node into creating class and remove the container */
    ObservableList< Node > galleryIconContainer;
    RenderArea renderArea;
    Main mainWindow;

    public GallerySelector( ObservableList< Node > galleryIconContainer, GalleryInfoPage introPage, GalleryInfoPage infoPage, RenderArea renderArea, Main mainWindow )
    {
        super();

        this.introPage = introPage;
        this.infoPage = infoPage;

        this.galleryIconContainer = galleryIconContainer;
        this.renderArea = renderArea;
        this.mainWindow = mainWindow;

        galleries = new LinkedList<>();
        for( Gallery.Type type : Gallery.Type.values() )
            galleries.add( new Gallery( L.localeProperty(), type ) );

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
        introPage.setGalleryItem( g.getGalleryItems().get( 0 ) );
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
        infoPage.setGalleryItem( gi );
        try
        {
            renderArea.load( gi.getJsurfURL() );
            mainWindow.setMode( Main.Mode.INFO );
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Unable to load surface from gallery: " + gi.getJsurfURL(), e );
        }
    }
}
