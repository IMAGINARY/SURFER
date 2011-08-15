/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.rendering.cpu;

import javax.vecmath.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class Ray
{
    public Point3d o;
    public Vector3d d;

    public Ray( Point3d o, Vector3d d )
    {
        this.o = o;
        this.d = d;
    }

    public Point3d at( double t )
    {
        return Helper.interpolate1D( o, d, t );
    }

    public String toString()
    {
        return o.toString() + "+t*" + d.toString();
    }
}