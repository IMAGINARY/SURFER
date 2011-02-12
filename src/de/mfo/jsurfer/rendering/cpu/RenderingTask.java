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
public class RenderingTask implements Callable<Void>
{
    // initialized by the contructor
    private int xStart;
    private int yStart;
    private int xEnd;
    private int yEnd;
    private DrawcallStaticData dcsd;

    public RenderingTask( DrawcallStaticData dcsd, int xStart, int yStart, int xEnd, int yEnd )
    {
        this.dcsd = dcsd;
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
    }

    public Void call()
    {
        try
        {
            render();
        } catch( Throwable t )
        {
            System.err.println( "Uncaught exception in thread " + Thread.currentThread() + ": " + t );
            t.printStackTrace();
        }
        return null;
    }

    protected void render()
    {
        switch( dcsd.antiAliasingPattern )
        {
            case PATTERN_1x1:
            {
                // no antialising -> sample pixel center
                for( int y = yStart; y <= yEnd; y++ )
                {
                    float v = -y / ( dcsd.height - 1.0f );
                    ColumnSubstitutor someB = dcsd.someA.setV( v );
                
                    for( int x = xStart; x <= xEnd; x++ )
                    {
                        float u = -x / ( dcsd.width - 1.0f );
                        UnivariatePolynomial p = someB.setU( u );

                        dcsd.colorBuffer[ dcsd.width * y + x ] = tracePolynomial( p, u, v ).get().getRGB();                        
                        //dcsd.colorBuffer[ dcsd.width * y + x ] = traceRay( u, v ).get().getRGB();
                    }
                }
                break;
            }
            default:
            {
                // all other antialiasing modes
                // first sample canvas at pixel corners and cast primary rays
                Color3f[] internalColorBuffer = new Color3f[ ( xEnd - xStart + 2 ) * ( yEnd - yStart + 2 ) ];
                for( int y = yStart; y <= yEnd + 1; y++ )
                {
                    int internalColorBufferIndex = ( xEnd - xStart + 2 ) * ( y - yStart );
                    float v = -( y - 0.5f ) / ( dcsd.height - 1.0f );
                    ColumnSubstitutor someB = dcsd.someA.setV( v );
                    
                    for( int x = xStart; x <= xEnd + 1; x++ )
                    {
                        // current position on viewing plane
                        float u = -( x - 0.5f ) / ( dcsd.width - 1.0f );
                        UnivariatePolynomial p = someB.setU( u );

                        // trace rays corresponding to (u,v)-coordinates on viewing plane
                        internalColorBuffer[internalColorBufferIndex++] = tracePolynomial( p, u, v );
                    }
                }

                // antialias pixels
                int ulIndex = 0; // index of color of upper left corner of the current pixel
                for( int y = yStart; y <= yEnd; y++ )
                {
                    int colorBufferIndex = dcsd.width * y + xStart;
                    for( int x = xStart; x <= xEnd; x++ )
                    {
                        Color3f ulColor = internalColorBuffer[ulIndex];
                        Color3f urColor = internalColorBuffer[ulIndex + 1];
                        Color3f llColor = internalColorBuffer[ulIndex + ( xEnd - xStart + 2 )];
                        Color3f lrColor = internalColorBuffer[ulIndex + ( xEnd - xStart + 2 ) + 1];

                        dcsd.colorBuffer[colorBufferIndex++] = antiAliasPixel( x, y, dcsd.antiAliasingPattern, ulColor, urColor, llColor, lrColor ).get().getRGB();
                        ulIndex++;
                    }
                    ulIndex++;
                }
            }
        }
    }

    private Color3f antiAliasPixel( int x, int y, AntiAliasingPattern aap, Color3f ulColor, Color3f urColor, Color3f llColor, Color3f lrColor )
    {
        // adaptive sampling
        Color3f finalColor;
        float thresholdSqr = dcsd.antiAliasingThreshold * dcsd.antiAliasingThreshold;
        if( colorDiffSqr( ulColor, urColor ) > thresholdSqr ||
            colorDiffSqr( ulColor, llColor ) > thresholdSqr ||
            colorDiffSqr( ulColor, lrColor ) > thresholdSqr ||
            colorDiffSqr( urColor, llColor ) > thresholdSqr ||
            colorDiffSqr( urColor, lrColor ) > thresholdSqr ||
            colorDiffSqr( llColor, lrColor ) > thresholdSqr )
        {
            if(false)
            return new Color3f( Color.BLACK );
            // anti-alias pixel with advanced sampling pattern
            finalColor = new Color3f();
            finalColor.scaleAdd( aap.getWeight( 0, 0 ), ulColor, finalColor );
            finalColor.scaleAdd( aap.getWeight( 0, aap.getSize() - 1 ), urColor, finalColor );
            finalColor.scaleAdd( aap.getWeight( aap.getSize() - 1, 0 ), llColor, finalColor );
            finalColor.scaleAdd( aap.getWeight( aap.getSize() - 1, aap.getSize() - 1 ), lrColor, finalColor );

            // first row (ul- and ur-samples left out)
            {
                int row = 0;
                float v = -( y - 0.5f + row / ( ( float ) ( aap.getSize() - 1 ) ) ) / ( dcsd.height - 1.0f );
                ColumnSubstitutor someB = dcsd.someA.setV( v );
                for( int col = 1; col < aap.getSize() - 1; col++ )
                {
                    float u = -( x - 0.5f + col / ( ( float ) ( aap.getSize() - 1 ) ) ) / ( dcsd.width - 1.0f );
                    UnivariatePolynomial p = someB.setU( u );
                    finalColor.scaleAdd( aap.getWeight( row, col ), tracePolynomial( p, u, v ), finalColor );
                }
            }
            // second row to row before last
            for( int row = 1; row < aap.getSize() - 1; row++ )
            {
                float v = -( y - 0.5f + row / ( ( float ) ( aap.getSize() - 1 ) ) ) / ( dcsd.height - 1.0f );
                ColumnSubstitutor someB = dcsd.someA.setV( v );
                for( int col = 0; col < aap.getSize(); col++ )
                {
                    float u = -( x - 0.5f + col / ( ( float ) ( aap.getSize() - 1 ) ) ) / ( dcsd.width - 1.0f );
                    UnivariatePolynomial p = someB.setU( u );
                    finalColor.scaleAdd( aap.getWeight( row, col ), tracePolynomial( p, u, v ), finalColor );
                }
            }
            // last row (ll- and lr-samples left out)
            {
                int row = aap.getSize() - 1;
                float v = -( y - 0.5f + row / ( ( float ) ( aap.getSize() - 1 ) ) ) / ( dcsd.height - 1.0f );
                ColumnSubstitutor someB = dcsd.someA.setV( v );
                for( int col = 1; col < aap.getSize() - 1; col++ )
                {
                    float u = -( x - 0.5f + col / ( ( float ) ( aap.getSize() - 1 ) ) ) / ( dcsd.width - 1.0f );
                    UnivariatePolynomial p = someB.setU( u );
                    finalColor.scaleAdd( aap.getWeight( row, col ), tracePolynomial( p, u, v ), finalColor );
                }
            }
        }
        else
        {
            // just use average of pixel-corner colors
            finalColor = new Color3f( ulColor );
            finalColor.add( urColor );
            finalColor.add( llColor );
            finalColor.add( lrColor );
            finalColor.scale( 0.25f );
        }

        // clamp color, because floating point operations may yield values outside [0,1]
        finalColor.clamp( 0f, 1f );
        return finalColor;
    }

    private Color3f tracePolynomial( UnivariatePolynomial surfacePoly, float u, float v )
    {
        // create rays
        Ray ray = dcsd.rayCreator.createCameraSpaceRay( u, v );
        Ray clippingRay = dcsd.rayCreator.createClippingSpaceRay( u, v );
        Ray surfaceRay = dcsd.rayCreator.createSurfaceSpaceRay( u, v );

        Point3f eye = Helper.interpolate1D( ray.o, ray.d, dcsd.rayCreator.getEyeLocation() );

        // optimize rays and root-finder parameters
        //optimizeRays( ray, clippingRay, surfaceRay );

        // clip ray against unit sphere
        Vector2f interval = new Vector2f();
        if( clipToSphere( clippingRay, interval ) )
        {
            // adjust interval, so that it does not start before the eye point
            float eyeLocation = ( float ) dcsd.rayCreator.getEyeLocation();
            if( interval.x < eyeLocation && eyeLocation < interval.y )
                interval.x = Math.max( interval.x, eyeLocation );            
            
            // intersect ray with surface and shade pixel
            float[] hit = new float[ 1 ];
            if( intersectPolynomial( surfacePoly, interval.x, interval.y, hit ) )
                return shade( ray, surfaceRay, hit[ 0 ], eye );
            else
                return  dcsd.backgroundColor;
        }
        else
        {
            return dcsd.backgroundColor;
        }
    }
    
    private Color3f traceRay( float u, float v )
    {
        // create rays
        Ray ray = dcsd.rayCreator.createCameraSpaceRay( u, v );
        Ray clippingRay = dcsd.rayCreator.createClippingSpaceRay( u, v );
        Ray surfaceRay = dcsd.rayCreator.createSurfaceSpaceRay( u, v );

        Point3f eye = Helper.interpolate1D( ray.o, ray.d, dcsd.rayCreator.getEyeLocation() );

        // optimize rays and root-finder parameters
        //optimizeRays( ray, clippingRay, surfaceRay );
        
        //System.out.println( u + "," + v + ":("+surfaceRay.o.x+","+surfaceRay.o.y+","+surfaceRay.o.z+")"+"("+surfaceRay.d.x+","+surfaceRay.d.y+","+surfaceRay.d.z+")t" );


        // clip ray against unit sphere
        Vector2f interval = new Vector2f();
        if( clipToSphere( clippingRay, interval ) )
        {
            // adjust interval, so that it does not start before the eye point
            float eyeLocation = ( float ) dcsd.rayCreator.getEyeLocation();
            if( interval.x < eyeLocation && eyeLocation < interval.y )
                interval.x = Math.max( interval.x, eyeLocation );            
            
            // intersect ray with surface and shade pixel
            float[] hit = new float[ 1 ];
            if( intersect( surfaceRay, interval.x, interval.y, hit ) )
                return shade( ray, surfaceRay, hit[ 0 ], eye );
            else
                return dcsd.backgroundColor;
        }
        else
        {
            return dcsd.backgroundColor;
        }
    }

    private float colorDiffSqr( Color3f c1, Color3f c2 )
    {
        Vector3f diff = new Vector3f( c1 );
        diff.sub( c2 );
        return diff.dot( diff );
    }

    protected boolean intersectPolynomial( UnivariatePolynomial p, float rayStart, float rayEnd, float[] hit )
    {   
        //System.out.println( p );
        hit[ 0 ] = ( float ) dcsd.realRootFinder.findFirstRootIn( p, rayStart, rayEnd );
        return !Float.isNaN( hit[ 0 ] );
    }
    
    protected boolean intersect( Ray r, float rayStart, float rayEnd, float[] hit )
    {
        UnivariatePolynomial x = new UnivariatePolynomial( r.o.x, r.d.x );
        UnivariatePolynomial y = new UnivariatePolynomial( r.o.y, r.d.y );
        UnivariatePolynomial z = new UnivariatePolynomial( r.o.z, r.d.z );

        UnivariatePolynomial p = dcsd.coefficientCalculator.calculateCoefficients( x, y, z );
        p = p.shrink();

        hit[ 0] = ( float ) dcsd.realRootFinder.findFirstRootIn( p, rayStart, rayEnd );
        return !Float.isNaN( hit[ 0 ] );
    }

    protected boolean clipToSphere( Ray r, Vector2f interval )
    {
        Vector3f my_o = new Vector3f( r.o );
        Vector3f my_d = new Vector3f( r.d );
        float length = my_d.length();
        my_d.scale( 1.0f / length );

        // solve algebraic
        float B = -my_o.dot( my_d );
        float C = my_o.dot( my_o ) - 1.0f;
        float D = B * B - C;

        if( D < 0.0f )
            return false;

        float sqrtD = ( float ) Math.sqrt( D );
        interval.set( B - sqrtD, B + sqrtD );
        interval.scale( 1.0f / length );
        return true;
    }

    protected Color3f shade( Ray ray, Ray surfaceRay, float hit, Point3f eye )
    {
        // compute normal
        Point3f hitPoint = new Point3f();
        hitPoint.scaleAdd( hit, ray.d, ray.o );
        Point3f surfaceHitPoint = new Point3f();
        surfaceHitPoint.scaleAdd( hit, surfaceRay.d, surfaceRay.o );
        Vector3f n = dcsd.gradientCalculator.calculateGradient( surfaceHitPoint );

        // transform normal from surface space to camera space
        n = dcsd.rayCreator.surfaceSpaceNormalToCameraSpaceNormal( n );
        
        // normalize only if point is not singular
        float nLength = n.length();
        if( nLength != 0.0f )
            n.scale( 1.0f / nLength );

        // compute view vector
        Vector3f v = new Vector3f( eye );
        v.sub( hitPoint );

        // compute, which material to use
        if( n.dot( v ) > 0.0f )
        {
            return shadeWithMaterial( hitPoint, v, n, dcsd.frontAmbientColor, dcsd.frontLightProducts );
        }
        else
        {
            n.negate();
            return shadeWithMaterial( hitPoint, v, n, dcsd.backAmbientColor, dcsd.backLightProducts );
        }
    }

    /**
     * Shades a point with the same algorithm used by the
     * {@link <a href="http://surf.sourceforge.net">surf raytracer</a>}.
     * @param hitPoint Intersection point.
     * @param v View vector (from intersection point to eye).
     * @param n Surface normal.
     * @param material Surface material.
     * @return
     */
    protected Color3f shadeWithMaterial( Point3f hitPoint, Vector3f v, Vector3f n, Color3f ambientColor, LightProducts[] lightProducts )
    {
        Vector3f l = new Vector3f();
        Vector3f h = new Vector3f();

        Color3f color = new Color3f( ambientColor );

        for( int i = 0; i < dcsd.lightSources.length; i++ )
        {
            LightSource lightSource = dcsd.lightSources[i];

            l.sub( lightSource.getPosition(), hitPoint );
            l.normalize();

            float lambertTerm = n.dot( l );
            if( lambertTerm > 0.0f )
            {
                // compute diffuse color component
                color.scaleAdd( lambertTerm, lightProducts[i].getDiffuseProduct(), color );

                // compute specular color component
                h.add( l, v );
                h.normalize();

                color.scaleAdd( ( float ) Math.pow( Math.max( 0.0f, n.dot( h ) ), lightProducts[i].getMaterial().getShininess() ), lightProducts[i].getSpecularProduct(), color );
            }
        }

        color.clampMax( 1.0f );

        return color;
    }
}
    
