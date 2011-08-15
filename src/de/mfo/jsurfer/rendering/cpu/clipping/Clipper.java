/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.rendering.cpu.clipping;

import java.util.List;
import java.util.LinkedList;
import javax.vecmath.*;
import de.mfo.jsurfer.rendering.*;
import de.mfo.jsurfer.rendering.cpu.*;

/**
 *
 * @author stussak
 */
public abstract class Clipper
{
    // returns sorted list of disjoint intervals
    public abstract List< Vector2d > clipRay( Ray r );

    // returns true, if the point is not clipped
    public abstract boolean clipPoint( Point3d p );

    // returns true, if additional point clipping is necessary after ray clipping
    public abstract boolean pointClippingNecessary();

    public List< Vector2d > clipRay( Ray r, Vector2d interval )
    {
        List< Vector2d > intervals = clipRay( r );
        LinkedList< Vector2d > trimmed_intervals = new LinkedList< Vector2d >();
        for( Vector2d i : intervals )
        {
            Vector2d trimmedInterval = intersect( i, interval );
            if( trimmedInterval != null )
                trimmed_intervals.add( trimmedInterval );
        }
        return trimmed_intervals;
    }

    public boolean clipPoint( Point3d p, boolean clippedAgainstRay )
    {
        return clippedAgainstRay && !pointClippingNecessary() ? true : clipPoint( p );
    }
    
    public List< Point3d > clipPoints( List< Point3d > l )
    {
        LinkedList< Point3d > result = new LinkedList< Point3d >();
        for( Point3d p : l )
            if( clipPoint( p ) )
                result.add( p );
        return result;
    }

    public List< Point3d > clipPoints( List< Point3d > l, boolean clippedAgainstRay )
    {
        return clippedAgainstRay && !pointClippingNecessary() ? l : clipPoints( l );
    }

    // set intersection of both intervals, i1 will be overwritten
    // return null, if intervals are disjoint
    static Vector2d intersect( Vector2d i1, Vector2d i2 )
    {
        i1.x = Math.max( i1.x, i2.x );
        i1.y = Math.min( i1.y, i2.y );
        return i1.y < i1.x ? null : i1;
    }

}
