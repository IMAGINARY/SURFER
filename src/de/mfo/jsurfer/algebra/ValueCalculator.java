/*
 * To change this template: case choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

import java.util.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class ValueCalculator extends AbstractVisitor< Double, Void >
{
    private double x;
    private double y;
    private double z;
    
    private Map< String, Double > dict;
    
    public ValueCalculator()
    {
        this( 0.0, 0.0, 0.0 );
    }
    
    public ValueCalculator( double x, double y, double z )
    {
        this.x = x;
        this.y = y;
        this.z = z;
        
        this.dict = new HashMap< String, Double >();
    }
    
    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }
    
    public double getZ()
    {
        return this.z;
    }   
    
    public void setX( double x )
    {
        this.x = x;
    }
    
    public void setY( double y )
    {
        this.y = y;
    }
    
    public void setZ( double z )
    {
        this.z = z;
    }
    
    public void setXYZ( double x, double y, double z )
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public double getParameterValue( String name )
    {
        try
        {
            return this.dict.get( name );    
        }
        catch( NullPointerException npe )
        {
            return Double.NaN;
        }
    }

    public Set< String > getParameters()
    {
        return this.dict.keySet();
    }
    
    public void setParameterValue( String name, double value )
    {
        this.dict.put( name, value );
    }
    
    public Double visit( PolynomialAddition pa, Void param )
    {
        return pa.firstOperand.accept( this, ( Void ) null ) + pa.secondOperand.accept( this, ( Void ) null );
    }
            
    public Double visit( PolynomialSubtraction ps, Void param )
    {
        return ps.firstOperand.accept( this, ( Void ) null ) - ps.secondOperand.accept( this, ( Void ) null );
    }

    public Double visit( PolynomialMultiplication pm, Void param )
    {
        return pm.firstOperand.accept( this, ( Void ) null ) * pm.secondOperand.accept( this, ( Void ) null );
    }

    public Double visit( PolynomialPower pp, Void param )
    {
        return Math.pow( pp.base.accept( this, ( Void ) null ), ( double ) pp.exponent );
    }

    public Double visit( PolynomialNegation pn, Void param )
    {
        return -pn.operand.accept( this, ( Void ) null );
    }    
    
    public Double visit( PolynomialDoubleDivision pdd, Void param )
    {
        return pdd.dividend.accept( this,( Void ) null ) / pdd.divisor.accept( this,( Void ) null );
    }

    public Double visit( PolynomialVariable pv, Void param )
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
    
    public Double visit( DoubleBinaryOperation dbop, Void param )
    {
        double firstOperand = dbop.firstOperand.accept( this, ( Void ) null );
        double secondOperand = dbop.secondOperand.accept( this, ( Void ) null );
        
        switch( dbop.operator )
        {
            case add:
                return firstOperand + secondOperand;
            case sub:
                return firstOperand - secondOperand;
            case mult:
                return firstOperand * secondOperand;
            case div:
                return firstOperand / secondOperand;
            case pow:
                return Math.pow( firstOperand, secondOperand );
            case atan2:
                return Math.atan2( firstOperand, secondOperand );
            default:
                throw new UnsupportedOperationException();
        }
    }

    public Double visit( DoubleUnaryOperation duop, Void param )
    {
        double operand = duop.operand.accept( this, ( Void ) null );
        
        switch( duop.operator )
        {
            case neg:
                return -operand;
            case sin:
                return Math.sin( operand );
            case cos:
                return Math.cos( operand );
            case tan:
                return Math.tan( operand );
            case asin:
                return Math.asin( operand );
            case acos:
                return Math.acos( operand );
            case atan:
                return Math.atan( operand );
            case exp:
                return Math.exp( operand );
            case log:
                return Math.log( operand );
            case sqrt:
                return Math.sqrt( operand );
            case ceil:
                return Math.ceil( operand );
            case floor:
                return Math.floor( operand );
            case abs:
                return Math.abs( operand );
            case sign:
                return Math.signum( operand );
            default:
                throw new UnsupportedOperationException();
        }
    }

    public Double visit( DoubleVariable dv, Void param )
    {
        return this.dict.get( dv.name );
    }
    
    public Double visit( DoubleValue dv, Void param )
    {
        return dv.value;
    }
}