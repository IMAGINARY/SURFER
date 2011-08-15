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
public class Simplificator extends AbstractVisitor< PolynomialOperation, Void >
{
    private Map< String, java.lang.Double > dict;

    public Simplificator()
    {
        this.dict = new HashMap< String, java.lang.Double >();
    }

    public double getParameterValue( String name )
    {
        return this.dict.get( name );
    }

    public Set< String > getKnownParameterNames()
    {
        return this.dict.keySet();
    }

    public Set< Map.Entry< String, java.lang.Double > > getKnownParameters()
    {
        return this.dict.entrySet();
    }

    public void setParameterValue( String name, double value )
    {
        this.dict.put( name, value );
    }

    public void unsetParameterValue( String name )
    {
        this.dict.remove(name);
    }

    public PolynomialOperation visit( PolynomialAddition pa, Void param )
    {
        PolynomialOperation firstOperand = pa.firstOperand.accept( this, ( Void ) null );
        PolynomialOperation secondOperand = pa.secondOperand.accept( this, ( Void ) null );

        try
        {
            if( ( ( DoubleValue ) firstOperand ).value == 0.0 )
                return secondOperand;
        }
        catch( ClassCastException cce )
        {
        }
        try
        {
            if( ( ( DoubleValue ) secondOperand ).value == 0.0 )
                return firstOperand;
        }
        catch( ClassCastException cce )
        {
        }
        try
        {
            return new DoubleBinaryOperation( DoubleBinaryOperation.Op.add, ( DoubleOperation ) firstOperand, ( DoubleOperation ) secondOperand );
        }
        catch( ClassCastException cce )
        {
        }
        return new PolynomialAddition( firstOperand, secondOperand );
    }

    public PolynomialOperation visit( PolynomialSubtraction ps, Void param )
    {
        PolynomialOperation firstOperand = ps.firstOperand.accept( this, ( Void ) null );
        PolynomialOperation secondOperand = ps.secondOperand.accept( this, ( Void ) null );

        try
        {
            if( ( ( DoubleValue ) firstOperand ).value == 0.0 )
                return new PolynomialNegation( secondOperand ).accept( this, ( Void ) null );
        }
        catch( ClassCastException cce )
        {
        }
        try
        {
            if( ( ( DoubleValue ) secondOperand ).value == 0.0 )
                return firstOperand;
        }
        catch( ClassCastException cce )
        {
        }
        try
        {
            return new DoubleBinaryOperation( DoubleBinaryOperation.Op.sub, ( DoubleOperation ) firstOperand, ( DoubleOperation ) secondOperand );
        }
        catch( ClassCastException cce )
        {
        }        
        return new PolynomialSubtraction( firstOperand, secondOperand );
    }

    public PolynomialOperation visit( PolynomialMultiplication pm, Void param )
    {
        PolynomialOperation firstOperand = pm.firstOperand.accept( this, ( Void ) null );
        PolynomialOperation secondOperand = pm.secondOperand.accept( this, ( Void ) null );
        
        try
        {
            if( ( ( DoubleValue ) firstOperand ).value == 0.0 )
                return firstOperand;
            else if( ( ( DoubleValue ) firstOperand ).value == 1.0 )
                return secondOperand;
        }
        catch( ClassCastException cce )
        {
        }
        try
        {
            if( ( ( DoubleValue ) secondOperand ).value == 0.0 )
                return secondOperand;
            else if( ( ( DoubleValue ) secondOperand ).value == 1.0 )
                return firstOperand;
        }
        catch( ClassCastException cce )
        {
        }
        try
        {
            return new DoubleBinaryOperation( DoubleBinaryOperation.Op.mult, ( DoubleOperation ) firstOperand, ( DoubleOperation ) secondOperand );
        }
        catch( ClassCastException cce )
        {
        }
        return new PolynomialMultiplication( firstOperand, secondOperand );
    }

    public PolynomialOperation visit( PolynomialPower pp, Void param )
    {
        PolynomialOperation base = pp.base.accept( this, ( Void ) null );
        if( pp.exponent == 0 )
        {
            return new DoubleValue( 1.0 );
        }
        else if( pp.exponent == 1 )
        {
            return base;
        }
        else
        {
        }
        try
        {
            double dBase = ( ( DoubleValue ) base ).value;
            return new DoubleValue( Math.pow( dBase, pp.exponent ) );
        }
        catch( ClassCastException cce )
        {
        }        
        return new PolynomialPower( base, pp.exponent );
    }

    public PolynomialOperation visit( PolynomialNegation pn, Void param )
    {
        PolynomialOperation operand = pn.operand.accept( this, ( Void ) null );
        try
        {
            return new DoubleValue( -( ( DoubleValue ) operand ).value );
        }
        catch( ClassCastException cce )
        {
            return new PolynomialNegation( operand );
        }
    }

    public PolynomialOperation visit( PolynomialDoubleDivision pdd, Void param )
    {
        PolynomialOperation dividend = pdd.dividend.accept( this, ( Void ) null );
        PolynomialOperation divisor = pdd.divisor.accept( this, ( Void ) null );
        try
        {
            return new DoubleValue( ( ( DoubleValue ) dividend ).value / ( ( DoubleValue ) divisor ).value );
        }
        catch( ClassCastException cce1 )
        {
        }
        try
        {
            return new DoubleBinaryOperation( DoubleBinaryOperation.Op.div, ( DoubleOperation ) dividend, ( DoubleOperation ) divisor );
        }
        catch( ClassCastException cce2 )
        {
            return new PolynomialDoubleDivision( dividend, ( DoubleOperation ) divisor );
        }
    }

    public PolynomialOperation visit( PolynomialVariable pv, Void param )
    {
        return pv;
    }

    public DoubleOperation visit( DoubleBinaryOperation dbop, Void param )
    {
        DoubleOperation firstOperand = ( DoubleOperation ) dbop.firstOperand.accept( this, ( Void ) null );
        DoubleOperation secondOperand = ( DoubleOperation ) dbop.secondOperand.accept( this, ( Void ) null );

        try
        {
            double firstValue = ( ( DoubleValue ) firstOperand ).value;
            double secondValue = ( ( DoubleValue ) secondOperand ).value;
            double result;
            switch( dbop.operator )
            {
                case add:
                    result = firstValue + secondValue;
                    break;
                case sub:
                    result = firstValue - secondValue;
                    break;
                case mult:
                    result = firstValue * secondValue;
                    break;
                case div:
                    result = firstValue / secondValue;
                    break;
                case pow:
                    result = Math.pow( firstValue, secondValue );
                    break;
                case atan2:
                    result = Math.atan2( firstValue, secondValue );
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            return new DoubleValue( result );
        }
        catch( ClassCastException cce )
        {
            return new DoubleBinaryOperation( dbop.operator, firstOperand, secondOperand );
        }
    }

    public DoubleOperation visit( DoubleUnaryOperation duop, Void param )
    {
        DoubleOperation operand = ( DoubleOperation ) duop.operand.accept( this, ( Void ) null );
        try
        {
            double value = ( ( DoubleValue ) operand ).value;
            double result;
            switch( duop.operator )
            {
                case neg:
                    result = -value;
                    break;
                case sin:
                    result = Math.sin( value );
                    break;
                case cos:
                    result = Math.cos( value );
                    break;
                case tan:
                    result = Math.tan( value );
                    break;
                case asin:
                    result = Math.asin( value );
                    break;
                case acos:
                    result = Math.acos( value );
                    break;
                case atan:
                    result = Math.atan( value );
                    break;
                case exp:
                    result = Math.exp( value );
                    break;
                case log:
                    result = Math.log( value );
                    break;
                case sqrt:
                    result = Math.sqrt( value );
                    break;
                case ceil:
                    result = Math.ceil( value );
                    break;
                case floor:
                    result = Math.floor( value );
                    break;
                case abs:
                    result = Math.abs( value );
                    break;
                case sign:
                    result = Math.signum( value );
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            return new DoubleValue( result );
        }
        catch( ClassCastException cce )
        {
            return new DoubleUnaryOperation( duop.operator, operand );
        }
    }

    public DoubleOperation visit( DoubleValue dv, Void param )
    {
        return dv;
    }

    public DoubleOperation visit( DoubleVariable dv, Void param )
    {
        try
        {
            return new DoubleValue( this.dict.get( dv.name ) );
        }
        catch( NullPointerException npe )
        {
            return dv;
        }
    }
}
