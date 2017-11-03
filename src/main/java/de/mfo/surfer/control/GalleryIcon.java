package de.mfo.surfer.control;

import de.mfo.surfer.gallery.GalleryItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;

public class GalleryIcon extends RadioButton
{
    private GalleryItem galleryItem;

    public GalleryIcon( GalleryItem galleryItem )
    {
        this.galleryItem = galleryItem;
        this.textProperty().bind( galleryItem.titleProperty() );
        this.graphicProperty().setValue( new ImageView( galleryItem.getThumbnailImage() ) );
        getStyleClass().clear();
        getStyleClass().addAll( galleryItem.isFirst() ? "galleryIcon" : "galleryItemIcon" );
    }

    public GalleryIcon( GalleryItem galleryItem, ToggleGroup toggleGroup )
    {
        this( galleryItem );
        this.setToggleGroup( toggleGroup );
    }

    public GalleryItem getGalleryItem()
    {
        return this.galleryItem;
    }

    @Override
    public String getUserAgentStylesheet()
    {
        return GalleryIcon.class.getResource( "/de/mfo/surfer/css/style.css" ).toExternalForm();
    }
}
