/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class PolynomialVariable implements PolynomialOperation
{
    public enum Var { x, y, z; }
    public Var variable;
    
    public PolynomialVariable( Var variable )
    {
        this.variable = variable;
    }
    
    public < RETURN_TYPE, PARAM_TYPE > RETURN_TYPE accept( Visitor< RETURN_TYPE, PARAM_TYPE > visitor, PARAM_TYPE arg )
    {
        return visitor.visit( this, arg );
    }
}