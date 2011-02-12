/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class ToStringVisitor extends AbstractVisitor< String, Void >
{
    public String visit( PolynomialOperation pop, Void param )
    {
        return "(" + pop.toString() + ")";
    }
    
    public String visit( PolynomialAddition pa, Void param )
    {
        return "(" + pa.firstOperand.accept( this, ( Void ) null ) + "+" + pa.secondOperand.accept( this, ( Void ) null ) + ")";
    }
    
    public String visit( PolynomialSubtraction ps, Void param )
    {
        return "(" + ps.firstOperand.accept( this, ( Void ) null ) + "-" + ps.secondOperand.accept( this, ( Void ) null ) + ")";
    }
    
    public String visit( PolynomialMultiplication pm, Void param )
    {
        return "(" + pm.firstOperand.accept( this, ( Void ) null ) + "*" + pm.secondOperand.accept( this, ( Void ) null ) + ")";
    }

    public String visit( PolynomialPower pp, Void param )
    {
        return "(" + pp.base.accept( this, ( Void ) null ) + "^" + pp.exponent + ")";
    }

    public String visit( PolynomialNegation pn, Void param )
    {
        return "(" + pn.operand.accept( this, ( Void ) null ) + ")";
    }
    
    public String visit( PolynomialDoubleDivision pdd, Void param )
    {
        return "( " + pdd.dividend.accept( this,( Void ) null ) + "/" + pdd.divisor.accept( this,( Void ) null ) + ")";
    }
    
    public String visit( PolynomialVariable pv, Void param )
    {
        switch( pv.variable )
        {
            case x:
                return "x";
            case y:
                return "y";
            case z:
                return "z";
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    public String visit( DoubleOperation dop, Void param )
    {
        return "(" + dop.toString() + ")";
    }
    
    public String visit( DoubleBinaryOperation dbop, Void param )
    {
        String firstOperand = dbop.firstOperand.accept( this, ( Void ) null );
        String secondOperand = dbop.secondOperand.accept( this, ( Void ) null );
        
        switch( dbop.operator )
        {
            case add:
                return "(" + firstOperand + "+" + secondOperand + ")";
            case sub:
                return "(" + firstOperand + "-" + secondOperand + ")";
            case mult:
                return "(" + firstOperand + "*" + secondOperand + ")";
            case div:
                return "(" + firstOperand + "/" + secondOperand + ")";
            case pow:
                return "(" + firstOperand + "^" + secondOperand + ")";
            case atan2:
                return "atan2(" + firstOperand + ", " + secondOperand + ")";
            default:
                throw new UnsupportedOperationException();
        }        
    }
    
    public String visit( DoubleUnaryOperation duop, Void param )
    {
        String operand = duop.operand.accept( this, ( Void ) null );
        
        switch( duop.operator )
        {
            case neg:
                return "(-" + operand + ")";
            case sin:
                return "sin(" + operand + ")";
            case cos:
                return "cos(" + operand + ")";
            case tan:
                return "tan(" + operand + ")";
            case asin:
                return "asin(" + operand + ")";
            case acos:
                return "acos(" + operand + ")";
            case atan:
                return "atan(" + operand + ")";
            case exp:
                return "exp(" + operand + ")";
            case log:
                return "log(" + operand + ")";
            case sqrt:
                return "sqrt(" + operand + ")";
            case ceil:
                return "ceil(" + operand + ")";
            case floor:
                return "floor(" + operand + ")";
            case abs:
                return "abs(" + operand + ")";
            case sign:
                return "signum(" + operand + ")";
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    public String visit( DoubleValue dv, Void param )
    {
        return "" + dv.value;
    }
    
    public String visit( DoubleVariable dv, Void param )
    {
        return dv.name;
    }
}
