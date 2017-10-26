package de.mfo.surfer.gallery;

import java.net.URL;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyStringProperty;

public interface GalleryItem extends Observable
{
    public boolean isFirst();
    public ReadOnlyStringProperty titleProperty();
    public String getTitle();
    public URL getJsurfURL();
    public Image getThumbnailImage();
    public Image getInfoPageRendering( Bounds boundingBox );
}
