/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class PolynomialPower implements PolynomialOperation
{
    public PolynomialOperation base;
    public int exponent;
    
    public PolynomialPower( PolynomialOperation base, int exponent )
    {
        this.base = base;
        this.exponent = exponent;
    }
    
    public < RETURN_TYPE, PARAM_TYPE > RETURN_TYPE accept( Visitor< RETURN_TYPE, PARAM_TYPE > visitor, PARAM_TYPE arg )
    {
        return visitor.visit( this, arg );
    }
}