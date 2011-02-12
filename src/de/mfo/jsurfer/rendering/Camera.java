/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.rendering;

import javax.vecmath.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class Camera {
    
    public enum CameraType
    {
        ORTHOGRAPHIC_CAMERA,
        PERSPECTIVE_CAMERA
    }
    
    private CameraType cameraType;
    private float fovY;
    private float height;
    private Matrix4f transform;

    public Camera()
    {
        this.cameraType = CameraType.ORTHOGRAPHIC_CAMERA;
        this.fovY = 60.0f;
        this.height = 2.0f;
        this.transform = new Matrix4f();
        this.transform.setIdentity();
    }
    
    public void lookAt( Point3f camPosition, Point3f pointOfInterest, Vector3f upVector )
    {
        Vector3f x = new Vector3f();
        Vector3f y = new Vector3f();
        Vector3f z = new Vector3f();
        
        z.sub( camPosition, pointOfInterest );
        z.normalize();
        
        x.cross( upVector, z );
        x.normalize();
        
        y.cross( z, x );
   
        this.transform.setColumn( 0, new Vector4f( x ) );
        this.transform.setColumn( 1, new Vector4f( y ) );
        this.transform.setColumn( 2, new Vector4f( z ) );

        this.transform.setColumn( 3, new Vector4f( -camPosition.x, -camPosition.y, -camPosition.z, 1f ) );
    }
    
    public void setCameraType( CameraType camType )
    {
        this.cameraType = camType;
    }
    
    public CameraType getCameraType()
    {
        return this.cameraType;
    }
    
    public void setFoVY( float fovy )
    {
        this.fovY = fovy;
    }
    
    public float getFoVY()
    {
        return this.fovY;
    }
    
    public void setHeight( float height )
    {
        this.height = height;
    }
    
    public float getHeight()
    {
        return this.height;
    }
    
    public Matrix4f getTransform()
    {
        return this.transform;
    }
}
