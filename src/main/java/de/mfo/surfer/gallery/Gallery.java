package de.mfo.surfer.gallery;

import de.mfo.surfer.control.GalleryIcon;
import de.mfo.surfer.control.GalleryInfoPage;
import de.mfo.surfer.util.ThumbnailGenerator;
import de.mfo.surfer.util.Utils;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gallery
{
    private static final Logger logger = LoggerFactory.getLogger( Gallery.class );

    private static PDDocument pdfDocument;
    private static PDFRenderer pdfRenderer;
    private static HashMap< Pair< Locale, Type >, Gallery > galleries;

    private static PDDocument getGalleryPdf()
    {
        if( pdfDocument == null )
        {
            pdfDocument = Utils.wrapInRte( () -> PDDocument.load( Gallery.class.getResourceAsStream( "SURFER-Galleries.pdf" ) ) );
        }
        return pdfDocument;
    }

    private static PDFRenderer getGalleryRenderer()
    {
        if( pdfRenderer == null )
            pdfRenderer = new PDFRenderer( pdfDocument );
        return pdfRenderer;
    }

    private static void initGalleries()
    {
        galleries = new HashMap<>();
        PDOutlineItem localeKey = Gallery.getGalleryPdf().getDocumentCatalog().getDocumentOutline().getFirstChild();
        while( localeKey != null )
        {
            Locale locale = new Locale( localeKey.getTitle() );
            PDOutlineItem galleryKey = localeKey.getFirstChild();
            while( galleryKey != null )
            {
                Type type = Type.valueOf( galleryKey.getTitle().toUpperCase() );
                galleries.put( new Pair<>( locale, type ), new Gallery( galleryKey.getFirstChild() ) );
                galleryKey = galleryKey.getNextSibling();
            }
            localeKey = localeKey.getNextSibling();
        }
    }

    public static Gallery getGallery( Locale locale, Type type )
    {
        if( galleries == null )
            initGalleries();
        return galleries.get( new Pair<>( locale, type ) );
    }

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

            @Override
            public String getUserAgentStylesheet()
            {
                return Gallery.class.getResource( "../css/style.css" ).toExternalForm();
            }
        }

        private class GalleryInfoPageImpl extends GalleryInfoPage
        {
            Canvas canvas;
            PDRectangle cropBox;
            float lastScale;

            public GalleryInfoPageImpl()
            {
                super();

                lastScale = 0f;
                cropBox = Gallery.getGalleryPdf().getPage( pdfPageIndex ).getCropBox();

                canvas = new Canvas();
                canvas.widthProperty().bind( widthProperty() );
                canvas.heightProperty().bind( heightProperty() );

                widthProperty().addListener( ( o, ov, nv ) -> Platform.runLater( () -> render() ) );
                heightProperty().addListener( ( o, ov, nv ) -> Platform.runLater( () -> render() ) );
                localToSceneTransformProperty().addListener( ( o, ov, nv ) -> Platform.runLater( () -> render() ) );

                getChildren().add( canvas );
            }

            void render()
            {
                Bounds bb = localToScene( getBoundsInLocal(), false );
                float scale_x = ( float ) bb.getWidth() / cropBox.getWidth();
                float scale_y = ( float ) bb.getHeight() / cropBox.getHeight();
                float scale = 2f * Math.min( scale_x, scale_y );

                if( scale * Math.min( cropBox.getWidth(), cropBox.getHeight() )  >= 1f && ( scale != lastScale || lastScale == 0f ) )
                {
                    logger.debug( "redraw at  {}x{}", Math.round( scale * cropBox.getWidth() ), Math.round( scale * cropBox.getHeight() ) );
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.clearRect( 0.0, 0.0, getWidth(), getHeight() );
                    Image image = SwingFXUtils.toFXImage( Utils.wrapInRte( () -> Gallery.getGalleryRenderer().renderImage( pdfPageIndex, scale ) ), null );
                    if( image.getWidth() / image.getHeight() > canvas.getWidth() / canvas.getHeight() )
                        gc.drawImage( image, 0.0, 0.0, image.getWidth(), image.getHeight(), 0.0, 0.0, canvas.getWidth(), ( canvas.getWidth() * image.getHeight() ) / image.getWidth() );
                    else
                        gc.drawImage( image, 0.0, 0.0, image.getWidth(), image.getHeight(), 0.0, 0.0, ( canvas.getHeight() * image.getWidth() ) / image.getHeight(), canvas.getHeight() );
                    lastScale = scale;
                }
            }
        }

        private int pdfPageIndex;
        private String title;
        private URL jsurfURL;
        private Image thumbnailImage;
        private GalleryIcon icon;
        private GalleryInfoPage infoPage;

        public GalleryItemImpl( int pdfPageIndex, String title, URL jsurfURL )
        {
            this.pdfPageIndex = pdfPageIndex;
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

    List< GalleryItem > galleryItems;

    public enum Type
    {
        TUTORIAL, FANTASY, RECORD;
    }

    private PDOutlineItem rootItem;

    private Gallery( PDOutlineItem rootItem )
    {
        this.rootItem = rootItem;
    }

    public List< GalleryItem > getGalleryItems()
    {
        if( galleryItems == null )
        {
            PDOutlineItem item = rootItem;
            galleryItems = new LinkedList<>();
            while( item != null )
            {
                final PDOutlineItem finalItem = item;
                int pageIndex = Utils.wrapInRte( () -> ( ( PDPageDestination ) finalItem.getDestination() ) ).retrievePageNumber();
                GalleryItem gi = new GalleryItemImpl(
                    pageIndex,
                    item.getTitle(),
                    getClass().getResource( item.getFirstChild().getTitle() + ".jsurf" )
                );
                gi.getIcon().getStyleClass().addAll( item == rootItem ? "galleryIcon" : "galleryItemIcon" );
                galleryItems.add( gi );
                item = item.getNextSibling();
            }
            galleryItems = Collections.unmodifiableList( galleryItems );
        }

        return galleryItems;
    }
}
