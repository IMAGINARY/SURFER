/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mfo.jsurfer.rendering.cpu;

import de.mfo.jsurfer.rendering.*;
import de.mfo.jsurfer.algebra.*;
import javax.vecmath.*;
import java.util.concurrent.*;
import java.util.*;

import de.mfo.jsurfer.debug.*;
import java.awt.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class CPUAlgebraicSurfaceRenderer extends AlgebraicSurfaceRenderer
{
    private ExecutorService threadPoolExecutor;
    
    public DrawcallStaticData collectDrawCallStaticData( int[] colorBuffer, int width, int height )
    {
        DrawcallStaticData dcsd = new DrawcallStaticData();
        
        dcsd.colorBuffer = colorBuffer;
        dcsd.width = width;
        dcsd.height = height;
        
        dcsd.coefficientCalculator = new PolynomialExpansionCoefficientCalculator( getSurfaceExpression() );
//        dcsd.realRootFinder = new DChainRootFinder();
//        dcsd.realRootFinder = new SturmChainRootFinder(); 
        dcsd.realRootFinder = new DescartesRootFinder( false ); 
//        dcsd.realRootFinder = new GPUSuitableDescartesRootFinder2( false );
//        dcsd.realRootFinder = new BernsteinDescartesRootFinder( false );
        dcsd.gradientCalculator = new FastGradientCalculator( getGradientXExpression(), getGradientYExpression(), getGradientZExpression() );

        dcsd.frontAmbientColor = new Color3f( getFrontMaterial().getColor() );
        dcsd.frontAmbientColor.scale( getFrontMaterial().getAmbientIntensity() );

        dcsd.backAmbientColor = new Color3f( getBackMaterial().getColor() );
        dcsd.backAmbientColor.scale( getFrontMaterial().getAmbientIntensity() );

        int numOfLightSources = 0;
        for( int i = 0; i < MAX_LIGHTS; i++ )
            if( getLightSource( i ).getStatus() == LightSource.Status.ON )
                numOfLightSources++;
        dcsd.lightSources = new LightSource[ numOfLightSources ];
        dcsd.frontLightProducts = new LightProducts[ numOfLightSources ];
        dcsd.backLightProducts = new LightProducts[ numOfLightSources ];
        int lightSourceIndex = 0;
        for( int i = 0; i < MAX_LIGHTS; i++ )
        {
            LightSource lightSource = getLightSource( i );
            if( lightSource.getStatus() == LightSource.Status.ON )
            {
                dcsd.lightSources[lightSourceIndex] = lightSource;
                dcsd.frontLightProducts[lightSourceIndex] = new LightProducts( lightSource, getFrontMaterial() );
                dcsd.backLightProducts[lightSourceIndex] = new LightProducts( lightSource, getBackMaterial() );

                lightSourceIndex++;
            }
        }
        
        dcsd.backgroundColor = getBackgroundColor();
        
        dcsd.antiAliasingPattern = getAntiAliasingPattern();
        dcsd.antiAliasingThreshold = aaThreshold;
                
        dcsd.rayCreator = RayCreator.createRayCreator( getTransform(), getSurfaceTransform(), getCamera(), width, height );
        dcsd.someA = new RowSubstitutor( getSurfaceExpression(), dcsd.rayCreator.getXForSomeA(), dcsd.rayCreator.getYForSomeA(), dcsd.rayCreator.getZForSomeA() );
        
        return dcsd;
    }

    public CPUAlgebraicSurfaceRenderer()
    {
        super();
        class PriorityThreadFactory implements ThreadFactory {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setPriority( Thread.MIN_PRIORITY );
                return t;
            }
        }

        threadPoolExecutor = Executors.newFixedThreadPool( ( int ) ( Runtime.getRuntime().availableProcessors() * 2.0 ), new PriorityThreadFactory() );
        //threadPoolExecutor = Executors.newFixedThreadPool( 1 );
        this.setAntiAliasingMode( AntiAliasingMode.ADAPTIVE_SUPERSAMPLING );
        this.setAntiAliasingPattern( AntiAliasingPattern.PATTERN_4x4 );
    }

    public enum AntiAliasingMode
    {
        SUPERSAMPLING,
        ADAPTIVE_SUPERSAMPLING;
    }
    private AntiAliasingMode aaMode;
    private float aaThreshold;
    private AntiAliasingPattern aaPattern;

    public void setAntiAliasingMode( AntiAliasingMode mode )
    {
        this.aaMode = mode;
        if( mode == AntiAliasingMode.SUPERSAMPLING )
            this.aaThreshold = 0.0f;
        else
            this.aaThreshold = 0.045f;
    }

    public AntiAliasingMode getAntiAliasingMode()
    {
        return this.aaMode;
    }

    public void setAntiAliasingPattern( AntiAliasingPattern pattern )
    {
        this.aaPattern = pattern;
    }

    public AntiAliasingPattern getAntiAliasingPattern()
    {
        return this.aaPattern;
    }

    public void draw( int[] colorBuffer, int width, int height )
    {
        /*DrawcallStaticData dcsd = collectDrawCallStaticData( colorBuffer, width, height );

        LinkedList<RenderingTask> renderingTasks = new LinkedList<RenderingTask>();
        int xStep = width / Math.min( width, Runtime.getRuntime().availableProcessors() );
        int yStep = height / Math.min( height, Runtime.getRuntime().availableProcessors() );
        for( int x = 0; x < width; x += xStep )
            for( int y = 0; y < height; y += yStep )
                renderingTasks.add( new RenderingTask( dcsd, x, y, Math.min( x + xStep, width - 1 ), Math.min( y + yStep, height - 1 ) ) );
        try
        {
            threadPoolExecutor.invokeAll( renderingTasks );
        } catch( InterruptedException ie )
        {
            System.out.println( ie );
        }*/
        DrawcallStaticData dcsd = collectDrawCallStaticData( colorBuffer, width, height );

        Collection< Callable< Void > > renderingTasks = new LinkedList<Callable< Void >>();
        int xStep = width / Math.min( width, Math.max( 2, Runtime.getRuntime().availableProcessors() ) );
        int yStep = height / Math.min( height, Math.max( 2, Runtime.getRuntime().availableProcessors() ) );
        for( int x = 0; x < width; x += xStep )
            for( int y = 0; y < height; y += yStep )
            {
               renderingTasks.add( new RenderingTask( dcsd, x, y, Math.min( x + xStep, width - 1 ), Math.min( y + yStep, height - 1 ) ) );
            }

        try
        {
            threadPoolExecutor.invokeAll( renderingTasks );
        } catch( InterruptedException ie )
        {
            System.out.println( ie );
        }
    }
}
