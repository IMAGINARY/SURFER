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
//import java.awt.*;

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
        }
        catch( RuntimeException re )
        {
            re.printStackTrace();
            throw re;
        }
        return null;
    }

    private class ColumnSubstitutorPair
    {
        ColumnSubstitutorPair( ColumnSubstitutor scs, ColumnSubstitutorForGradient gcs )
        {
            this.scs = scs;
            this.gcs = gcs;
        }

        ColumnSubstitutor scs;
        ColumnSubstitutorForGradient gcs;
    }

    protected void render()
    {
        switch( dcsd.antiAliasingPattern )
        {
            case OG_1x1:
            {
                // no antialising -> sample pixel center
                int internal_width = xEnd - xStart + 1;
                int internal_height = yEnd - yStart + 1;
                double u_start = dcsd.rayCreator.transformU( xStart / ( dcsd.width - 1.0 ) );
                double v_start = dcsd.rayCreator.transformV( yStart / ( dcsd.height - 1.0 ) );
                double u_incr = ( dcsd.rayCreator.getUInterval().y - dcsd.rayCreator.getUInterval().x ) / ( dcsd.width - 1.0 );
                double v_incr = ( dcsd.rayCreator.getVInterval().y - dcsd.rayCreator.getVInterval().x ) / ( dcsd.height - 1.0 );
                for( int y = 0; y < internal_height; y++ )
                {
                    double v = v_start + y * v_incr;
                    ColumnSubstitutor scs = dcsd.surfaceRowSubstitutor.setV( v );
                    ColumnSubstitutorForGradient gcs = dcsd.gradientRowSubstitutor.setV( v );
                
                    for( int x = 0; x < internal_width; x++ )
                    {
                        if( Thread.interrupted() )
                            return;
                        double u = u_start + x * u_incr;
                        dcsd.colorBuffer[ dcsd.width * ( yStart + y ) + xStart + x ] = tracePolynomial( scs, gcs, u, v ).get().getRGB();
                        //dcsd.colorBuffer[ dcsd.width * y + x ] = traceRay( u, v ).get().getRGB();
                    }
                }
                break;
            }
            default:
            {
                // all other antialiasing modes
                // first sample canvas at pixel corners and cast primary rays
                int internal_width = xEnd - xStart + 2;
                int internal_height = yEnd - yStart + 2;
                Color3f[] internalColorBuffer = new Color3f[ internal_width * internal_height ];
                
                ColumnSubstitutor scs = null;
                ColumnSubstitutorForGradient gcs = null;
                HashMap< java.lang.Double, ColumnSubstitutorPair > csp_hm = new HashMap< java.lang.Double, ColumnSubstitutorPair >();
                double u_start = dcsd.rayCreator.transformU( ( xStart - 0.5 ) / ( dcsd.width - 1.0 ) );
                double v_start = dcsd.rayCreator.transformV( ( yStart - 0.5 ) / ( dcsd.height - 1.0 ) );
                double u_incr = ( dcsd.rayCreator.getUInterval().y - dcsd.rayCreator.getUInterval().x ) / ( dcsd.width - 1.0 );
                double v_incr = ( dcsd.rayCreator.getVInterval().y - dcsd.rayCreator.getVInterval().x ) / ( dcsd.height - 1.0 );
                double v = 0.0;
                for( int y = 0; y < internal_height; ++y )
                {
                    csp_hm.clear(); csp_hm.put( v, new ColumnSubstitutorPair( scs, gcs ) );

                    v = v_start + y * v_incr;
                    scs = dcsd.surfaceRowSubstitutor.setV( v );
                    gcs = dcsd.gradientRowSubstitutor.setV( v );
                    
                    csp_hm.put( v, new ColumnSubstitutorPair( scs, gcs ) );

                    for( int x = 0; x < internal_width; ++x )
                    {
                        if( Thread.interrupted() )
                            return;

                        // current position on viewing plane
                        double u = u_start + x * u_incr;
                        // trace rays corresponding to (u,v)-coordinates on viewing plane

                        internalColorBuffer[ y * internal_width + x ] = tracePolynomial( scs, gcs, u, v );
                        if( x > 0 && y > 0 )
                        {
                            Color3f ulColor = internalColorBuffer[ y * internal_width + x - 1 ];
                            Color3f urColor = internalColorBuffer[ y * internal_width + x ];
                            Color3f llColor = internalColorBuffer[ ( y - 1 ) * internal_width + x - 1];
                            Color3f lrColor = internalColorBuffer[ ( y - 1 ) * internal_width + x ];

                            dcsd.colorBuffer[ ( yStart + y - 1 ) * dcsd.width + ( xStart + x - 1 ) ] = antiAliasPixel( u - u_incr, v - v_incr, u_incr, v_incr, dcsd.antiAliasingPattern, ulColor, urColor, llColor, lrColor, csp_hm ).get().getRGB();
                        }
                    }
                }
            }
        }
    }

    private Color3f antiAliasPixel( double ll_u, double ll_v, double u_incr, double v_incr, AntiAliasingPattern aap, Color3f ulColor, Color3f urColor, Color3f llColor, Color3f lrColor, HashMap< java.lang.Double, ColumnSubstitutorPair > csp_hm )
    {
        // first average pixel-corner colors
        Color3f finalColor;

        // adaptive supersampling
        float thresholdSqr = dcsd.antiAliasingThreshold * dcsd.antiAliasingThreshold;
        if( aap != AntiAliasingPattern.OG_2x2 && ( colorDiffSqr( ulColor, urColor ) > thresholdSqr ||
            colorDiffSqr( ulColor, llColor ) > thresholdSqr ||
            colorDiffSqr( ulColor, lrColor ) > thresholdSqr ||
            colorDiffSqr( urColor, llColor ) > thresholdSqr ||
            colorDiffSqr( urColor, lrColor ) > thresholdSqr ||
            colorDiffSqr( llColor, lrColor ) > thresholdSqr ) )
        {
            // anti-alias pixel with advanced sampling pattern
            finalColor = new Color3f();
            for( AntiAliasingPattern.SamplingPoint sp : aap )
            {
                if( Thread.interrupted() )
                    return finalColor;

                Color3f ss_color;
                if( sp.getU() == 0.0 && sp.getV() == 0.0 )
                    ss_color = llColor;
                else if( sp.getU() == 0.0 && sp.getV() == 1.0 )
                    ss_color = ulColor;
                else if( sp.getU() == 1.0 && sp.getV() == 1.0 )
                    ss_color = urColor;
                else if( sp.getU() == 1.0 && sp.getV() == 0.0 )
                    ss_color = lrColor;
                else
                {
                    // color of this sample point is not known -> calculate
                    double v = ll_v + sp.getV() * v_incr;
                    double u = ll_u + sp.getU() * u_incr;
                    ColumnSubstitutorPair csp = csp_hm.get( v );
                    if( csp == null )
                    {
                        csp = new ColumnSubstitutorPair( dcsd.surfaceRowSubstitutor.setV( v ), dcsd.gradientRowSubstitutor.setV( v ) );
                        csp_hm.put( v, csp );
                    }
                    ss_color = tracePolynomial( csp.scs, csp.gcs, u, v );
                }
                finalColor.scaleAdd( sp.getWeight(), ss_color, finalColor );
                
                if( false )
                    return new Color3f( 0, 0, 0 ); // paint pixels, that are supposed to be anti-aliased in black
            }
        }
        else
        {
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

    private Color3f tracePolynomial( ColumnSubstitutor scs, ColumnSubstitutorForGradient gcs, double u, double v )
    {
        // create rays
        Ray ray = dcsd.rayCreator.createCameraSpaceRay( u, v );
        Ray clippingRay = dcsd.rayCreator.createClippingSpaceRay( u, v );
        Ray surfaceRay = dcsd.rayCreator.createSurfaceSpaceRay( u, v );

        Point3d eye = ray.at( dcsd.rayCreator.getEyeLocationOnRay() );
        UnivariatePolynomialVector3d gradientPolys = null;

        // optimize rays and root-finder parameters
        //optimizeRays( ray, clippingRay, surfaceRay );

        // clip ray
        List< Vector2d > intervals = dcsd.rayClipper.clipRay( clippingRay );
        if( !intervals.isEmpty() )
        {
            UnivariatePolynomial surfacePoly = scs.setU( u );
            for( Vector2d interval : intervals )
            {
                // adjust interval, so that it does not start before the eye point
                double eyeLocation = dcsd.rayCreator.getEyeLocationOnRay();
                if( interval.x < eyeLocation && eyeLocation < interval.y )
                    interval.x = Math.max( interval.x, eyeLocation );

                // intersect ray with surface and shade pixel
                //double[] hits = hits = dcsd.realRootFinder.findAllRootsIn( surfacePoly, interval.x, interval.y );
                double[] hits = { dcsd.realRootFinder.findFirstRootIn( surfacePoly, interval.x, interval.y ) };
                if( java.lang.Double.isNaN( hits[ 0 ]  ))
                    hits = new double[ 0 ];
                for( double hit : hits )
                {
                    if( dcsd.rayClipper.clipPoint( surfaceRay.at( hit ), true ) )
                    {
                        if( gradientPolys == null )
                            gradientPolys = gcs.setU( u );
                        Vector3d n_surfaceSpace = gradientPolys.setT( hit );
                        Vector3d n_cameraSpace = dcsd.rayCreator.surfaceSpaceNormalToCameraSpaceNormal( n_surfaceSpace );

                        return shade( ray.at( hit ), n_cameraSpace, eye );
                    }
                }
            }
        }
        return dcsd.backgroundColor;
    }
    
//    private Color3f traceRay( double u, double v )
//    {
//        // create rays
//        Ray ray = dcsd.rayCreator.createCameraSpaceRay( u, v );
//        Ray clippingRay = dcsd.rayCreator.createClippingSpaceRay( u, v );
//        Ray surfaceRay = dcsd.rayCreator.createSurfaceSpaceRay( u, v );
//
//        Point3d eye = Helper.interpolate1D( ray.o, ray.d, dcsd.rayCreator.getEyeLocationOnRay() );
//        UnivariatePolynomialVector3d gradientPolys = null;
//
//        // optimize rays and root-finder parameters
//        //optimizeRays( ray, clippingRay, surfaceRay );
//
//        //System.out.println( u + "," + v + ":("+surfaceRay.o.x+","+surfaceRay.o.y+","+surfaceRay.o.z+")"+"("+surfaceRay.d.x+","+surfaceRay.d.y+","+surfaceRay.d.z+")t" );
//
//        // clip ray
//        List< Vector2d > intervals = dcsd.rayClipper.clipRay( clippingRay );
//        for( Vector2d interval : intervals )
//        {
//            // adjust interval, so that it does not start before the eye point
//            double eyeLocation = dcsd.rayCreator.getEyeLocationOnRay();
//
//            if( interval.x < eyeLocation && eyeLocation < interval.y )
//                interval.x = Math.max( interval.x, eyeLocation );
//
//            // intersect ray with surface and shade pixel
//            double[] hit = new double[ 1 ];
//            if( intersect( surfaceRay, interval.x, interval.y, hit ) )
//                if( dcsd.rayClipper.clipPoint( surfaceRay.at( hit[ 0 ] ), true ) )
//                {
//                        if( gradientPolys == null )
//                            gradientPolys = gcs.setU( u );
//                        Vector3d n_surfaceSpace = gradientPolys.setT( hit );
//                        Vector3d n_cameraSpace = dcsd.rayCreator.surfaceSpaceNormalToCameraSpaceNormal( n_surfaceSpace );
//
//                        return shade( ray.at( hit ), n_cameraSpace, eye );
//                }
//                    return shade( ray, surfaceRay, hit[ 0 ], eye );
//        }
//        return dcsd.backgroundColor;
//    }

    private float colorDiffSqr( Color3f c1, Color3f c2 )
    {
        Vector3f diff = new Vector3f( c1 );
        diff.sub( c2 );
        return diff.dot( diff );
    }

    protected boolean intersectPolynomial( UnivariatePolynomial p, double rayStart, double rayEnd, double[] hit )
    {   
        //System.out.println( p );
        hit[ 0 ] = dcsd.realRootFinder.findFirstRootIn( p, rayStart, rayEnd );
        return !java.lang.Double.isNaN( hit[ 0 ] );
    }

    protected boolean intersect( Ray r, double rayStart, double rayEnd, double[] hit )
    {
        UnivariatePolynomial x = new UnivariatePolynomial( r.o.x, r.d.x );
        UnivariatePolynomial y = new UnivariatePolynomial( r.o.y, r.d.y );
        UnivariatePolynomial z = new UnivariatePolynomial( r.o.z, r.d.z );

        UnivariatePolynomial p = dcsd.coefficientCalculator.calculateCoefficients( x, y, z );
        p = p.shrink();

        hit[ 0 ] = ( float ) dcsd.realRootFinder.findFirstRootIn( p, rayStart, rayEnd );
        return !java.lang.Double.isNaN( hit[ 0 ] );
    }

    boolean blowUpChooseMaterial( Point3d p )
    {
        double R;
        if( dcsd.rayClipper instanceof de.mfo.jsurfer.rendering.cpu.clipping.ClipBlowUpSurface )
            R = ( ( de.mfo.jsurfer.rendering.cpu.clipping.ClipBlowUpSurface ) dcsd.rayClipper ).get_R();
        else
            R = 1.0;
        
	double u = p.x;
	double tmp = Math.sqrt( p.y*p.y + p.z*p.z );
	double v = R + tmp;
	double dist = u * u + v * v;
	if( dist > 1.0 )
		v = R - tmp; // choose the solution inside the disc
	return ( 3.0 * dist ) % 2.0 < 1.0;
    }

    /**
     * Calculates the shading in camera space
     * @param p The hit point on the surface in camera space.
     * @param n The surface normal at the hit point in camera space.
     * @param eye The eye point in camera space.
     * @return
     */
    protected Color3f shade( Point3d p, Vector3d n, Point3d eye )
    {        
        // normalize only if point is not singular
        float nLength = (float) n.length();
        if( nLength != 0.0f )
            n.scale( 1.0f / nLength );

        // compute view vector
        Vector3d v = new Vector3d( eye );
        v.sub( p );
        v.normalize();
/*
        // special coloring for blowup-visualization
        if( n.dot( v ) < 0.0f )
            n.negate();
        if( blowUpChooseMaterial( dcsd.rayCreator.cameraSpaceToSurfaceSpace( p ) ) )
        {
            return shadeWithMaterial( p, v, n, dcsd.frontAmbientColor, dcsd.frontLightProducts );
        }
        else
        {
            return shadeWithMaterial( p, v, n, dcsd.backAmbientColor, dcsd.backLightProducts );
        }
*/
        // compute, which material to use
        if( n.dot( v ) > 0.0f )
        {
            return shadeWithMaterial( p, v, n, dcsd.frontAmbientColor, dcsd.frontLightProducts );
        }
        else
        {
            n.negate();
            return shadeWithMaterial( p, v, n, dcsd.backAmbientColor, dcsd.backLightProducts );
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
    protected Color3f shadeWithMaterial( Point3d hitPoint, Vector3d v, Vector3d n, Color3f ambientColor, LightProducts[] lightProducts )
    {
        Vector3d l = new Vector3d();
        Vector3d h = new Vector3d();

        Color3f color = new Color3f( ambientColor );

        for( int i = 0; i < dcsd.lightSources.length; i++ )
        {
            LightSource lightSource = dcsd.lightSources[i];

            l.sub( lightSource.getPosition(), hitPoint );
            l.normalize();

            float lambertTerm = (float) n.dot( l );
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
    

