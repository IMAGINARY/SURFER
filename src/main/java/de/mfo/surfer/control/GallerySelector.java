package de.mfo.surfer.control;

import de.mfo.surfer.gallery.Gallery;
import de.mfo.surfer.gallery.GalleryItem;
import de.mfo.surfer.Main;
import de.mfo.surfer.util.L;

import java.util.*;

import javafx.collections.ObservableList;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

public class GallerySelector extends VBox
{
    private class GalleryWithIcons {
        public Gallery gallery;
        public GalleryIcon first;
        public List< GalleryIcon > others;

        public GalleryWithIcons( Gallery gallery )
        {
            this.gallery = gallery;
            this.first = new GalleryIcon( gallery.getGalleryItems().get( 0 ), GallerySelector.this.galleryToggleGroup );
            this.first.setOnMouseClicked( e -> selectGallery( gallery.getType() ) );


            this.others = new ArrayList<>( gallery.getGalleryItems().size() - 1 );
            gallery.getGalleryItems().stream().skip( 1 ).forEach( galleryItem -> {
                GalleryIcon galleryIcon = new GalleryIcon( galleryItem, GallerySelector.this.galleryItemToggleGroup );
                galleryIcon.setOnMouseClicked( e -> GallerySelector.this.selectGalleryItem( galleryItem ) );
                this.others.add( galleryIcon );
            } );
        }
    }

    Map< Gallery.Type, GalleryWithIcons > galleriesWithIcons;
    GalleryInfoPage introPage;
    GalleryInfoPage infoPage;
    ObservableList< Node > galleryIconContainer;
    RenderArea renderArea;
    Main mainWindow;
    ToggleGroup galleryToggleGroup;
    ToggleGroup galleryItemToggleGroup;
    Map< Gallery, List< GalleryIcon > > galleryIcons;

    public GallerySelector( ObservableList< Node > galleryIconContainer, GalleryInfoPage introPage, GalleryInfoPage infoPage, RenderArea renderArea, Main mainWindow )
    {
        super();

        this.introPage = introPage;
        this.infoPage = infoPage;

        this.galleryIconContainer = galleryIconContainer;
        this.renderArea = renderArea;
        this.mainWindow = mainWindow;

        this.galleryToggleGroup = new ToggleGroup();
        this.galleryItemToggleGroup = new ToggleGroup();

        this.galleriesWithIcons = new EnumMap<>( Gallery.Type.class );
        for( Gallery.Type type : Gallery.Type.values() ) {
            GalleryWithIcons galleryWithIcons = new GalleryWithIcons( new Gallery(L.localeProperty(), type ) );
            this.getChildren().add( galleryWithIcons.first );
            VBox.setVgrow( galleryWithIcons.first, Priority.ALWAYS );
            this.galleriesWithIcons.put( type, galleryWithIcons );
        }
    }

    public Gallery getGallery( Gallery.Type galleryType )
    {
        return galleriesWithIcons.get( galleryType ).gallery;
    }

    public void selectGallery( Gallery.Type galleryType )
    {
        GalleryWithIcons galleryWithIcons = galleriesWithIcons.get( galleryType );
        galleryWithIcons.first.setSelected( true );
        introPage.setGalleryItem( galleryWithIcons.first.getGalleryItem() );
        galleryIconContainer.clear();
        for( GalleryIcon galleryIcon : galleryWithIcons.others )
            galleryIconContainer.add( galleryIcon );
        mainWindow.setMode( Main.Mode.GALLERY );
    }

    public void selectGalleryItem( GalleryItem gi )
    {
        infoPage.setGalleryItem( gi );
        try
        {
            renderArea.load( gi );
            mainWindow.setMode( Main.Mode.INFO );
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Unable to load surface from gallery: " + gi.getJsurfURL(), e );
        }
    }
}
