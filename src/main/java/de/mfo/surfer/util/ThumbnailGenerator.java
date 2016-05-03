package de.mfo.surfer.util;

import de.mfo.jsurf.rendering.cpu.CPUAlgebraicSurfaceRenderer;
import de.mfo.jsurf.util.FileFormat;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ThumbnailGenerator
{
    static int thumbnailSize = 200;
    static HashMap< URL, Image > cachedImages;
    static
    {
        cachedImages = new HashMap< URL, Image >();
    }

    public static Image getImage( URL jSurfURL )
    {
        Image result = cachedImages.get( jSurfURL );
        if( result == null )
        {
            result = renderImage( jSurfURL );
            cachedImages.put( jSurfURL, result );
            return result;
        }
        return result;
    }

    private static Image renderImage( URL jSurfURL )
    {
        try
        {
            CPUAlgebraicSurfaceRenderer asr = new CPUAlgebraicSurfaceRenderer();
            Properties jsurf = new Properties();
            jsurf.load( jSurfURL.openStream() );
            FileFormat.load( jsurf, asr );
            int[] colorBuffer = new int[ thumbnailSize * thumbnailSize ];
            asr.draw( colorBuffer, thumbnailSize, thumbnailSize );

            WritableImage image = new WritableImage( thumbnailSize, thumbnailSize );
            for( int i = 0; i < thumbnailSize; ++i )
            {
                image.getPixelWriter().setPixels(
                    0, i, thumbnailSize, 1,
                    PixelFormat.getIntArgbInstance(),
                    colorBuffer,
                    ( thumbnailSize - 1 - i ) * thumbnailSize,
                    thumbnailSize
                );
            }

            return image;
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Error creating thumbnail image for " + jSurfURL, e );
        }
    }
}
