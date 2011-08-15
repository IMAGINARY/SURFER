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
public class OrthographicCameraRayCreator extends RayCreator
{
    Point3d rayOrigin;
    Vector3d rayDir;
    Vector3d du;
    Vector3d dv;

    Point3d surfaceRayOrigin;
    Vector3d surfaceRayDir;
    Vector3d surfaceDu;
    Vector3d surfaceDv;

    Point3d clippingRayOrigin;
    Vector3d clippingRayDir;
    Vector3d clippingDu;
    Vector3d clippingDv;

    double uscale;
    double vscale;
    double uoffset;
    double voffset;

    double eyeLocationOnRay;

    public OrthographicCameraRayCreator( Matrix4d transformMatrix, Matrix4d surfaceTransformMatrix, Camera cam, double width, double height )
    {
        // call constructor of superclass
        super( transformMatrix, surfaceTransformMatrix, cam );

        // create orthographic camera width default properties (origion is (0,0,0))
        dv = new Vector3d( 0.0, cam.getHeight() / 2.0, 0.0 );
        du = new Vector3d( ( dv.y * width ) / height, 0.0, 0.0 );
        rayDir = new Vector3d( 0, 0, -1 );

        // transform view plane spanning vectors to surface space
        surfaceDu = cameraSpaceToSurfaceSpace( du );
        surfaceDv = cameraSpaceToSurfaceSpace( dv );

        uscale = surfaceDu.length();
        vscale = surfaceDv.length();
        surfaceDu.scale( 1.0 / uscale );
        surfaceDv.scale( 1.0 / vscale );

        surfaceRayDir = cameraSpaceToSurfaceSpace( rayDir );
        double rayDirScale = surfaceRayDir.length();
        surfaceRayDir.scale( 1.0 / rayDirScale );

        surfaceRayOrigin = cameraSpaceToSurfaceSpace( new Point3d( 0, 0, 0 ) );

        double planeDistance = surfaceRayDir.dot( new Vector3d( surfaceRayOrigin ) );

        Vector3d projCamOrigin = new Vector3d(); // project the cam origin onto the surface space viewing plane (which starts at (0,0,0) in surface space)
        projCamOrigin.scaleAdd( -planeDistance, surfaceRayDir, surfaceRayOrigin );

        // decompose projCamOrigin into uoffset * surfaceDu + voffset * surfaceDv
        uoffset = projCamOrigin.dot( surfaceDu );
        voffset = projCamOrigin.dot( surfaceDv );

        surfaceRayOrigin = new Point3d( 0, 0, 0 );

        // adjust camera parameters in camera space
        du.scale( 1.0 / uscale );
        dv.scale( 1.0 / vscale );
        rayDir.scale( 1.0 / rayDirScale );

        rayOrigin = new Point3d( rayDir );
        rayOrigin.scale( -planeDistance );
        rayOrigin.scaleAdd( uoffset, du, rayOrigin );
        rayOrigin.scaleAdd( voffset, dv, rayOrigin );

        clippingRayOrigin = cameraSpaceToClippingSpace( rayOrigin );
        clippingDu = cameraSpaceToClippingSpace( du );
        clippingDv = cameraSpaceToClippingSpace( dv );
        clippingRayDir = cameraSpaceToClippingSpace( rayDir );

        eyeLocationOnRay = planeDistance;
/*
        // create orthographic camera width default properties
        this.upperLeft = new Point3d();
        this.upperLeft.y = cam.getHeight() / 2.0;
        this.upperLeft.x = ( this.upperLeft.y * width ) / height;
        this.upperLeft.z = 0.0f;

        this.dx = new Vector3d( 2.0 * this.upperLeft.x, 0.0, 0.0 );
        this.dy = new Vector3d( 0.0, 2.0 * this.upperLeft.y, 0.0 );

        // transform camera parameters to surface space
        this.surfaceUpperLeft = cameraSpaceToSurfaceSpace( this.upperLeft );
        this.surfaceDx = cameraSpaceToSurfaceSpace( this.dx );
        this.surfaceDy = cameraSpaceToSurfaceSpace( this.dy );

        // adjust camera parameters to give a simple representation in

        this.clippingUpperLeft = cameraSpaceToClippingSpace( this.upperLeft );
        this.clippingDx = cameraSpaceToClippingSpace( this.dx );
        this.clippingDy = cameraSpaceToClippingSpace( this.dy );


        // ray direction is always the same for all rays of this orthographic camera
        this.rayDir = new Vector3d( 0.0, 0.0, -1.0 );
        this.clippingRayDir = cameraSpaceToClippingSpace( this.rayDir );
        this.surfaceRayDir = cameraSpaceToSurfaceSpace( this.rayDir );

        double myscale = 1.0 / this.clippingRayDir.length();
        this.rayDir.scale( myscale );
        this.clippingRayDir.scale( myscale );
        this.surfaceRayDir.scale( myscale );

        // clipping ray dir has unit length

        // optimize camera properties, so that the origin of the surface
        // ray is near the centre of the clipping sphere and
        // that the length of the surface ray direction is about 0.5
        double bestStart = -new Vector3d( createClippingSpaceRay( -0.5, -0.5 ).o ).dot( this.clippingRayDir );
        double scale = 1.0 / surfaceRayDir.length();

        // apply optimized camera properties
        this.upperLeft = Helper.interpolate1D( this.upperLeft, this.rayDir, bestStart );
        this.rayDir.scale( scale );

        this.clippingUpperLeft = Helper.interpolate1D( this.clippingUpperLeft, this.clippingRayDir, bestStart );
        this.clippingRayDir.scale( scale );

        this.surfaceUpperLeft = Helper.interpolate1D( this.surfaceUpperLeft, this.surfaceRayDir, bestStart );
        this.surfaceRayDir.scale( scale );

        // save original ray configuration (sometimes the original camera position is needed)
        this.eyeLocation = -bestStart / scale;
 * */
    }

    @Override
    public Ray createCameraSpaceRay( double u, double v )
    {
        return new Ray( Helper.interpolate2D( this.rayOrigin, this.du, this.dv, u, v ), this.rayDir );
    }

    @Override
    public Ray createSurfaceSpaceRay( double u, double v )
    {
        return new Ray( Helper.interpolate2D( this.surfaceRayOrigin, this.surfaceDu, this.surfaceDv, u, v ), this.surfaceRayDir );
    }

    @Override
    public Ray createClippingSpaceRay( double u, double v )
    {
        return new Ray( Helper.interpolate2D( this.clippingRayOrigin, this.clippingDu, this.clippingDv, u, v ), this.clippingRayDir );
    }

    @Override
    public double getEyeLocationOnRay()
    {
        return this.eyeLocationOnRay;
    }

    @Override
    public PolynomialOperation getXForSomeA()
    {
        PolynomialOperation result = new DoubleValue( this.surfaceRayOrigin.x );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.z ), new DoubleValue( this.surfaceDv.x ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.y ), new DoubleValue( this.surfaceDu.x ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.x ), new DoubleValue( this.surfaceRayDir.x ) ) );
        return result;
    }

    @Override
    public PolynomialOperation getYForSomeA()
    {
        PolynomialOperation result = new DoubleValue( this.surfaceRayOrigin.y );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.z ), new DoubleValue( this.surfaceDv.y ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.y ), new DoubleValue( this.surfaceDu.y ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.x ), new DoubleValue( this.surfaceRayDir.y ) ) );
        return result;
    }

    @Override
    public PolynomialOperation getZForSomeA()
    {
        PolynomialOperation result = new DoubleValue( this.surfaceRayOrigin.z );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.z ), new DoubleValue( this.surfaceDv.z ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.y ), new DoubleValue( this.surfaceDu.z ) ) );
        result = new PolynomialAddition( result, new PolynomialMultiplication( new PolynomialVariable( PolynomialVariable.Var.x ), new DoubleValue( this.surfaceRayDir.z ) ) );
        return result;
    }

    @Override
    public Vector2d getUInterval() { return new Vector2d( uoffset - uscale, uoffset + uscale ); }

    @Override
    public Vector2d getVInterval() { return new Vector2d( voffset - vscale, voffset + vscale ); }

    @Override
    public double transformU( double u )
    {
        double uout = uoffset + uscale * ( 2.0 * u - 1.0 );
        //System.out.println( "uin=" + u );
        //System.out.println( "uout=" + uout );
        return uout;//uoffset + uscale * ( 2.0 * u - 1.0 );
    }

    @Override
    public double transformV( double v )
    {
        double vout = voffset + vscale * ( 2.0 * v - 1.0 );
        //System.out.println( "vin=" + v );
        //System.out.println( "vout=" + vout );
        return vout;//uoffset + uscale * ( 2.0 * u - 1.0 );
    }
}