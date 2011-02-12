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
    static Point3f interpolate1D( Point3f p, Vector3f d, float t )
    {
        return new Point3f( p.x + d.x * t, p.y + d.y * t, p.z + d.z * t );
    }

    static Point3f interpolate2D( Point3f p, Vector3f dx, Vector3f dy, float u, float v )
    {
        return new Point3f( p.x + dx.x * u + dy.x * v, p.y + dx.y * u + dy.y * v, p.z + dx.z * u + dy.z * v );
    }
}
