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
class Ray
{
    public Point3f o;
    public Vector3f d;

    public Ray( Point3f o, Vector3f d )
    {
        this.o = o;
        this.d = d;
    }
}