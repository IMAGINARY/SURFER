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
class OrthographicCameraRayCreator extends RayCreator
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
    private Vector3f rayDir;
    private Vector3f clippingRayDir;
    private Vector3f surfaceRayDir;
    private float eyeLocation;

    public OrthographicCameraRayCreator( Matrix4f transformMatrix, Matrix4f surfaceTransformMatrix, Camera cam, float width, float height )
    {
        // call constructor of superclass
        super( transformMatrix, surfaceTransformMatrix, cam );
        
        // create orthographic camera width default properties
        this.upperLeft = new Point3f();
        this.upperLeft.y = cam.getHeight() / 2.0f;
        this.upperLeft.x = ( this.upperLeft.y * width ) / height;
        this.upperLeft.z = 0.0f;
                
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

        // ray direction is always the same for all rays of this orthographic camera
        this.rayDir = new Vector3f( 0.0f, 0.0f, -1.0f );
        this.clippingRayDir = cameraSpaceToClippingSpace( this.rayDir );
        this.surfaceRayDir = cameraSpaceToSurfaceSpace( this.rayDir );
        
        // optimize camera properties, so that the origin of the surface
        // ray is near the centre of the clipping sphere and
        // that the length of the surface ray direction is about 0.5
        float bestStart = -new Vector3f( this.clippingUpperLeft ).dot( this.clippingRayDir ) / this.clippingRayDir.dot( this.clippingRayDir );
        float scale = 1.0f / ( 2.0f * surfaceRayDir.length() );
        
        // apply optimized camera properties
        this.upperLeft = Helper.interpolate1D( this.upperLeft, this.rayDir, bestStart );
        this.rayDir.scale( scale );
        
        this.clippingUpperLeft = Helper.interpolate1D( this.clippingUpperLeft, this.clippingRayDir, bestStart );
        this.clippingRayDir.scale( scale );

        this.surfaceUpperLeft = Helper.interpolate1D( this.surfaceUpperLeft, this.surfaceRayDir, bestStart );
        this.surfaceRayDir.scale( scale );
        
        // save original ray configuration (sometimes the original camera position is needed)
        this.eyeLocation = -bestStart / scale;
    }

    public Ray createCameraSpaceRay( float u, float v )
    {
        return new Ray( Helper.interpolate2D( this.upperLeft, this.dx, this.dy, u, v ), this.rayDir );
    }

    public Ray createSurfaceSpaceRay( float u, float v )
    {
        return new Ray( Helper.interpolate2D( this.surfaceUpperLeft, this.surfaceDx, this.surfaceDy, u, v ), this.surfaceRayDir );
    }

    public Ray createClippingSpaceRay( float u, float v )
    {
        return new Ray( Helper.interpolate2D( this.clippingUpperLeft, this.clippingDx, this.clippingDy, u, v ), this.clippingRayDir );
    }
    
    public float getEyeLocation()
    {
        return this.eyeLocation;
    }
    
    public PolynomialOperation getXForSomeA()
    {
        PolynomialOperation result = new DoubleValue( this.surfaceUpperLeft.x );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.z ), new DoubleValue( this.surfaceDy.x ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.y ), new DoubleValue( this.surfaceDx.x ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.x ), new DoubleValue( this.surfaceRayDir.x ) ) );
        return result;
    }
    
    public PolynomialOperation getYForSomeA()
    {
        PolynomialOperation result = new DoubleValue( this.surfaceUpperLeft.y );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.z ), new DoubleValue( this.surfaceDy.y ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.y ), new DoubleValue( this.surfaceDx.y ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.x ), new DoubleValue( this.surfaceRayDir.y ) ) );
        return result;
    }

    public PolynomialOperation getZForSomeA()
    {
        PolynomialOperation result = new DoubleValue( this.surfaceUpperLeft.z );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.z ), new DoubleValue( this.surfaceDy.z ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.y ), new DoubleValue( this.surfaceDx.z ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.x ), new DoubleValue( this.surfaceRayDir.z ) ) );
        return result;
    }
}