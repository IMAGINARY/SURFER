/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

import java.util.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class PolynomialExpansion extends AbstractVisitor< UnivariatePolynomial, Void >
{
    private UnivariatePolynomial x;
    private UnivariatePolynomial y;
    private UnivariatePolynomial z;
    
    private ValueCalculator valueCalculator;
    
    public PolynomialExpansion( UnivariatePolynomial x, UnivariatePolynomial y, UnivariatePolynomial z )
    {
        this.x = x;
        this.y = y;
        this.z = z;
        
        this.valueCalculator = new ValueCalculator( 0.0, 0.0, 0.0 );
    }
    
    public UnivariatePolynomial getX()
    {
        return this.x;
    }

    public UnivariatePolynomial getY()
    {
        return this.y;
    }
    
    public UnivariatePolynomial getZ()
    {
        return this.z;
    }   
    
    public void setX( UnivariatePolynomial x )
    {
        this.x = x;
    }
    
    public void setY( UnivariatePolynomial y )
    {
        this.y = y;
    }
    
    public void setZ( UnivariatePolynomial z )
    {
        this.z = z;
    }
    
    public void setXYZ( UnivariatePolynomial x, UnivariatePolynomial y, UnivariatePolynomial z )
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public UnivariatePolynomial visit( PolynomialAddition pa, Void param )
    {
        return pa.firstOperand.accept( this, ( Void ) null ).add( pa.secondOperand.accept( this, ( Void ) null ) );
    }
    
    public UnivariatePolynomial visit( PolynomialSubtraction ps, Void param )
    {
        return ps.firstOperand.accept( this, ( Void ) null ).sub( ps.secondOperand.accept( this, ( Void ) null ) );
    }
    
    public UnivariatePolynomial visit( PolynomialMultiplication pm, Void param )
    {
        return pm.firstOperand.accept( this, ( Void ) null ).mult( pm.secondOperand.accept( this, ( Void ) null ) );
    }
    
    public UnivariatePolynomial visit( PolynomialPower pp, Void param )
    {
        return pp.base.accept( this, ( Void ) null ).pow( pp.exponent );
    }

    public UnivariatePolynomial visit( PolynomialNegation pn, Void param )
    {
        return pn.operand.accept( this,( Void ) null ).neg();
    }

    public UnivariatePolynomial visit( PolynomialDoubleDivision pdd, Void param )
    {
        return pdd.dividend.accept( this,( Void ) null ).div( pdd.divisor.accept( this.valueCalculator, ( Void ) null ) );
    }
    
    public UnivariatePolynomial visit( PolynomialVariable pv, Void param )
    {
        switch( pv.variable )
        {
            case x:
                return this.x;
            case y:
                return this.y;
            case z:
                return this.z;
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    public UnivariatePolynomial visit( DoubleBinaryOperation dbop, Void param )
    {
        return new UnivariatePolynomial( dbop.accept( this.valueCalculator, ( Void ) null ) );
    }
    
    public UnivariatePolynomial visit( DoubleUnaryOperation duop, Void param )
    {
        return new UnivariatePolynomial( duop.accept( this.valueCalculator, ( Void ) null ) );
    }

    public UnivariatePolynomial visit( DoubleVariable dv, Void param )
    {
        throw new UnsupportedOperationException();
    }
    
    public UnivariatePolynomial visit( DoubleValue dv, Void param )
    {
        return new UnivariatePolynomial( dv.value );
    }
}
