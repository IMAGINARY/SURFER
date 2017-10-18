package de.mfo.surfer.gallery;

import de.mfo.surfer.control.GalleryIcon;
import de.mfo.surfer.control.GalleryInfoPage;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.beans.property.ReadOnlyStringProperty;

public interface GalleryItem
{
    public ReadOnlyStringProperty titleProperty();
    public URL getJsurfURL();
    public Image getThumbnailImage();
    public GalleryIcon getIcon();
    public Image getInfoPageRendering( float scale ); // TODO: use BBox parameter instead of scale
}
