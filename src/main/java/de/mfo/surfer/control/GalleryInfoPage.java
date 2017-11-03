package de.mfo.surfer.control;

import de.mfo.surfer.gallery.GalleryItem;
import de.mfo.surfer.util.FXUtils;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GalleryInfoPage extends Pane
{
    private static final Logger logger = LoggerFactory.getLogger( GalleryInfoPage.class );

    GalleryItem galleryItem;
    ImageView imageView;
    Node emptyPageSymbol;

    ObjectBinding< BoundingBox > targetBoundsBinding;
    InvalidationListener galleryItemInvalidationListener;
    ObjectBinding<Void> invalidationCombiner;

    /**
     * This is a hack. imageView is an external node that needs to be placed outside of the scaled content but still
     * needs to be resized and relocated properly. This class than paints onto the external component.
     * Not really a good solution, but otherwise the renderings of the gallery info pages are not aligned to pixels which
     * causes almost unreadable text (scaled bitmap fonts are always ugly).
     * TODO: use different mechanism for UI scaling
     * @param imageView
     */
    public GalleryInfoPage( ImageView imageView )
    {
        super();

        this.setSnapToPixel( true );

        this.imageView = imageView;
        this.imageView.visibleProperty().bind( visibleProperty() );
        this.imageView.setMouseTransparent( true );
        this.imageView.setPreserveRatio( true );

        targetBoundsBinding = Bindings.createObjectBinding( () -> {
                Bounds boundsInScene = this.localToSceneTransformProperty().get().transform( boundsInLocalProperty().get() );
                BoundingBox boundsInSceneSnapped = new BoundingBox( Math.round( boundsInScene.getMinX() ),
                    Math.round( boundsInScene.getMinY() ),
                    Math.ceil( boundsInScene.getWidth() ),
                    Math.ceil( boundsInScene.getHeight() )
                );
                return boundsInSceneSnapped;
            },
            this.boundsInLocalProperty(),
            this.localToSceneTransformProperty()
        );

        invalidationCombiner = Bindings.createObjectBinding( () -> { return null; }, imageView.fitWidthProperty(), imageView.fitHeightProperty() );
        invalidationCombiner.addListener( __ -> Platform.runLater( this::render ) );
        ChangeListener<BoundingBox> l = (o, ov, nv ) -> {
            FXUtils.relocateTo( imageView, nv );
            imageView.setFitWidth( nv.getWidth() );
            imageView.setFitHeight( nv.getHeight() );
        };
        l.changed(targetBoundsBinding, targetBoundsBinding.getValue(), targetBoundsBinding.getValue());
        targetBoundsBinding.addListener( l );

        galleryItemInvalidationListener = __ -> Platform.runLater( this::render );

        getChildren().setAll( getEmptyPageSymbol() );
    }

    public GalleryInfoPage(ImageView imageView, GalleryItem galleryItem )
    {
        this(imageView);
        setGalleryItem( galleryItem );
    }

    void setGalleryItem( GalleryItem galleryItem )
    {
        if( this.galleryItem != null )
            this.galleryItem.removeListener( galleryItemInvalidationListener );
        this.galleryItem = galleryItem;
        galleryItem.addListener( galleryItemInvalidationListener );
        render();
    }

    void render()
    {
        // needs to be called to activate InvalidationListeners again
        invalidationCombiner.get();

        if( this.galleryItem != null ) {
            int maxImageWidth = (int) imageView.getFitWidth();
            int maxImageHeight = (int) imageView.getFitHeight();
            assert ( double ) maxImageWidth == imageView.getFitWidth() && ( double ) maxImageHeight == imageView.getFitHeight()
                : "target dimensions are not integral";
            getChildren().remove( getEmptyPageSymbol() );
            imageView.setImage(this.galleryItem.getInfoPageRendering( maxImageWidth, maxImageHeight ));
        }
    }

    private Node getEmptyPageSymbol()
    {
        if( emptyPageSymbol == null ) {
            javafx.scene.shape.SVGPath svgPath = new javafx.scene.shape.SVGPath();
            svgPath.setContent("M 112.28516,897.95508 C 50.428286,897.95508 0,948.37749 0,1010.2344 c 0,61.8568 50.428286,112.2851 112.28516,112.2851 61.85687,0 112.27929,-50.4283 112.27929,-112.2851 0,-61.85691 -50.42242,-112.27932 -112.27929,-112.27932 z m 0,26.26562 c 19.03541,0 36.57732,6.1299 50.79296,16.51563 L 42.789062,1061.0234 c -10.385241,-14.2154 -16.517578,-31.7541 -16.517578,-50.789 0,-47.66006 38.353652,-86.0137 86.013676,-86.0137 z m 69.49804,35.22071 c 10.38573,14.21565 16.51563,31.75755 16.51563,50.79299 0,47.66 -38.35365,86.0136 -86.01367,86.0136 -19.034931,0 -36.57361,-6.1323 -50.789066,-16.5175 z");
            svgPath.setFill(new javafx.scene.paint.Color(0.5, 0.5, 0.5, 0.25));
            javafx.scene.layout.StackPane stackPane = new javafx.scene.layout.StackPane(svgPath);
            stackPane.prefWidthProperty().bind(widthProperty());
            stackPane.prefHeightProperty().bind(heightProperty());
            this.emptyPageSymbol = stackPane;
        }
        return emptyPageSymbol;
    }
}
