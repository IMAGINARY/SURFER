package de.mfo.surfer.control;

import de.mfo.surfer.gallery.GalleryItem;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;

public class GalleryInfoPage extends Region
{
    GalleryItem galleryItem;
    Canvas canvas;
    InvalidationListener galleryItemInvalidationListener;
    ObjectBinding<Void> invalidationCombiner;

    public GalleryInfoPage()
    {
        super();

        canvas = new Canvas();
        canvas.widthProperty().bind( widthProperty() );
        canvas.heightProperty().bind( heightProperty() );

        invalidationCombiner = Bindings.createObjectBinding( () -> { return null; }, widthProperty(), heightProperty(), localToSceneTransformProperty() );
        invalidationCombiner.addListener( __ -> Platform.runLater( this::render ) );

        galleryItemInvalidationListener = __ -> Platform.runLater( this::render );

        getChildren().add( getEmptyPageSymbol() );
        getChildren().add( canvas );
    }

    public GalleryInfoPage( GalleryItem galleryItem )
    {
        this();
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
        widthProperty().get();
        heightProperty().get();
        localToSceneTransformProperty().get();
        invalidationCombiner.get();

        if( this.galleryItem != null ) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0.0, 0.0, getWidth(), getHeight() );
            Image image = this.galleryItem.getInfoPageRendering(localToScene(getBoundsInLocal(), false));

            if (image.getWidth() / image.getHeight() > canvas.getWidth() / canvas.getHeight())
                gc.drawImage(image, 0.0, 0.0, image.getWidth(), image.getHeight(), 0.0, 0.0, canvas.getWidth(), (canvas.getWidth() * image.getHeight()) / image.getWidth());
            else
                gc.drawImage(image, 0.0, 0.0, image.getWidth(), image.getHeight(), 0.0, 0.0, (canvas.getHeight() * image.getWidth()) / image.getHeight(), canvas.getHeight());
        }
    }

    private Node getEmptyPageSymbol()
    {
        javafx.scene.shape.SVGPath svgPath = new javafx.scene.shape.SVGPath();
        svgPath.setContent( "M 112.28516,897.95508 C 50.428286,897.95508 0,948.37749 0,1010.2344 c 0,61.8568 50.428286,112.2851 112.28516,112.2851 61.85687,0 112.27929,-50.4283 112.27929,-112.2851 0,-61.85691 -50.42242,-112.27932 -112.27929,-112.27932 z m 0,26.26562 c 19.03541,0 36.57732,6.1299 50.79296,16.51563 L 42.789062,1061.0234 c -10.385241,-14.2154 -16.517578,-31.7541 -16.517578,-50.789 0,-47.66006 38.353652,-86.0137 86.013676,-86.0137 z m 69.49804,35.22071 c 10.38573,14.21565 16.51563,31.75755 16.51563,50.79299 0,47.66 -38.35365,86.0136 -86.01367,86.0136 -19.034931,0 -36.57361,-6.1323 -50.789066,-16.5175 z" );
        svgPath.setFill( new javafx.scene.paint.Color( 0.5, 0.5, 0.5, 0.25 ) );
        javafx.scene.layout.StackPane stackPane = new javafx.scene.layout.StackPane( svgPath );
        stackPane.prefWidthProperty().bind( widthProperty() );
        stackPane.prefHeightProperty().bind( heightProperty() );
        return stackPane;
    }
}
