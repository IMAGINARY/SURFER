package de.mfo.surfer.gallery;

import de.mfo.surfer.control.GalleryIcon;
import de.mfo.surfer.control.GalleryInfoPage;
import java.net.URL;
import javafx.scene.image.Image;

public interface GalleryItem
{
    public String getTitle();
    public URL getJsurfURL();
    public Image getThumbnailImage();
    public GalleryIcon getIcon();
    public GalleryInfoPage getInfoPage();
}
