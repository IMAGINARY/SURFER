/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class PolynomialDoubleDivision implements PolynomialOperation
{
    public PolynomialOperation dividend;
    public DoubleOperation divisor;
    
    public PolynomialDoubleDivision( PolynomialOperation dividend, DoubleOperation divisor )
    {
        this.dividend = dividend;
        this.divisor = divisor;
    }
    
    public < RETURN_TYPE, PARAM_TYPE > RETURN_TYPE accept( Visitor< RETURN_TYPE, PARAM_TYPE > visitor, PARAM_TYPE arg )
    {
        return visitor.visit( this, arg );
    }
}