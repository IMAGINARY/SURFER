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
public class LightSource {

    public enum Status { ON, OFF }

    private Status status;
    private Point3d position;
    private Color3f color;
    private float intensity;


    public LightSource() {
        this.status = Status.ON;
        this.position = new Point3d( 0.0, 0.0, 0.0 );
        this.color = new Color3f( 1.0f, 1.0f, 1.0f );
        this.intensity = 1.0f;
    }

    public void setStatus( Status status )
    {
        this.status = status;
    }

    public Status getStatus()
    {
        return this.status;
    }

    public void setPosition(Point3d position)
            throws NullPointerException {
        if (position == null) {
            throw new NullPointerException();
        }
        this.position = position;
    }

    public Point3d getPosition() {
        return position;
    }

    public void setColor(Color3f color)
            throws NullPointerException {
        if (color == null) {
            throw new NullPointerException();
        }
        this.color = color;
    }

    public Color3f getColor() {
        return color;
    }    
    
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float getIntensity() {
        return intensity;
    }

    public Properties saveProperties( Properties props, String prefix, String suffix )
    {
        props.setProperty( prefix + "status" + suffix, status.name() );
        props.setProperty( prefix + "position" + suffix, BasicIO.toString( position ) );
        props.setProperty( prefix + "color" + suffix, "" + BasicIO.toString( color ) );
        props.setProperty( prefix + "intensity" + suffix, "" + intensity );
        return props;
    }

    public void loadProperties( Properties props, String prefix, String suffix )
    {
        String status_key = prefix + "status" + suffix;
        if( props.containsKey( status_key ) )
            status = Status.valueOf( props.getProperty( status_key ) );

        String position_key = prefix + "position" + suffix;
        if( props.containsKey( position_key ) )
            position = BasicIO.fromPoint3dString( props.getProperty( position_key ) );

        String color_key = prefix + "color" + suffix;
        if( props.containsKey( color_key ) )
            color = BasicIO.fromColor3fString( props.getProperty( color_key ) );

        String intensity_key = prefix + "intensity" + suffix;
        if( props.containsKey( intensity_key ) )
            intensity = Float.parseFloat( props.getProperty( intensity_key ) );
    }
}
