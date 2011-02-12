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
public class RowSubstitutor {
    private XYZPolynomial tuvPolynomial;
    
    public RowSubstitutor( PolynomialOperation po, PolynomialOperation rayXComponent, PolynomialOperation rayYComponent, PolynomialOperation rayZComponent )
    {
        PolynomialOperation substituted = po.accept( new VariableSubstitutor( rayXComponent, rayYComponent, rayZComponent ), ( Void ) null );
        tuvPolynomial = substituted.accept( new Expand(), ( Void ) null );
    }
    
    private static class myColumnSubstitutor implements ColumnSubstitutor
    {
        private XYPolynomial tuPolynomial;
        private double v;
        
        public myColumnSubstitutor( XYZPolynomial tvuPolynomial, double v )
        {
            this.v = v;
            this.tuPolynomial = tvuPolynomial.substituteZ( v );
        }
        
        public UnivariatePolynomial setU( double u )
        {
            return this.tuPolynomial.substituteY( u );
        }
        
        public double getV()
        {
            return this.v;
        }
    }
    
    public ColumnSubstitutor setV( double v )
    {
        return new myColumnSubstitutor( tuvPolynomial, v );
    }
}