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
    private double fovY;
    private double height;
    private Matrix4d transform;

    public Camera()
    {
        this.cameraType = CameraType.ORTHOGRAPHIC_CAMERA;
        this.fovY = 60.0;
        this.height = 2.0;
        this.transform = new Matrix4d();
        this.transform.setIdentity();
    }
    
    public void lookAt( Point3d camPosition, Point3d pointOfInterest, Vector3d upVector )
    {
        Vector3d x = new Vector3d();
        Vector3d y = new Vector3d();
        Vector3d z = new Vector3d();
        
        z.sub( camPosition, pointOfInterest );
        z.normalize();
        
        x.cross( upVector, z );
        x.normalize();
        
        y.cross( z, x );
   
        this.transform.setColumn( 0, new Vector4d( x ) );
        this.transform.setColumn( 1, new Vector4d( y ) );
        this.transform.setColumn( 2, new Vector4d( z ) );

        this.transform.setColumn( 3, new Vector4d( -camPosition.x, -camPosition.y, -camPosition.z, 1f ) );
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
    
    public double getFoVY()
    {
        return this.fovY;
    }
    
    public void setHeight( double height )
    {
        this.height = height;
    }
    
    public double getHeight()
    {
        return this.height;
    }
    
    public Matrix4d getTransform()
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
            fovY = Double.parseDouble( props.getProperty( fov_y_key ) );

        String height_key = prefix + "height" + suffix;
        if( props.containsKey( height_key ) )
            height = Double.parseDouble( props.getProperty( height_key ) );

        String transform_key = prefix + "transform" + suffix;
        if( props.containsKey( transform_key ) )
            transform = BasicIO.fromMatrix4dString( props.getProperty( transform_key ) );
    }
}
