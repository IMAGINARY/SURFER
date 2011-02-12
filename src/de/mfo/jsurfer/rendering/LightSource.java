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
public class LightSource {

    private Point3f position;
    private Color3f color;
    private float intensity;

    public LightSource() {
        this.position = new Point3f( 0.0f, 0.0f, 0.0f );
        this.color = new Color3f( 1.0f, 1.0f, 1.0f );
        this.intensity = 1.0f;
    }

    public void setPosition(Point3f position)
            throws NullPointerException {
        if (position == null) {
            throw new NullPointerException();
        }
        this.position = position;
    }

    public Point3f getPosition() {
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
}
