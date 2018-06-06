package de.mfo.jsurf.rendering.cpu;

import de.mfo.jsurf.algebra.DegreeCalculator;
import de.mfo.jsurf.algebra.PolynomialOperation;
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

    private Integer degree;
    private DegreeCalculator degreeCalculator;

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
  
	// TODO: the parent method stopDrawing uses implicitly the renderingTasks field, but it's not actually needed
    private void stopTasks(List<FutureTask<Boolean>> tasks) {
    	tasks.forEach( (task) -> task.cancel(true) );
    }

	private List<FutureTask<Boolean>> createRenderTasks(DrawcallStaticDataExt dcsd) {
		int processors = Runtime.getRuntime().availableProcessors();
        int xStep = dcsd.privateDcsd.width / Math.min( dcsd.privateDcsd.width, Math.max( 2, processors ) );
        int yStep = dcsd.privateDcsd.height / Math.min( dcsd.privateDcsd.height, 3 );

        List< FutureTask< Boolean > > tasks = new LinkedList< FutureTask< Boolean > >();
        for( int x = 0; x < dcsd.privateDcsd.width; x += xStep )
            for( int y = 0; y < dcsd.privateDcsd.height; y += yStep )
                tasks.add( createRenderTask(dcsd, x, y, xStep, yStep) );

		return tasks;
	}

	FutureTask<Boolean> createRenderTask(DrawcallStaticDataExt dcsd, int x, int y, int xStep, int yStep) {
		int xEnd = Math.min( x + xStep, dcsd.privateDcsd.width - 1 );
		int yEnd = Math.min( y + yStep, dcsd.privateDcsd.height - 1 );
        return new FutureTask< Boolean >(new RenderingTask( dcsd.privateDcsd, x, y, xEnd, yEnd ) );
	}
	
    @Override
    public void setSurfaceFamily( String expression )
        throws Exception
    {
        super.setSurfaceFamily( expression );
        degree = null;
    }

    @Override
    public void setSurfaceFamily( PolynomialOperation expression )
    {
        super.setSurfaceFamily( expression );
        degree = null;
    }

    public int getSurfaceFamilyDegree() {
        if( degreeCalculator == null )
            degreeCalculator = new DegreeCalculator();
        if( degree == null )
            degree = getSurfaceFamily().accept( degreeCalculator, (Void) null );
        return degree;
    }
}
