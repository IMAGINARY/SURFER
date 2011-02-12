/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class VariableSubstitutor extends AbstractVisitor< PolynomialOperation, Void >
{
    PolynomialOperation xSubstitute;
    PolynomialOperation ySubstitute;
    PolynomialOperation zSubstitute;
    
    public VariableSubstitutor( PolynomialOperation xSubstitute, PolynomialOperation ySubstitute, PolynomialOperation zSubstitute )
    {
        this.xSubstitute = xSubstitute;
        this.ySubstitute = ySubstitute;
        this.zSubstitute = zSubstitute;
    }
    
    public PolynomialOperation visit( PolynomialAddition pa, Void param )
    {
        
        return new PolynomialAddition( pa.firstOperand.accept( this, ( Void ) null ), pa.secondOperand.accept( this, ( Void ) null ) );
    }
    
    public PolynomialOperation visit( PolynomialSubtraction ps, Void param )
    {
        return new PolynomialSubtraction( ps.firstOperand.accept( this, ( Void ) null ), ps.secondOperand.accept( this, ( Void ) null ) );
    }

    public PolynomialOperation visit( PolynomialMultiplication pm, Void param )
    {
        return new PolynomialMultiplication( pm.firstOperand.accept( this, ( Void ) null ), pm.secondOperand.accept( this, ( Void ) null ) );
    }

    public PolynomialOperation visit( PolynomialPower pp, Void param )
    {
        return new PolynomialPower( pp.base.accept( this, ( Void ) null ), pp.exponent );
    }

    public PolynomialOperation visit( PolynomialNegation pn, Void param )
    {
        return new PolynomialNegation( pn.operand.accept( this, ( Void ) null ) );
    }
    
    public PolynomialOperation visit( PolynomialDoubleDivision pdd, Void param )
    {
        return new PolynomialDoubleDivision( pdd.dividend.accept( this, ( Void ) null ), pdd.divisor );
    }
    
    public PolynomialOperation visit( PolynomialVariable pv, Void param )
    {
        switch( pv.variable )
        {
            case x:
                return xSubstitute;
            case y:
                return ySubstitute;
            case z:
                return zSubstitute;
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    public PolynomialOperation visit( DoubleBinaryOperation dbop, Void param )
    {
        return dbop;
    }
    
    public PolynomialOperation visit( DoubleUnaryOperation duop, Void param )
    {
        return duop;
    }
    
    public PolynomialOperation visit( DoubleValue dv, Void param )
    {
        return dv;
    }
    
    public PolynomialOperation visit( DoubleVariable dv, Void param )
    {
        return dv;
    }
}
