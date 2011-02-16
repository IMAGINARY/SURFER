/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.rendering;

import javax.vecmath.*;
import java.util.Properties;
import de.mfo.jsurfer.util.BasicIO;

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
    
    public Properties saveProperties( Properties props, String prefix, String suffix )
    {
        props.setProperty( prefix + "type" + suffix, cameraType.toString() );
        props.setProperty( prefix + "fov_y" + suffix, "" + fovY );        
        props.setProperty( prefix + "height" + suffix, "" + height );
        props.setProperty( prefix + "transform" + suffix, BasicIO.toString( transform ) );
        return props;
    }

    public void loadProperties( Properties props, String prefix, String suffix )
    {
        String camera_type_key = prefix + "type" + suffix;
        if( props.containsKey( camera_type_key ) )
            cameraType = CameraType.valueOf( props.getProperty( camera_type_key ) );

        String fov_y_key = prefix + "fov_y" + suffix;
        if( props.containsKey( fov_y_key ) )
            fovY = Float.parseFloat( props.getProperty( fov_y_key ) );

        String height_key = prefix + "height" + suffix;
        if( props.containsKey( height_key ) )
            height = Float.parseFloat( props.getProperty( height_key ) );

        String transform_key = prefix + "transform" + suffix;
        if( props.containsKey( transform_key ) )
            transform = BasicIO.fromMatrix4fString( props.getProperty( transform_key ) );
    }
}
