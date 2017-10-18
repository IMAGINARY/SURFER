package de.mfo.surfer.gallery;

import de.mfo.surfer.control.GalleryIcon;
import de.mfo.surfer.control.GalleryInfoPage;
import de.mfo.surfer.util.ThumbnailGenerator;
import de.mfo.surfer.util.Utils;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
    public enum Type
    {
        TUTORIAL, FANTASY, RECORD;
    }

    private static class TitleAndPageNumber
    {
        public String title;
        public int pageNumber;

        public TitleAndPageNumber( String title, int pageNumber )
        {
            this.title = title;
            this.pageNumber = pageNumber;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger( Gallery.class );

    private static PDDocument pdfDocument;
    private static PDFRenderer pdfRenderer;

    private static Set< Locale > availableLocales;
    private static HashMap< Type, HashMap< String, HashMap< Locale, TitleAndPageNumber > > > allGalleryEntries;

    private List< GalleryItem > galleryItems;
    private ReadOnlyObjectProperty< Locale > locale;

    static {
        pdfDocument = Utils.wrapInRte( () -> PDDocument.load( Gallery.class.getResourceAsStream( "Surfer-Galleries.pdf" ) ) );
        pdfRenderer = new PDFRenderer( pdfDocument );

        availableLocales = new HashSet<>();
        allGalleryEntries = new HashMap<>( Type.values().length );

        // locale loop
        for( PDOutlineItem localeItem : Gallery.pdfDocument.getDocumentCatalog().getDocumentOutline().children() )
        {
            Locale locale = Locale.forLanguageTag( localeItem.getTitle().replaceAll( "_", "-" ) );
            availableLocales.add( locale );
            for( PDOutlineItem typeItem : localeItem.children() )  // type loop
            {
                Type type = Type.valueOf( typeItem.getTitle().toUpperCase() );
                int index = 0;
                for( PDOutlineItem idParentItem : typeItem.children() ) // id loop
                {
                    String id = (index++) + " " + idParentItem.getFirstChild().getTitle();

                    HashMap< String, HashMap< Locale, TitleAndPageNumber > > typeHM = allGalleryEntries.get( type );
                    if( typeHM == null )
                    {
                        typeHM = new LinkedHashMap<>();
                        allGalleryEntries.put( type, typeHM );
                    }

                    HashMap< Locale, TitleAndPageNumber > idHM = typeHM.get( id );
                    if( idHM == null )
                    {
                        idHM = new HashMap<>();
                        typeHM.put( id, idHM );
                    }

                    String title = idParentItem.getTitle();
                    int pdfPageNumber = Utils.wrapInRte( () -> ( ( PDPageDestination ) idParentItem.getFirstChild().getDestination() ) ).retrievePageNumber();
                    idHM.put( locale, new TitleAndPageNumber( title, pdfPageNumber ) );
                }
            }
        }

        availableLocales = Collections.unmodifiableSet( availableLocales );
    }

    public static Set< Locale > getAvailableLocales()
    {
        return availableLocales;
    }

    protected static Image getGalleryInfoPageRendering( int pdfPageIndex, float scale )
    {
        return SwingFXUtils.toFXImage( Utils.wrapInRte( () -> Gallery.pdfRenderer.renderImage( pdfPageIndex, scale ) ), null );
    }

    public Gallery( ReadOnlyObjectProperty< Locale > locale, Type type )
    {
        this.locale = locale;

        LinkedList< GalleryItem > tmpGalleryItems = new LinkedList<>();
        for( HashMap.Entry< String, HashMap< Locale, TitleAndPageNumber > > thisGalleryEntries : allGalleryEntries.get( type ).entrySet() )
        {
            String[] parts = thisGalleryEntries.getKey().split( " ", 2 );
            tmpGalleryItems.add( new GalleryItemImpl( parts[ 1 ], thisGalleryEntries.getValue(), parts[ 0 ].equals( "0" ) ) );
        }
        this.galleryItems = Collections.unmodifiableList( tmpGalleryItems );
    }

    public List< GalleryItem > getGalleryItems()
    {
        return this.galleryItems;
    }

    private class GalleryItemImpl implements GalleryItem
    {
        private class GalleryIconImpl extends GalleryIcon
        {
            public GalleryIconImpl()
            {
                textProperty().bind( GalleryItemImpl.this.titleProperty() );
                setGraphic( new ImageView( GalleryItemImpl.this.getThumbnailImage() ) );
                getStyleClass().addAll( GalleryItemImpl.this.isFirst ? "galleryIcon" : "galleryItemIcon" );
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
                cropBox = Gallery.pdfDocument.getPage( GalleryItemImpl.this.pdfPageNumber.get() ).getCropBox();

                canvas = new Canvas();
                canvas.widthProperty().bind( widthProperty() );
                canvas.heightProperty().bind( heightProperty() );

                widthProperty().addListener( ( o, ov, nv ) -> Platform.runLater( () -> render() ) );
                heightProperty().addListener( ( o, ov, nv ) -> Platform.runLater( () -> render() ) );
                localToSceneTransformProperty().addListener( ( o, ov, nv ) -> Platform.runLater( () -> render() ) );
                GalleryItemImpl.this.pdfPageNumber.addListener( ( o, ov, nv ) -> Platform.runLater( () -> { lastScale = 0.0f; render(); } ) );

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
                    Image image = Gallery.this.getGalleryInfoPageRendering( GalleryItemImpl.this.pdfPageNumber.get(), scale );
                    if( image.getWidth() / image.getHeight() > canvas.getWidth() / canvas.getHeight() )
                        gc.drawImage( image, 0.0, 0.0, image.getWidth(), image.getHeight(), 0.0, 0.0, canvas.getWidth(), ( canvas.getWidth() * image.getHeight() ) / image.getWidth() );
                    else
                        gc.drawImage( image, 0.0, 0.0, image.getWidth(), image.getHeight(), 0.0, 0.0, ( canvas.getHeight() * image.getWidth() ) / image.getHeight(), canvas.getHeight() );
                    lastScale = scale;
                }
            }
        }

        private String idInGallery;
        private HashMap< Locale, TitleAndPageNumber > localizationMap;
        private boolean isFirst;

        private SimpleIntegerProperty pdfPageNumber;
        private SimpleStringProperty title;
        private URL jsurfURL;
        private Image thumbnailImage;
        private GalleryIcon icon;
        private GalleryInfoPage infoPage;

        public GalleryItemImpl( String idInGallery, HashMap< Locale, TitleAndPageNumber > localizationMap, boolean isFirst )
        {
            this.idInGallery = idInGallery;
            this.localizationMap = localizationMap;
            this.isFirst = isFirst;

            this.pdfPageNumber = new SimpleIntegerProperty();
            this.title = new SimpleStringProperty();
            Gallery.this.locale.addListener( ( o, ov, nv ) -> updateProperties( nv ) );
            updateProperties( Gallery.this.locale.get() );

            this.jsurfURL = getClass().getResource( idInGallery + ".jsurf" );
        }

        private void updateProperties( Locale locale )
        {
            TitleAndPageNumber tapn = localizationMap.get( locale );
            pdfPageNumber.setValue( tapn.pageNumber );
            title.setValue( tapn.title );
        }

        public ReadOnlyStringProperty titleProperty()
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
}
