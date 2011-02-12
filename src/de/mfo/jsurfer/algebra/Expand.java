/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class Expand extends AbstractVisitor< XYZPolynomial, Void >
{
    private ValueCalculator valueCalculator;
    
    public Expand()
    {
        this.valueCalculator = new ValueCalculator( 0.0, 0.0, 0.0 );
    }
    
    public XYZPolynomial visit( PolynomialAddition pa, Void param )
    {
        XYZPolynomial first = pa.firstOperand.accept( this,( Void ) null );
        XYZPolynomial second = pa.secondOperand.accept( this,( Void ) null );
        
        return first.add( second );
    }
    
    public XYZPolynomial visit( PolynomialSubtraction ps, Void param )
    {
        XYZPolynomial first = ps.firstOperand.accept( this,( Void ) null );
        XYZPolynomial second = ps.secondOperand.accept( this,( Void ) null );
        
        return first.sub( second );
    }
    
    public XYZPolynomial visit( PolynomialMultiplication pm, Void param )
    {
        XYZPolynomial first = pm.firstOperand.accept( this,( Void ) null );
        XYZPolynomial second = pm.secondOperand.accept( this,( Void ) null );
        
        return first.mult( second );
    }
    
    public XYZPolynomial visit( PolynomialPower pp, Void param )
    {
        XYZPolynomial base = pp.base.accept( this,( Void ) null );        
        return base.pow( pp.exponent );
    }
    
    public XYZPolynomial visit( PolynomialNegation pn, Void param )
    {
        return pn.operand.accept( this,( Void ) null ).neg();
    }
    
    public XYZPolynomial visit( PolynomialDoubleDivision pdd, Void param )
    {
        XYZPolynomial dividend = pdd.dividend.accept( this,( Void ) null );
        double divisor = pdd.divisor.accept( this.valueCalculator, ( Void ) null );

        return dividend.mult( divisor );
    }
    
    public XYZPolynomial visit( PolynomialVariable pv, Void param )
    {
        switch( pv.variable )
        {
            case x:
                return new XYZPolynomial( new XYZPolynomial.Term( 1.0, 1, 0, 0 ) );
            case y:
                return new XYZPolynomial( new XYZPolynomial.Term( 1.0, 0, 1, 0 ) );
            case z:
                return new XYZPolynomial( new XYZPolynomial.Term( 1.0, 0, 0, 1 ) );
            default:
                throw new UnsupportedOperationException();                
        }
    }
    
    public XYZPolynomial visit( DoubleBinaryOperation dbop, Void param )
    {
        return new XYZPolynomial( dbop.accept( this.valueCalculator, ( Void ) null ) );
    }
    
    public XYZPolynomial visit( DoubleUnaryOperation duop, Void param )
    {
        return new XYZPolynomial( duop.accept( this.valueCalculator, ( Void ) null ) );
    }

    public XYZPolynomial visit( DoubleVariable dv, Void param )
    {
        throw new UnsupportedOperationException();
    }
    
    public XYZPolynomial visit( DoubleValue dv, Void param )
    {
        return new XYZPolynomial( dv.value );
    }
}
