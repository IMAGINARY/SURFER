package de.mfo.surfer.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

public class ImageUtil {

    public static Image createImageFromRGB( int[] rgbBuffer, int w, int h )
    {
        WritableImage image = new WritableImage( w, h );
        image.getPixelWriter().setPixels(
            0, 0, w, h,
            PixelFormat.getIntArgbInstance(),
            rgbBuffer, 0, w
        );
        return image;
    }
}
