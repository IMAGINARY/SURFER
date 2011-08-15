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
public abstract class RayCreator
{
    Matrix4d cameraSpaceToSurfaceSpaceMatrix;
    Matrix4d cameraSpaceToClippingSpaceMatrix;
    Matrix4d surfaceSpaceNormalToCameraSpaceNormalMatrix;

    RayCreator( Matrix4d transformMatrix, Matrix4d surfaceTransformMatrix, Camera cam )
    {
        Matrix4d c_i = new Matrix4d( cam.getTransform() );
        c_i.invert();

        cameraSpaceToClippingSpaceMatrix = new Matrix4d( transformMatrix );
        cameraSpaceToClippingSpaceMatrix.mul( c_i );

        cameraSpaceToSurfaceSpaceMatrix = new Matrix4d( surfaceTransformMatrix );
        cameraSpaceToSurfaceSpaceMatrix.mul( cameraSpaceToClippingSpaceMatrix );

        surfaceSpaceNormalToCameraSpaceNormalMatrix = new Matrix4d( cameraSpaceToSurfaceSpaceMatrix );
        surfaceSpaceNormalToCameraSpaceNormalMatrix.invert();
    }

    public static RayCreator createRayCreator( Matrix4d transformMatrix, Matrix4d surfaceTransformMatrix, Camera cam, int width, int height )
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

    public Vector3d cameraSpaceToSurfaceSpace( Vector3d v )
    {
        Vector4d t_v = new Vector4d( v );
        cameraSpaceToSurfaceSpaceMatrix.transform( t_v );
        return new Vector3d( t_v.x, t_v.y, t_v.z );
    }


    public Point3d cameraSpaceToSurfaceSpace( Point3d p )
    {
        Point3d t_p = new Point3d( p );
        cameraSpaceToSurfaceSpaceMatrix.transform( t_p );
        return t_p;
    }

    public Vector3d cameraSpaceToClippingSpace( Vector3d v )
    {
        Vector4d t_v = new Vector4d( v );
        cameraSpaceToClippingSpaceMatrix.transform( t_v );
        return new Vector3d( t_v.x, t_v.y, t_v.z );
    }

    public Point3d cameraSpaceToClippingSpace( Point3d p )
    {
        Point3d t_p = new Point3d( p );
        cameraSpaceToClippingSpaceMatrix.transform( t_p );
        return t_p;
    }

    public Vector3d surfaceSpaceNormalToCameraSpaceNormal( Vector3d n )
    {
        Vector3d t_n = new Vector3d( n );
        surfaceSpaceNormalToCameraSpaceNormalMatrix.transform( t_n );
        return t_n;
    }

    public abstract Ray createCameraSpaceRay( double u, double v );
    public abstract Ray createSurfaceSpaceRay( double u, double v );
    public abstract Ray createClippingSpaceRay( double u, double v );

    public abstract double getEyeLocationOnRay();

    public abstract PolynomialOperation getXForSomeA();
    public abstract PolynomialOperation getYForSomeA();
    public abstract PolynomialOperation getZForSomeA();

    // get the intervals, for which u and v are in the viewport
    public abstract Vector2d getUInterval();
    public abstract Vector2d getVInterval();
    
    // transform (u,v) \in [0,1]^2 into local viewport coordinates
    public abstract double transformU( double u );
    public abstract double transformV( double v );

    public static void decomposeTRS( Matrix4d A, Matrix4d T, Matrix4d R, Matrix4d S )
    {
        assert A.m03 == 0.0 && A.m13 == 0.0 && A.m23 == 0.0 : "projective transformations are currently not supported";

        AffineDecomposition.AffineParts ap = AffineDecomposition.decompAffine( A );

        T.setIdentity();
        T.setTranslation( ap.t );

        R.setIdentity();
        R.setRotation( ap.q );

        S.setIdentity();
        S.setM00( ap.k.x );
        S.setM11( ap.k.y );
        S.setM22( ap.k.z );

        {
            Matrix4d stretch_rotation = new Matrix4d();
            stretch_rotation.setIdentity();
            stretch_rotation.setRotation( ap.u );

            Matrix4d rotated_stretch = new Matrix4d( stretch_rotation );
            rotated_stretch.mul( S );
            stretch_rotation.transpose();
            rotated_stretch.mul( stretch_rotation );

            assert S.epsilonEquals( rotated_stretch, 1e-10 ) : "shear transformations are currently not supported";
        }
    }
}