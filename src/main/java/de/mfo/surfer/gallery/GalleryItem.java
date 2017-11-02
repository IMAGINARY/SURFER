package de.mfo.surfer.gallery;

import java.net.URL;

import javafx.scene.image.Image;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyStringProperty;

public interface GalleryItem extends Observable
{
    boolean isFirst();
    ReadOnlyStringProperty titleProperty();
    String getTitle();
    URL getJsurfURL();
    Image getThumbnailImage();
    Image getInfoPageRendering( int maxWidth, int maxHeight );
}
