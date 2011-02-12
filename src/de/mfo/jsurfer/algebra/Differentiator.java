/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class Differentiator extends AbstractVisitor< PolynomialOperation, Void >
{
    private PolynomialVariable.Var var;
    
    public Differentiator( PolynomialVariable.Var var )
    {
        this.var = var;
    }
    
    public PolynomialVariable.Var getVariable()
    {
       return this.var;
    }
    
    public void setVariable( PolynomialVariable.Var var )
    {
        this.var = var;
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
        PolynomialOperation u = pm.firstOperand;
        PolynomialOperation v = pm.secondOperand;
        PolynomialOperation udiff = u.accept( this, ( Void ) null );
        PolynomialOperation vdiff = v.accept( this, ( Void ) null );
        
        return new PolynomialAddition( new PolynomialMultiplication( udiff, v ), new PolynomialMultiplication( u, vdiff ) );
    }
    
    public PolynomialOperation visit( PolynomialPower pp, Void param )
    {
        switch( pp.exponent )
        {
            case 1:
                return new DoubleValue( 1.0 );
            case 2:
            {
                PolynomialOperation u = pp.base;
                PolynomialOperation udiff = u.accept( this, ( Void ) null );
                return new PolynomialMultiplication( new PolynomialMultiplication( new DoubleValue( ( double ) pp.exponent ), u ), udiff );
            }
            default:
            {
                PolynomialOperation u = pp.base;
                PolynomialOperation udiff = u.accept( this, ( Void ) null );
                return new PolynomialMultiplication( new PolynomialMultiplication( new DoubleValue( ( double ) pp.exponent ), new PolynomialPower( u, pp.exponent - 1 ) ), udiff );
            }
        }
    }

    public PolynomialOperation visit( PolynomialNegation pn, Void param )
    {
        return new PolynomialNegation( pn.operand.accept( this,( Void ) null ) );
    }
    
    public PolynomialOperation visit( PolynomialDoubleDivision pdd, Void param )
    {
        return new PolynomialDoubleDivision( pdd.dividend.accept( this,( Void ) null ), pdd.divisor );
    }

    public PolynomialOperation visit( PolynomialVariable pv, Void param )
    {
        if( pv.variable == this.var )
            return new DoubleValue( 1.0 );
        else
            return new DoubleValue( 0.0 );
    }
    
    public PolynomialOperation visit( DoubleBinaryOperation dbop, Void param )
    {
        return new DoubleValue( 0.0 );
    }
            
    public PolynomialOperation visit( DoubleUnaryOperation duop, Void param )
    {
        return new DoubleValue( 0.0 );
    }

    public PolynomialOperation visit( DoubleValue dv, Void param )
    {
        return new DoubleValue( 0.0 );
    }

    public PolynomialOperation visit( DoubleVariable dv, Void param )
    {
        return new DoubleValue( 0.0 );
    }
}
