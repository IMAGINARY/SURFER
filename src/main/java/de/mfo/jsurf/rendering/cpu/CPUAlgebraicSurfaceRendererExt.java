package de.mfo.jsurf.rendering.cpu;

import de.mfo.jsurf.rendering.cpu.CPUAlgebraicSurfaceRenderer;
import de.mfo.jsurf.rendering.cpu.DrawcallStaticData;
import de.mfo.jsurf.rendering.RenderingInterruptedException;

import java.util.concurrent.*;
import java.util.*;

public class CPUAlgebraicSurfaceRendererExt extends CPUAlgebraicSurfaceRenderer
{
    public static class DrawcallStaticDataExt
    {
        private DrawcallStaticData privateDcsd;

        private DrawcallStaticDataExt( DrawcallStaticData dcsd )
        {
            privateDcsd = dcsd;
        }

        public int getWidth() { return privateDcsd.width; }
        public int getHeight() { return privateDcsd.height; }
        public int[] getColorBuffer() { return privateDcsd.colorBuffer; }
    }

    public CPUAlgebraicSurfaceRendererExt()
    {
        super();
    }

    public synchronized DrawcallStaticDataExt collectDrawCallStaticDataExt( int[] colorBuffer, int width, int height )
    {
        return new DrawcallStaticDataExt( super.collectDrawCallStaticData( colorBuffer, width, height ) );
    }

    public synchronized void draw( DrawcallStaticDataExt dcsd )
    {
        final int width = dcsd.privateDcsd.width;
        final int height = dcsd.privateDcsd.height;

        if( width == 0 || height == 0 )
            return;

        int xStep = width / Math.min( width, Math.max( 2, Runtime.getRuntime().availableProcessors() ) );
        int yStep = height / Math.min( height, 3 );//Math.max( 2, Runtime.getRuntime().availableProcessors() ) );

        boolean success = true;

        LinkedList< FutureTask< Boolean > > tasks = new LinkedList< FutureTask< Boolean > >();
        for( int x = 0; x < width; x += xStep )
            for( int y = 0; y < height; y += yStep )
                tasks.add( new FutureTask< Boolean >( new RenderingTask( dcsd.privateDcsd, x, y, Math.min( x + xStep, width - 1 ), Math.min( y + yStep, height - 1 ) ) ) );

        renderingTasks = tasks;

        try
        {
            for( FutureTask< Boolean > task : tasks )
                threadPoolExecutor.execute( task );
            for( FutureTask< Boolean > task : tasks )
                success = success && task.get();
        }
        catch( ExecutionException ie )
        {
            success = false;
        }
        catch( InterruptedException ie )
        {
            super.stopDrawing();
            success = false;
        }
        catch( RejectedExecutionException ree )
        {
            success = false;
        }
        catch( CancellationException ree )
        {
            success = false;
        }
        finally
        {
            if( !success || Thread.interrupted() )
                throw new RenderingInterruptedException( "Rendering interrupted" );
        }
    }
}
