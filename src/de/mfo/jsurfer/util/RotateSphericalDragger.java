/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.util;

import java.awt.Point;
import javax.vecmath.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class RotateSphericalDragger
{
    Point lastLocation;
    Matrix4f rotation;
    float xSpeed;
    float ySpeed;
    
    public RotateSphericalDragger()
    {
        this( 1f, 1f );
    }
    
    public RotateSphericalDragger( float xSpeed, float ySpeed )
    {
        lastLocation = new Point();
        rotation = new Matrix4f();
        rotation.setIdentity();
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }
    
    public void startDrag( Point p )
    {
        lastLocation = new Point( p );
    }
    
    public void dragTo( Point p )
    {
        float xAngle = -( lastLocation.x - p.x ) * xSpeed;
        float yAngle = ( lastLocation.y - p.y ) * ySpeed;
        
        Matrix4f rotX = new Matrix4f();
        rotX.setIdentity();
        rotX.rotX( ( float ) ( Math.PI / 180.0 ) * yAngle );

        Matrix4f rotY = new Matrix4f();
        rotY.setIdentity();
        rotY.rotY( ( float ) ( Math.PI / 180.0 ) * xAngle );
        
        rotation.mul( rotX );
        rotation.mul( rotY );
        
        lastLocation = new Point( p );
    }
    
    public Matrix4f getRotation()
    {
        return new Matrix4f( rotation );
    }
    
    public void setRotation( Matrix4f m )
    {
        rotation = new Matrix4f( m );
    }
    
    public float getXSpeed()
    {
        return xSpeed;
    }
    
    public void setXSpeed( float xSpeed )
    {
        this.xSpeed = xSpeed;
    }
    
    public float getYSpeed()
    {
        return ySpeed;
    }
    
    public void setYSpeed( float ySpeed )
    {
        this.ySpeed = ySpeed;
    }
}
