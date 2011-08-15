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
public class TransformedPolynomialRowSubstitutor implements RowSubstitutor {
    private XYZPolynomial tuvPolynomial;
    
    public TransformedPolynomialRowSubstitutor( PolynomialOperation po, PolynomialOperation rayXComponent, PolynomialOperation rayYComponent, PolynomialOperation rayZComponent )
    {
        ToStringVisitor tsv = new ToStringVisitor();
        //System.out.println();
        
        //System.out.println( "x=" + rayXComponent.accept( tsv, null ) );
        //System.out.println( "y=" + rayYComponent.accept( tsv, null ) );
        //System.out.println( "z=" + rayZComponent.accept( tsv, null ) );
        

        Expand expand = new Expand();
        XYZPolynomial p = po.accept( expand, ( Void ) null );
        //System.out.println( "p=" + p );

        XYZPolynomial x = rayXComponent.accept( expand, ( Void ) null );
        XYZPolynomial y = rayYComponent.accept( expand, ( Void ) null );
        XYZPolynomial z = rayZComponent.accept( expand, ( Void ) null );
        tuvPolynomial = p.substitute( x, y, z );
        //System.out.println( tuvPolynomial );
    }

    public TransformedPolynomialRowSubstitutor( XYZPolynomial p, XYZPolynomial rayXComponent, XYZPolynomial rayYComponent, XYZPolynomial rayZComponent )
    {
        tuvPolynomial = p.substitute( rayXComponent, rayYComponent, rayZComponent );
    }
    
    private static class myColumnSubstitutor implements ColumnSubstitutor
    {
        private XYPolynomial tuPolynomial;
        private double v;
        
        public myColumnSubstitutor( XYZPolynomial tvuPolynomial, double v )
        {
            this.v = v;
            this.tuPolynomial = tvuPolynomial.evaluateZ( v );
            //System.out.println( "coeffs at v=" + v + ":" + this.tuPolynomial );
        }
        
        public UnivariatePolynomial setU( double u )
        {
            //System.out.println( "coeffs at v=" +v+",u=" + u+  ":" + this.tuPolynomial.evaluateY( u ) );
            return this.tuPolynomial.evaluateY( u );
        }
    }
    
    public ColumnSubstitutor setV( double v )
    {
        return new myColumnSubstitutor( tuvPolynomial, v );
    }
}