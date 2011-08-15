/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.rendering.cpu;

import javax.vecmath.*;
import de.mfo.jsurfer.rendering.*;
import de.mfo.jsurfer.algebra.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class PerspectiveCameraRayCreator extends RayCreator
{
    private Point3d upperLeft;
    private Vector3d dx;
    private Vector3d dy;
    private Point3d clippingUpperLeft;
    private Vector3d clippingDx;
    private Vector3d clippingDy;
    private Point3d surfaceUpperLeft;
    private Vector3d surfaceDx;
    private Vector3d surfaceDy;
    private Point3d rayOrigin;
    private Point3d clippingRayOrigin;
    private Point3d surfaceRayOrigin;
    private double bestStart;
    private double scale;
    private PolynomialOperation optimizedSubstitute;

    public PerspectiveCameraRayCreator( Matrix4d transformMatrix, Matrix4d surfaceTransformMatrix, Camera cam, double width, double height )
    {
        // call constructor of superclass
        super( transformMatrix, surfaceTransformMatrix, cam );
        
        // create orthographic camera width default properties
        this.upperLeft = new Point3d();
        this.upperLeft.y = ( float ) Math.tan( Math.PI / 180.0 * ( cam.getFoVY() / 2.0 ) );
        this.upperLeft.x = ( this.upperLeft.y * width ) / height;
        this.upperLeft.z = -1.0;

        this.dx = new Vector3d( 2.0 * this.upperLeft.x, 0.0, 0.0 );
        this.dy = new Vector3d( 0.0, 2.0 * this.upperLeft.y, 0.0 );

        // transform properties for usage with clipping ... 
        this.clippingUpperLeft = cameraSpaceToClippingSpace( this.upperLeft );
        this.clippingDx = cameraSpaceToClippingSpace( this.dx );
        this.clippingDy = cameraSpaceToClippingSpace( this.dy );

        // ... and intersection algorithms
        this.surfaceUpperLeft = cameraSpaceToSurfaceSpace( this.upperLeft );
        this.surfaceDx = cameraSpaceToSurfaceSpace( this.dx );
        this.surfaceDy = cameraSpaceToSurfaceSpace( this.dy );

        // ray origin is always the same for all rays of this perpectiive camera
        this.rayOrigin = new Point3d( 0.0, 0.0, 0.0 );
        this.clippingRayOrigin = cameraSpaceToClippingSpace( this.rayOrigin );
        this.surfaceRayOrigin = cameraSpaceToSurfaceSpace( this.rayOrigin );
        
        // calculate optimal camera properties, so that the origin of the surface
        // ray is near the centre of the clipping sphere and
        // that the length of the surface ray direction is about 0.5
        Vector3d clippingRayDir = new Vector3d( Helper.interpolate2D( this.clippingUpperLeft, this.clippingDx, this.clippingDy, 0.5, 0.5 ) );
        clippingRayDir.sub( this.clippingRayOrigin );
        Vector3d surfaceRayDir = new Vector3d( Helper.interpolate2D( this.surfaceUpperLeft, this.surfaceDx, this.surfaceDy, 0.5, 0.5 ) );
        this.bestStart = -new Vector3d( this.clippingUpperLeft ).dot( clippingRayDir ) / clippingRayDir.dot( clippingRayDir );
        scale = ( 0.5 * surfaceRayDir.length() ); // 1.0 / ( 2.0 * surfaceRayDir.length() );
        
        this.optimizedSubstitute = new PolynomialMultiplication( new DoubleValue( this.scale ), new PolynomialVariable( PolynomialVariable.Var.x ) );
        this.optimizedSubstitute = new PolynomialAddition( optimizedSubstitute, new DoubleValue( this.bestStart ) );
    }

    @Override
    public Ray createCameraSpaceRay( double u, double v )
    {
        Vector3d dir = new Vector3d( Helper.interpolate2D( this.upperLeft, this.dx, this.dy, u, v ) );
        dir.sub( this.rayOrigin );
        Ray result = new Ray( this.rayOrigin, dir );
        result.o = Helper.interpolate1D( result.o, result.d, bestStart );
        result.d.scale( scale );
        return result;
    }

    @Override
    public Ray createSurfaceSpaceRay( double u, double v )
    {
        Vector3d dir = new Vector3d( Helper.interpolate2D( this.surfaceUpperLeft, this.surfaceDx, this.surfaceDy, u, v ) );
        dir.sub( this.surfaceRayOrigin );
        Ray result = new Ray( this.surfaceRayOrigin, dir );
        result.o = Helper.interpolate1D( result.o, result.d, bestStart );
        result.d.scale( scale );
        return result;
    }

    @Override
    public Ray createClippingSpaceRay( double u, double v )
    {
        Vector3d dir = new Vector3d( Helper.interpolate2D( this.clippingUpperLeft, this.clippingDx, this.clippingDy, u, v ) );
        dir.sub( this.clippingRayOrigin );
        Ray result = new Ray( this.clippingRayOrigin, dir );
        result.o = Helper.interpolate1D( result.o, result.d, bestStart );
        result.d.scale( scale );
        return result;
    }
    
    @Override
    public double getEyeLocationOnRay() { return -this.bestStart; }
    
    @Override
    public PolynomialOperation getXForSomeA()
    {        
        PolynomialOperation result = new DoubleValue( this.surfaceUpperLeft.x );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.z ), new DoubleValue( this.surfaceDy.x ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.y ), new DoubleValue( this.surfaceDx.x ) ) );
        result = new PolynomialSubtraction( result, new DoubleValue( this.surfaceRayOrigin.x ) );
        result = new PolynomialMultiplication( result, optimizedSubstitute );
        result = new PolynomialAddition( result, new DoubleValue( this.surfaceRayOrigin.x ) );
        return result;
    }
    
    @Override
    public PolynomialOperation getYForSomeA()
    {
        PolynomialOperation result = new DoubleValue( this.surfaceUpperLeft.y );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.z ), new DoubleValue( this.surfaceDy.y ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.y ), new DoubleValue( this.surfaceDx.y ) ) );
        result = new PolynomialSubtraction( result, new DoubleValue( this.surfaceRayOrigin.y ) );
        result = new PolynomialMultiplication( result, optimizedSubstitute );
        result = new PolynomialAddition( result, new DoubleValue( this.surfaceRayOrigin.y ) );
        return result;
    }

    @Override
    public PolynomialOperation getZForSomeA()
    {
        PolynomialOperation result = new DoubleValue( this.surfaceUpperLeft.z );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.z ), new DoubleValue( this.surfaceDy.z ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.y ), new DoubleValue( this.surfaceDx.z ) ) );
        result = new PolynomialSubtraction( result, new DoubleValue( this.surfaceRayOrigin.z ) );
        result = new PolynomialMultiplication( result, optimizedSubstitute );
        result = new PolynomialAddition( result, new DoubleValue( this.surfaceRayOrigin.z ) );
        return result;
    }

    // get the intervals, for which u and v are in the viewport
    @Override
    public Vector2d getUInterval() { return new Vector2d( 0.0, 1.0 ); }

    @Override
    public Vector2d getVInterval() { return new Vector2d( 0.0, 1.0 ); }

    // transform (u,v) \in [0,1]^2 into local viewport coordinates
    @Override
    public double transformU( double u ) { return u; }

    @Override
    public double transformV( double v ) { return v; }

}
