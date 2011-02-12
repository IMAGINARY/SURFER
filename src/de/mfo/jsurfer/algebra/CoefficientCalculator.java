/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 * Calculates coefficients of polynomial, that results from substituting (x,y,z)
 * with the components of a ray: (o.x+t*d.x,o.y+t*d.y,o.z+t*d.z).
 * @author Christian Stussak <christian at knorf.de>
 */
public interface CoefficientCalculator
{
    public UnivariatePolynomial calculateCoefficients( UnivariatePolynomial x, UnivariatePolynomial y, UnivariatePolynomial z );
}