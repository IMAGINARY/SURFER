package de.mfo.surfer.gallery;

import de.mfo.surfer.control.GalleryIcon;
import de.mfo.surfer.control.GalleryInfoPage;
import de.mfo.surfer.util.ThumbnailGenerator;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Gallery
{
    private class GalleryItemImpl implements GalleryItem
    {
        private class GalleryIconImpl extends GalleryIcon
        {
            public GalleryIconImpl()
            {
                super(
                    GalleryItemImpl.this.getTitle(),
                    new ImageView( GalleryItemImpl.this.getThumbnailImage() )
                );
            }
        }

        private class GalleryInfoPageImpl extends GalleryInfoPage
        {
            public GalleryInfoPageImpl()
            {
                super();
            }
        }

        private String title;
        private URL jsurfURL;
        private Image thumbnailImage;
        private GalleryIcon icon;
        private GalleryInfoPage infoPage;

        public GalleryItemImpl( String title, URL jsurfURL )
        {
            this.title = title;
            this.jsurfURL = jsurfURL;
        }

        public String getTitle()
        {
            return title;
        }

        public URL getJsurfURL()
        {
            return jsurfURL;
        }

        public Image getThumbnailImage()
        {
            if( thumbnailImage == null )
                thumbnailImage = ThumbnailGenerator.getImage( getJsurfURL() );
            return thumbnailImage;
        }

        public GalleryIcon getIcon()
        {
            if( icon == null )
                icon = new GalleryIconImpl();
            return icon;
        }

        public GalleryInfoPage getInfoPage()
        {
            if( infoPage == null )
                infoPage = new GalleryInfoPageImpl();
            return infoPage;
        }
    }

    public Gallery( URL pdfURL )
    {

    }

    public List< GalleryItem > getGalleryItems()
    {
        LinkedList< GalleryItem > items = new LinkedList<>();
        for( int i = 0; i < 13; ++i )
            items.add( new GalleryItemImpl( "item " + i, getClass().getResource( "default.jsurf" ) ) );
        return items;
    }
}
