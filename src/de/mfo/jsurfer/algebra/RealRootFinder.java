/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public interface RealRootFinder
{
    /**
     * Find all real roots of p.
     * @param p
     * @return
     */
    public double[] findAllRoots( UnivariatePolynomial p );
    
    /**
     * Find all real roots of p within lowerBound and upperBound (bounds may or may not be included).
     * @param p
     * @param lowerBound
     * @param upperBound
     * @return
     */
    public double[] findAllRootsIn( UnivariatePolynomial p, double lowerBound, double upperBound );
    
    /**
     * Find the smallest real root of p within lowerBound and upperBound (bounds may or may not be included).
     * If no real root exists in this interval, Double.NaN ist returned.
     * @param p
     * @param lowerBound
     * @param upperBound
     * @return
     */
    public double findFirstRootIn( UnivariatePolynomial p, double lowerBound, double upperBound );
}
