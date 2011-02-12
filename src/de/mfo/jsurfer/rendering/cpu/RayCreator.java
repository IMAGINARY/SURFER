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
abstract class RayCreator
{
    private Matrix4f surfaceTransform_x_modelViewInverseMatrix;
    private Matrix4f modelViewInverseMatrix;
    private Matrix4f modelView_x_surfaceTransformInverseMatrix;
    
    RayCreator( Matrix4f transformMatrix, Matrix4f surfaceTransformMatrix, Camera cam )
    {
        Matrix4f modelViewMatrix = new Matrix4f( cam.getTransform() );
        modelViewMatrix.mul( transformMatrix );
        modelViewInverseMatrix = new Matrix4f( modelViewMatrix );
        modelViewInverseMatrix.invert();

        surfaceTransform_x_modelViewInverseMatrix = new Matrix4f( surfaceTransformMatrix );
        surfaceTransform_x_modelViewInverseMatrix.mul( modelViewInverseMatrix );
        Matrix4f surfaceTransformInverseMatrix = new Matrix4f( surfaceTransformMatrix );
        surfaceTransformInverseMatrix.invert();
        modelView_x_surfaceTransformInverseMatrix = new Matrix4f( modelViewMatrix );
        modelView_x_surfaceTransformInverseMatrix.mul( surfaceTransformInverseMatrix );
    }
    
    public static RayCreator createRayCreator( Matrix4f transformMatrix, Matrix4f surfaceTransformMatrix, Camera cam, int width, int height )
    {
        switch( cam.getCameraType() )
        {
            case ORTHOGRAPHIC_CAMERA:
                return new OrthographicCameraRayCreator( transformMatrix, surfaceTransformMatrix, cam, width, height );
            case PERSPECTIVE_CAMERA:
                return new PerspectiveCameraRayCreator( transformMatrix, surfaceTransformMatrix, cam, width, height );
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    public Vector3f cameraSpaceToSurfaceSpace( Vector3f v )
    {
        Vector4f t_v = new Vector4f( v );
        surfaceTransform_x_modelViewInverseMatrix.transform( t_v );
        return new Vector3f( t_v.x, t_v.y, t_v.z );
    }

    public Point3f cameraSpaceToSurfaceSpace( Point3f p )
    {
        Point3f t_p = new Point3f( p );
        surfaceTransform_x_modelViewInverseMatrix.transform( t_p );
        return t_p;
    }

    public Vector3f cameraSpaceToClippingSpace( Vector3f v )
    {
        Vector4f t_v = new Vector4f( v );
        modelViewInverseMatrix.transform( t_v );
        return new Vector3f( t_v.x, t_v.y, t_v.z );
    }

    public Point3f cameraSpaceToClippingSpace( Point3f p )
    {
        Point3f t_p = new Point3f( p );
        modelViewInverseMatrix.transform( t_p );
        return t_p;
    }
    
    public Vector3f surfaceSpaceNormalToCameraSpaceNormal( Vector3f n )
    {
        Vector3f t_n = new Vector3f( n );
        modelView_x_surfaceTransformInverseMatrix.transform( t_n );
        return t_n;
    }
    
    public abstract Ray createCameraSpaceRay( float u, float v );
    public abstract Ray createSurfaceSpaceRay( float u, float v );
    public abstract Ray createClippingSpaceRay( float u, float v );

    public abstract float getEyeLocation();
    
    public abstract PolynomialOperation getXForSomeA();
    public abstract PolynomialOperation getYForSomeA();
    public abstract PolynomialOperation getZForSomeA();    
}