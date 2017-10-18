package de.mfo.surfer.control;

import de.mfo.surfer.gallery.Gallery;
import de.mfo.surfer.gallery.GalleryItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class GalleryInfoPage extends Region
{
    GalleryItem galleryItem;
    Canvas canvas;
    PDRectangle cropBox;
    float lastScale;
    ChangeListener<Number> pageNumberListener;

    public GalleryInfoPage()
    {
        super();

        canvas = new Canvas();
        canvas.widthProperty().bind( widthProperty() );
        canvas.heightProperty().bind( heightProperty() );

        // TODO: somehow bind the invalidation of the three properties together and call render() only once
        widthProperty().addListener( ( o, ov, nv ) -> Platform.runLater( () -> render() ) );
        heightProperty().addListener( ( o, ov, nv ) -> Platform.runLater( () -> render() ) );
        localToSceneTransformProperty().addListener( ( o, ov, nv ) -> Platform.runLater( () -> render() ) );

        pageNumberListener = (o, ov, nv ) -> Platform.runLater( () -> renderAnyway() );

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
            ( ( Gallery.GalleryItemImpl ) this.galleryItem ).pdfPageNumber.removeListener( pageNumberListener );
        this.galleryItem = galleryItem;
        ((Gallery.GalleryItemImpl) this.galleryItem).pdfPageNumber.addListener( pageNumberListener );
        this.cropBox = Gallery.pdfDocument.getPage( ((Gallery.GalleryItemImpl) galleryItem).pdfPageNumber.get() ).getCropBox();
        renderAnyway();
    }

    void renderAnyway()
    {
        lastScale = 0.0f;
        render();
    }

    void render()
    {
        if( cropBox != null )
        {
            Bounds bb = localToScene(getBoundsInLocal(), false);
            float scale_x = (float) bb.getWidth() / cropBox.getWidth();
            float scale_y = (float) bb.getHeight() / cropBox.getHeight();
            float scale = 2f * Math.min(scale_x, scale_y);

            if (scale * Math.min(cropBox.getWidth(), cropBox.getHeight()) >= 1f && (scale != lastScale || lastScale == 0f))
            {
                //logger.debug( "redraw at  {}x{}", Math.round( scale * cropBox.getWidth() ), Math.round( scale * cropBox.getHeight() ) );
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.clearRect(0.0, 0.0, getWidth(), getHeight());
                Image image = this.galleryItem.getInfoPageRendering(scale);
                if (image.getWidth() / image.getHeight() > canvas.getWidth() / canvas.getHeight())
                    gc.drawImage(image, 0.0, 0.0, image.getWidth(), image.getHeight(), 0.0, 0.0, canvas.getWidth(), (canvas.getWidth() * image.getHeight()) / image.getWidth());
                else
                    gc.drawImage(image, 0.0, 0.0, image.getWidth(), image.getHeight(), 0.0, 0.0, (canvas.getHeight() * image.getWidth()) / image.getHeight(), canvas.getHeight());
                lastScale = scale;
            }
        }
    }
}
