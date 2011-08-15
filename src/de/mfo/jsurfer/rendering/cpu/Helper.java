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
final class Helper
{
    static Point3d interpolate1D( Point3d p, Vector3d d, double t )
    {
        return new Point3d( p.x + d.x * t, p.y + d.y * t, p.z + d.z * t );
    }

    static Point3d interpolate2D( Point3d p, Vector3d dx, Vector3d dy, double u, double v )
    {
        return new Point3d( p.x + dx.x * u + dy.x * v, p.y + dx.y * u + dy.y * v, p.z + dx.z * u + dy.z * v );
    }
}
