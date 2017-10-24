package de.mfo.surfer.control;

import de.mfo.surfer.gallery.GalleryItem;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
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
}
