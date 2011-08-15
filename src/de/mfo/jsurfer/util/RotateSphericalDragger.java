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
    Matrix4d rotation;
    double xSpeed;
    double ySpeed;
    
    public RotateSphericalDragger()
    {
        this( 1, 1 );
    }
    
    public RotateSphericalDragger( double xSpeed, double ySpeed )
    {
        lastLocation = new Point();
        rotation = new Matrix4d();
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
        double xAngle = -( lastLocation.x - p.x ) * xSpeed;
        double yAngle = ( lastLocation.y - p.y ) * ySpeed;
        
        Matrix4d rotX = new Matrix4d();
        rotX.setIdentity();
        rotX.rotX( ( Math.PI / 180.0 ) * yAngle );

        Matrix4d rotY = new Matrix4d();
        rotY.setIdentity();
        rotY.rotY( ( Math.PI / 180.0 ) * xAngle );
        
        rotation.mul( rotX );
        rotation.mul( rotY );
        
        lastLocation = new Point( p );
    }
    
    public Matrix4d getRotation()
    {
        return new Matrix4d( rotation );
    }
    
    public void setRotation( Matrix4d m )
    {
        rotation = new Matrix4d( m );
    }
    
    public double getXSpeed()
    {
        return xSpeed;
    }
    
    public void setXSpeed( double xSpeed )
    {
        this.xSpeed = xSpeed;
    }
    
    public double getYSpeed()
    {
        return ySpeed;
    }
    
    public void setYSpeed( double ySpeed )
    {
        this.ySpeed = ySpeed;
    }
}
