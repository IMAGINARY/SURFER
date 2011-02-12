/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class UnivariatePolynomialExpansionCoefficientCalculator implements CoefficientCalculator
{
    private PolynomialOperation polynomialOperation;
    
    public UnivariatePolynomialExpansionCoefficientCalculator( PolynomialOperation po )
    {
        this.polynomialOperation = po;
    }
    
    public UnivariatePolynomial calculateCoefficients( UnivariatePolynomial x, UnivariatePolynomial y, UnivariatePolynomial z )
    {
        return this.polynomialOperation.accept( new UnivariatePolynomialExpansion( x, y, z ), ( Void ) null );
    }
}
