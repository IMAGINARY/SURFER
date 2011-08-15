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
public class XYPolynomial
{
    public static final XYPolynomial X;
    public static final XYPolynomial Y;

    static
    {
        X = new XYPolynomial( 1.0, (byte) 1, (byte) 0 );
        Y = new XYPolynomial( 1.0, (byte) 0, (byte) 1 );
    }

    //private Term[] terms; // ordered list of terms, such that terms[ i ].lexCompare( terms[ i + 1 ] ) < 0
    private double[] coeffs;
    private byte[] xExps;
    private byte[] yExps;

    private byte xDegree;
    private byte yDegree;
    private byte degree;

    static byte[] byteArray( byte ... b ) { return b; }
    static double[] doubleArray( double ... d ) { return d; }


    public XYPolynomial()
    {
        this( 0.0 );
    }

    public XYPolynomial( double value )
    {
        coeffs = doubleArray( value );
        xExps = byteArray( (byte) 0 );
        yExps = byteArray( (byte) 0 );
        calculateDegree();
    }

    public XYPolynomial( double value, byte xExp, byte yExp )
    {
        coeffs = doubleArray( value );
        xExps = byteArray( xExp );
        yExps = byteArray( yExp );
        calculateDegree();
    }

    XYPolynomial( double[] coeffs, byte[] xExps, byte[] yExps, int from, int length )
    {
        createArrays( length );
        System.arraycopy( coeffs, from, this.coeffs, 0, length );
        System.arraycopy( xExps, from, this.xExps, 0, length );
        System.arraycopy( yExps, from, this.yExps, 0, length );
        calculateDegree();
    }

    XYPolynomial( double[] coeffs, byte[] xExps, byte[] yExps, boolean copy )
    {
        if( copy )
        {
            createArrays( coeffs.length );
            System.arraycopy( coeffs, 0, this.coeffs, 0, coeffs.length );
            System.arraycopy( xExps, 0, this.xExps, 0, coeffs.length );
            System.arraycopy( yExps, 0, this.yExps, 0, coeffs.length );
        }
        else
        {
            this.coeffs = coeffs;
            this.xExps = xExps;
            this.yExps = yExps;
        }
        calculateDegree();
    }

    XYPolynomial( double[] coeffs, byte[] xExps, byte[] yExps )
    {
        this( coeffs, xExps, yExps, true );
    }

    XYPolynomial( int length )
    {
        createArrays( length );
    }

    private void createArrays( int length )
    {
        this.coeffs = new double[ length ];
        this.xExps = new byte[ length ];
        this.yExps = new byte[ length ];
    }

    private void calculateDegree()
    {
        xDegree = xExps[ xExps.length - 1 ];
        yDegree = 0;
        int yDegreePos = this.coeffs.length - 1;
        degree = 0;

        for( int i = this.coeffs.length - 1; i >= 0; --i )
        {
            if( yExps[ i ] > yDegree )
            {
                yDegree = yExps[ i ];
                yDegreePos = i;
            }
        }
        degree = ( byte ) Math.max( xExps[ yDegreePos ] + yDegree, xDegree + yExps[ xExps.length - 1 ] );
    }

    public static int lexCompare( XYPolynomial p1, XYPolynomial p2, int pos1, int pos2 )
    {
        int result = p1.xExps[ pos1 ] < p2.xExps[ pos2 ] ? -4 : ( p1.xExps[ pos1 ] > p2.xExps[ pos2 ] ? 4 : 0 );
        if( result == 0 )
            result += p1.yExps[ pos1 ] < p2.yExps[ pos2 ] ? -2 : ( p1.yExps[ pos1 ] > p2.yExps[ pos2 ] ? 2 : 0 );
        return result;
    }

    public XYPolynomial neg()
    {
        double[] negCoeffs = new double[ this.coeffs.length ];
        for( int i = 0; i < this.coeffs.length; ++i )
            negCoeffs[ i ] = -coeffs[ i ];
        return new XYPolynomial( negCoeffs, xExps, yExps, false );
    }

    public XYPolynomial sub( XYPolynomial p  )
    {
        return this.add( p.neg() );
    }

    public XYPolynomial add( XYPolynomial p )
    {
        double[] result_coeffs = new double[ this.coeffs.length + p.coeffs.length ];
        byte[] result_xExps = new byte[ this.coeffs.length + p.coeffs.length ];
        byte[] result_yExps = new byte[ this.coeffs.length + p.coeffs.length ];

        // merge term lists and sum terms of same order
        int result_i = 0;
        int i1 = 0;
        int i2 = 0;

        while( i1 < coeffs.length && i2 < p.coeffs.length )
        {
            int lexCompare = lexCompare( this, p, i1, i2 );
            if( lexCompare < 0 )
            {
                result_coeffs[ result_i ] = this.coeffs[ i1 ];
                result_xExps[ result_i ] = this.xExps[ i1 ];
                result_yExps[ result_i ] = this.yExps[ i1 ];
                ++result_i;
                ++i1;
            }
            else if( lexCompare > 0 )
            {
                result_coeffs[ result_i ] = p.coeffs[ i2 ];
                result_xExps[ result_i ] = p.xExps[ i2 ];
                result_yExps[ result_i ] = p.yExps[ i2 ];
                ++result_i;
                ++i2;
            }
            else
            {
                // add terms
                result_coeffs[ result_i ] = this.coeffs[ i1 ] + p.coeffs[ i2 ];
                result_xExps[ result_i ] = this.xExps[ i1 ];
                result_yExps[ result_i ] = this.yExps[ i1 ];
                ++result_i;
                ++i1;
                ++i2;
            }
        }

        while( i1 < this.coeffs.length )
        {
            result_coeffs[ result_i ] = this.coeffs[ i1 ];
            result_xExps[ result_i ] = this.xExps[ i1 ];
            result_yExps[ result_i ] = this.yExps[ i1 ];
            ++result_i;
            ++i1;
        }
        while( i2 < p.coeffs.length )
        {
            result_coeffs[ result_i ] = p.coeffs[ i2 ];
            result_xExps[ result_i ] = p.xExps[ i2 ];
            result_yExps[ result_i ] = p.yExps[ i2 ];
            ++result_i;
            ++i2;
        }

        if( result_i == result_coeffs.length )
            return new XYPolynomial( result_coeffs, xExps, yExps, false );
        else
            return new XYPolynomial( result_coeffs, xExps, yExps, 0, result_i );
    }

    public XYPolynomial mult( XYPolynomial p )
    {
        XYPolynomial result = new XYPolynomial();
        for( int j = 0; j < this.coeffs.length; ++j )
        {
            XYPolynomial partialProduct = new XYPolynomial( p.coeffs.length );
            for( int i = 0; i < p.coeffs.length; i++ )
            {
                partialProduct.coeffs[ i ] = this.coeffs[ j ] * p.coeffs[ i ];
                partialProduct.xExps[ i ] = ( byte ) ( this.xExps[ j ] + p.xExps[ i ] );
                partialProduct.yExps[ i ] = ( byte ) ( this.yExps[ j ] + p.yExps[ i ] );
            }
            partialProduct.calculateDegree();
            result = result.add( partialProduct );
        }
        return result;
    }

    public XYPolynomial mult( double d )
    {
        double[] newCoeffs = new double[ this.coeffs.length ];
        for( int i = 0; i < this.coeffs.length; ++i )
            newCoeffs[ i ] = d * coeffs[ i ];
        return new XYPolynomial( newCoeffs, xExps, yExps, false );
    }

    public XYPolynomial pow( int exp )
    {
        if( exp == 0 )
        {
            return new XYPolynomial( 1.0 );
        }
        else
        {
            XYPolynomial result = this;
            XYPolynomial x = this;

            exp--;
            while( exp > 0 )
            {
                if( ( exp & 1 ) == 1 )
                {
                    result = result.mult( x );
                    exp--;
                }
                x = x.mult( x );
                exp /= 2;
            }
            return result;
        }
    }

    public double evaluateXY( double x, double y )
    {
        double result = 0.0;
        for( int i = 0; i < this.coeffs.length; ++i )
            result += coeffs[ i ] * Math.pow( x, xExps[ i ] ) * Math.pow( y, yExps[ i ] );
        return result;
    }

    public UnivariatePolynomial evaluateY( double y )
    {
        if( this.coeffs.length == 0 )
            return new UnivariatePolynomial( 0.0 );
/*
        if( !isCompact )
        {
            terms = collect( terms, true );
            isCompact = true;
        }
*/
        double[] yPowers = new double[ yDegree * 2 + 1 ];
        for( int i = 0; i <= yDegree; ++i )
            yPowers[ yDegree + i ] = Math.pow( y, i );

        int term_index, incr, last_index;
        if( Math.abs( y ) > 1.0 )
        {
            term_index = 0;
            incr = 1;
            last_index = coeffs.length - 1;
            for( int i = 1; i <= yDegree; ++i )
                yPowers[ yDegree - i ] = 1.0 / yPowers[ yDegree + i ];
        }
        else
        {
            term_index =  coeffs.length - 1;
            incr = -1;
            last_index = 0;
        }
        int last_xExp = xExps[ term_index ];
        int last_yExp = yExps[ term_index ];

        double a[] = new double[ xExps[ coeffs.length - 1 ] + 1 ];
        double cur_coeff = coeffs[ term_index ];

        //java.math.BigDecimal c_bd = new java.math.BigDecimal( last_t.coeff );
        //java.math.BigDecimal y_bd = new java.math.BigDecimal( y );
        while( incr * term_index < incr * last_index  )
        {
            term_index += incr;
            int xExp = xExps[ term_index ];
            int yExp = yExps[ term_index ];

            if( last_xExp == xExp )
            {
                cur_coeff = cur_coeff * yPowers[ yDegree + ( last_yExp - yExp ) ] + coeffs[ term_index ]; // stardard Horner step for abs(z)<=1.0, inverser Horner otherwise (mult with 1/y)
                //c_bd = c_bd.multiply( y_bd.pow( last_t.yExp - t.yExp ) ).add( new java.math.BigDecimal( t.coeff ) );
            }
            else
            {
                cur_coeff = cur_coeff * yPowers[ yDegree + last_yExp ]; // finalize term
                //c_bd = c_bd.multiply( y_bd.pow( last_t.yExp ) );
                //a[ last_t.xExp ] = c_bd.doubleValue();
                a[ last_xExp ] = cur_coeff;
                cur_coeff = coeffs[ term_index ];
                //c_bd = new java.math.BigDecimal( t.coeff );
            }
            last_xExp = xExp;
            last_yExp = yExp;
        }

        // finalize last term
        cur_coeff = cur_coeff * yPowers[ yDegree + last_yExp ]; // finalize term
        a[ last_xExp ] = cur_coeff;
        //c_bd = c_bd.multiply( y_bd.pow( last_t.yExp ) );
        //a[ last_t.xExp ] = c_bd.doubleValue();

        UnivariatePolynomial result = new UnivariatePolynomial( a, false );
        return result;
    }

    /**
     *
     * @return This polynomial without non-constant terms that have a zero coefficient.
     */
    public XYPolynomial eliminateZeroTerms()
    {
        XYPolynomial tmp_result = new XYPolynomial( this.coeffs.length );
        int j = 0;
        for( int i = 0; i < this.coeffs.length; i++ )
        {
            if( this.coeffs[ i ] != 0.0 )
            {
                tmp_result.coeffs[ j ] = this.coeffs[ i ];
                tmp_result.xExps[ j ] = this.xExps[ i ];
                tmp_result.yExps[ j ] = this.yExps[ i ];
                ++j;
            }
        }
        if( j == 0 )
            ++j;

        return new XYPolynomial( tmp_result.coeffs, tmp_result.xExps, tmp_result.yExps, 0, j );
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for( int i = 0; i < this.coeffs.length; ++i )
            sb.append( termToString( i ) );
        return sb.toString();
    }

    private String termToStringShort( int i )
    {
        StringBuffer result = new StringBuffer();
        if( coeffs[ i ] != 1.0 )
        {
            if( coeffs[ i ] == -1.0 && !( xExps[ i ] == 0 && yExps[ i ] == 0 ) )
                result.append( '-' );
            else
                result.append( coeffs[ i ] );
        }

        if( xExps[ i ] >= 1 )
            result.append( 'x' );
        if( xExps[ i ] > 1 )
            result.append( "^" + xExps[ i ] );
        if( yExps[ i ] >= 1 )
            result.append( 'y' );
        if( yExps[ i ] > 1 )
            result.append( "^" + yExps[ i ] );
        return result.toString();
    }

    private String termToString( int i )
    {
        StringBuffer result = new StringBuffer();
        if( coeffs[ i ] >= 0.0 )
            result.append( '+' );
        
        result.append( coeffs[ i ] );
        result.append( "x^" + xExps[ i ] );
        result.append( "y^" + yExps[ i ] );
        return result.toString();
    }
}