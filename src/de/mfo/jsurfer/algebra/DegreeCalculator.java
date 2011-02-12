/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class DegreeCalculator extends AbstractVisitor< Integer, Void >
{
    public Integer visit( PolynomialAddition pa, Void param )
    {
        return Math.max( pa.firstOperand.accept( this, ( Void ) null ), pa.secondOperand.accept( this, ( Void ) null ) );
    }

    public Integer visit( PolynomialSubtraction ps, Void param )
    {
        return Math.max( ps.firstOperand.accept( this, ( Void ) null ), ps.secondOperand.accept( this, ( Void ) null ) );
    }
    
    public Integer visit( PolynomialMultiplication pm, Void param )
    {
        return pm.firstOperand.accept( this, ( Void ) null ) + pm.secondOperand.accept( this, ( Void ) null );
    }

    public Integer visit( PolynomialPower pp, Void param )
    {
        return pp.exponent * pp.base.accept( this, ( Void ) null );
    }
        
    public Integer visit( PolynomialNegation pn, Void param )
    {
        return pn.operand.accept( this, ( Void ) null );
    }

    public Integer visit( PolynomialDoubleDivision pdd, Void param )
    {
        return pdd.dividend.accept( this, ( Void ) null );
    }
    
    public Integer visit( PolynomialVariable pv, Void param )
    {
        return 1;
    }    
    
    public Integer visit( DoubleBinaryOperation dbop, Void param )
    {
        return 0;
    }
    
    public Integer visit( DoubleUnaryOperation duop, Void param )
    {
        return 0;
    }

    public Integer visit( DoubleValue dv, Void param )
    {
        return 0;
    }

    public Integer visit( DoubleVariable dv, Void param )
    {
        return 0;
    }
}
