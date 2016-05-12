package de.mfo.surfer.control;

import de.mfo.surfer.gallery.Gallery;
import de.mfo.surfer.gallery.GalleryItem;
import de.mfo.surfer.Main;
import de.mfo.surfer.util.L;
import java.util.LinkedList;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class GallerySelector extends VBox
{
    LinkedList< Gallery > galleries;
    Pane introPageContainer;
    Pane infoPageContainer;
    Pane galleryIconContainer;
    RenderArea renderArea;
    Main mainWindow;

    public GallerySelector( Pane galleryIconContainer, Pane introPageContainer, Pane infoPageContainer, RenderArea renderArea, Main mainWindow )
    {
        super();

        this.galleryIconContainer = galleryIconContainer;
        this.introPageContainer = introPageContainer;
        this.infoPageContainer = infoPageContainer;
        this.renderArea = renderArea;
        this.mainWindow = mainWindow;

        galleries = new LinkedList<>();
        galleries.add( new Gallery( null ) );
        galleries.add( new Gallery( null ) );
        galleries.add( new Gallery( null ) );

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
        introPageContainer.getChildren().setAll( g.getGalleryItems().get( 0 ).getInfoPage() );
        galleryIconContainer.getChildren().clear();
        g.getGalleryItems()
            .stream()
            .sequential()
            .skip( 1 )
            .forEach( gi -> galleryIconContainer.getChildren().add( gi.getIcon() ) );
        mainWindow.setMode( Main.Mode.GALLERY );
    }

    private void selectGalleryItem( GalleryItem gi )
    {
        infoPageContainer.getChildren().setAll( gi.getInfoPage() );
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
