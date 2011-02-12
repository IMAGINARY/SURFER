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
class PerspectiveCameraRayCreator extends RayCreator
{
    private Point3f upperLeft;
    private Vector3f dx;
    private Vector3f dy;
    private Point3f clippingUpperLeft;
    private Vector3f clippingDx;
    private Vector3f clippingDy;
    private Point3f surfaceUpperLeft;
    private Vector3f surfaceDx;
    private Vector3f surfaceDy;
    private Point3f rayOrigin;
    private Point3f clippingRayOrigin;
    private Point3f surfaceRayOrigin;
    private float bestStart;
    private float scale;
    private PolynomialOperation optimizedSubstitute;

    public PerspectiveCameraRayCreator( Matrix4f transformMatrix, Matrix4f surfaceTransformMatrix, Camera cam, float width, float height )
    {
        // call constructor of superclass
        super( transformMatrix, surfaceTransformMatrix, cam );
        
        // create orthographic camera width default properties
        this.upperLeft = new Point3f();
        this.upperLeft.y = ( float ) Math.tan( Math.PI / 180.0 * ( cam.getFoVY() / 2.0 ) );
        this.upperLeft.x = ( this.upperLeft.y * width ) / height;
        this.upperLeft.z = -1.0f;

        this.dx = new Vector3f( 2.0f * this.upperLeft.x, 0.0f, 0.0f );
        this.dy = new Vector3f( 0.0f, 2.0f * this.upperLeft.y, 0.0f );

        // transform properties for usage with clipping ... 
        this.clippingUpperLeft = cameraSpaceToClippingSpace( this.upperLeft );
        this.clippingDx = cameraSpaceToClippingSpace( this.dx );
        this.clippingDy = cameraSpaceToClippingSpace( this.dy );

        // ... and intersection algorithms
        this.surfaceUpperLeft = cameraSpaceToSurfaceSpace( this.upperLeft );
        this.surfaceDx = cameraSpaceToSurfaceSpace( this.dx );
        this.surfaceDy = cameraSpaceToSurfaceSpace( this.dy );

        // ray origin is always the same for all rays of this perpectiive camera
        this.rayOrigin = new Point3f( 0.0f, 0.0f, 0.0f );
        this.clippingRayOrigin = cameraSpaceToClippingSpace( this.rayOrigin );
        this.surfaceRayOrigin = cameraSpaceToSurfaceSpace( this.rayOrigin );
        
        // calculate optimal camera properties, so that the origin of the surface
        // ray is near the centre of the clipping sphere and
        // that the length of the surface ray direction is about 0.5
        Vector3f clippingRayDir = new Vector3f( Helper.interpolate2D( this.clippingUpperLeft, this.clippingDx, this.clippingDy, 0.5f, 0.5f ) );
        clippingRayDir.sub( this.clippingRayOrigin );
        Vector3f surfaceRayDir = new Vector3f( Helper.interpolate2D( this.surfaceUpperLeft, this.surfaceDx, this.surfaceDy, 0.5f, 0.5f ) );
        this.bestStart = -new Vector3f( this.clippingUpperLeft ).dot( clippingRayDir ) / clippingRayDir.dot( clippingRayDir );
        this.scale = 1.0f / ( 2.0f * surfaceRayDir.length() );
        
        this.optimizedSubstitute = new PolynomialMultiplication( new DoubleValue( this.scale ), new PolynomialVariable( PolynomialVariable.Var.x ) );
        this.optimizedSubstitute = new PolynomialAddition( optimizedSubstitute, new DoubleValue( this.bestStart ) );
    }

    public Ray createCameraSpaceRay( float u, float v )
    {
        Vector3f dir = new Vector3f( Helper.interpolate2D( this.upperLeft, this.dx, this.dy, u, v ) );
        dir.sub( this.rayOrigin );
        Ray result = new Ray( this.rayOrigin, dir );
        result.o = Helper.interpolate1D( result.o, result.d, bestStart );
        result.d.scale( scale );
        return result;
    }

    public Ray createSurfaceSpaceRay( float u, float v )
    {
        Vector3f dir = new Vector3f( Helper.interpolate2D( this.surfaceUpperLeft, this.surfaceDx, this.surfaceDy, u, v ) );
        dir.sub( this.surfaceRayOrigin );
        Ray result = new Ray( this.surfaceRayOrigin, dir );
        result.o = Helper.interpolate1D( result.o, result.d, bestStart );
        result.d.scale( scale );
        return result;
    }

    public Ray createClippingSpaceRay( float u, float v )
    {
        Vector3f dir = new Vector3f( Helper.interpolate2D( this.clippingUpperLeft, this.clippingDx, this.clippingDy, u, v ) );
        dir.sub( this.clippingRayOrigin );
        Ray result = new Ray( this.clippingRayOrigin, dir );
        result.o = Helper.interpolate1D( result.o, result.d, bestStart );
        result.d.scale( scale );
        return result;
    }
    
    public float getEyeLocation() { return -this.bestStart; }
    
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

}
