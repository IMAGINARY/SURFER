package de.mfo.surfer.util;

import de.mfo.jsurf.rendering.cpu.CPUAlgebraicSurfaceRenderer;
import de.mfo.jsurf.util.FileFormat;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThumbnailGenerator
{
    private static final Logger logger = LoggerFactory.getLogger( ThumbnailGenerator.class );
    private static final ExecutorService executor = Executors.newCachedThreadPool( r -> {
            Thread t = new Thread( r );
            t.setName( ThumbnailGenerator.class.getName() + " Worker");
            t.setDaemon( true );
            return t;
        }
    );

    static int thumbnailSize = 150;
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
        final WritableImage image = new WritableImage( thumbnailSize, thumbnailSize );

        // do rendering in background
        Task<int[]> renderTask = new Task<int[]>() {
            @Override
            protected int[] call() throws Exception {
                CPUAlgebraicSurfaceRenderer asr = new CPUAlgebraicSurfaceRenderer();
                Properties jsurf = new Properties();
                jsurf.load( jSurfURL.openStream() );
                FileFormat.load( jsurf, asr );
                int[] colorBuffer = new int[ thumbnailSize * thumbnailSize ];
                asr.draw( colorBuffer, thumbnailSize, thumbnailSize );
                return colorBuffer;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                for( int i = 0; i < thumbnailSize; ++i )
                {
                    image.getPixelWriter().setPixels(
                        0, i, thumbnailSize, 1,
                        PixelFormat.getIntArgbInstance(),
                        this.getValue(),
                        ( thumbnailSize - 1 - i ) * thumbnailSize,
                        thumbnailSize
                    );
                }
            }

            @Override
            protected void failed() {
                super.failed();
                ThumbnailGenerator.logger.error( "Error creating thumbnail image for " + jSurfURL, getException() );
            }
        };

        Future f = executor.submit( renderTask );

        return image;
    }
}
