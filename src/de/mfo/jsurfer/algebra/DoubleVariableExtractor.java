package de.mfo.jsurfer.algebra;

import java.util.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class DoubleVariableExtractor extends AbstractVisitor< Set< String >, Void >
{
    public Set< String > visit( PolynomialAddition pa, Void param )
    {
        Set< String > s = pa.firstOperand.accept( this, ( Void ) null );
        s.addAll( pa.secondOperand.accept( this, ( Void ) null ) );
        return s;
    }

    public Set< String > visit( PolynomialSubtraction ps, Void param )
    {
        Set< String > s = ps.firstOperand.accept( this, ( Void ) null );
        s.addAll( ps.secondOperand.accept( this, ( Void ) null ) );
        return s;
    }

    public Set< String > visit( PolynomialMultiplication pm, Void param )
    {
        Set< String > s = pm.firstOperand.accept( this, ( Void ) null );
        s.addAll( pm.secondOperand.accept( this, ( Void ) null ) );
        return s;
    }

    public Set< String > visit( PolynomialPower pp, Void param )
    {
        return pp.base.accept( this, ( Void ) null );
    }

    public Set< String > visit( PolynomialNegation pn, Void param )
    {
        return pn.operand.accept( this, ( Void ) null );
    }

    public Set< String > visit( PolynomialDoubleDivision pdd, Void param )
    {
        Set< String > s = pdd.dividend.accept( this, ( Void ) null );
        s.addAll( pdd.divisor.accept( this, ( Void ) null ) );
        return s;
    }

    public Set< String > visit( PolynomialVariable pv, Void param )
    {
        return new HashSet< String >();
    }

    public Set< String > visit( DoubleBinaryOperation dbop, Void param )
    {
        Set< String > s = dbop.firstOperand.accept( this, ( Void ) null );
        s.addAll( dbop.secondOperand.accept( this, ( Void ) null ) );
        return s;
    }

    public Set< String > visit( DoubleUnaryOperation duop, Void param )
    {
        return duop.operand.accept( this, ( Void ) null );
    }

    public Set< String > visit( DoubleValue dv, Void param )
    {
        return new HashSet< String >();
    }

    public Set< String > visit( DoubleVariable dv, Void param )
    {
        HashSet< String > s = new HashSet< String >();
        s.add( dv.name );
        return s;
    }
}