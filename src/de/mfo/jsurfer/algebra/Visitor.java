/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public interface Visitor< RETURN_TYPE, PARAM_TYPE >
{
    public RETURN_TYPE visit( PolynomialOperation pop, PARAM_TYPE param );
    public RETURN_TYPE visit( PolynomialAddition pa, PARAM_TYPE param );
    public RETURN_TYPE visit( PolynomialSubtraction ps, PARAM_TYPE param );
    public RETURN_TYPE visit( PolynomialMultiplication pm, PARAM_TYPE param );
    public RETURN_TYPE visit( PolynomialPower pp, PARAM_TYPE param );
    public RETURN_TYPE visit( PolynomialNegation pn, PARAM_TYPE param );
    public RETURN_TYPE visit( PolynomialDoubleDivision pdd, PARAM_TYPE param );
    public RETURN_TYPE visit( PolynomialVariable pv, PARAM_TYPE param );
    
    public RETURN_TYPE visit( DoubleOperation dop, PARAM_TYPE param );
    public RETURN_TYPE visit( DoubleBinaryOperation dbop, PARAM_TYPE param );
    public RETURN_TYPE visit( DoubleUnaryOperation duop, PARAM_TYPE param );
    public RETURN_TYPE visit( DoubleValue dv, PARAM_TYPE param );
    public RETURN_TYPE visit( DoubleVariable dv, PARAM_TYPE param );
}
