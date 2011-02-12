/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

import javax.vecmath.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public interface GradientCalculator
{
    /**
     * Calculates the surface normal in surface space.
     * @param p Point in surface space.
     * @return Surface normal in surface space.
     */
    public Vector3d calculateGradient( Point3d p );
    
    /**
     * Calculates the surface normal in surface space.
     * @param p Point in surface space.
     * @return Surface normal in surface space.
     */
    public Vector3f calculateGradient( Point3f p );
}
