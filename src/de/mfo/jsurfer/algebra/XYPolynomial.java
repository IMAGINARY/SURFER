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
    public static class Term
    {
        double coeff;
        int xExp;
        int yExp;

        public Term( Term t )
        {
            this.coeff = t.coeff;
            this.xExp = t.xExp;
            this.yExp = t.yExp;
        }
        
        public Term( double coeff, int xExp, int yExp )
        {
            this.coeff = coeff;
            this.xExp = xExp;
            this.yExp = yExp;
        }

        public double getCoeff()
        {
            return coeff;
        }
        
        public int getXExp()
        {
            return xExp;
        }
        
        public int getYExp()
        {
            return yExp;
        }

        public int lexCompare( Term t )
        {
            int result = this.xExp < t.xExp ? -4 : ( this.xExp > t.xExp ? 4 : 0 );
            if( result == 0 )
                result += this.yExp < t.yExp ? -2 : ( this.yExp > t.yExp ? 2 : 0 );
            return result;
        }
        
        public Term mult( Term t )
        {
            return new Term( this.coeff * t.coeff, this.xExp + t.xExp, this.yExp + t.yExp );
        }
        
        public double evaluateAt( double x, double y )
        {
            return coeff * Helper.pow( x, xExp ) * Helper.pow( y, yExp );
        }

        public String toString()
        {
            StringBuffer result = new StringBuffer();
            if( coeff != 1.0 )
            {
                if( coeff == -1.0 && !( xExp == 0 && yExp == 0 ) )
                    result.append( '-' );
                else
                    result.append( coeff );
            }
            
            if( xExp >= 1 )
                result.append( 'x' );
            if( xExp > 1 )
                result.append( "^" + xExp );
            if( yExp >= 1 )
                result.append( 'y' );
            if( yExp > 1 )
                result.append( "^" + yExp );
            return result.toString();
        }
    }

    private Term[] terms; // ordered list of terms, such that terms[ i ].lexCompare( terms[ i + 1 ] ) < 0
    
    public XYPolynomial()
    {
        this( 0.0 );
    }
    
    public XYPolynomial( double value )
    {
        terms = new Term[ 1 ];
        terms[ 0 ] = new Term( value, 0, 0 );
    }

    public XYPolynomial( XYPolynomial p  )
    {
        this( p.terms );
    }    
    
    public XYPolynomial( Term t )
    {
        terms = new Term[ 1 ];
        terms[ 0 ] = new Term( t );
    }
    
    XYPolynomial( Term[] terms, boolean copy, int from, int length )
    {
        if( copy )
        {
            this.terms = new Term[ length ];
            System.arraycopy( terms, from, this.terms, 0, length );
        }
        else
        {
            this.terms = terms;
        }
    }

    XYPolynomial( Term[] terms )
    {
        this( terms, true, 0, terms.length );
    }    

    public Term[] getTerms()
    {
        Term[] t = new Term[ this.terms.length ];
        System.arraycopy( terms, 0, t, 0, this.terms.length );
        return t;
    }
    
    public XYPolynomial neg()
    {
        XYPolynomial result = new XYPolynomial( new Term[ this.terms.length ] );
        for( int i = 0; i < this.terms.length; i++ )
        {
            Term t = this.terms[ i ];
            result.terms[ i ] = new Term( -t.coeff, t.xExp, t.yExp );
        }
        return result;
    }
    
    public XYPolynomial sub( XYPolynomial p  )
    {
        return this.add( p.neg() );
    }
    
    public XYPolynomial add( XYPolynomial p )
    {
        LinkedList< Term > resultTerms = new LinkedList< Term >();
        
        // merge term lists and sum terms of same order
        Term[] terms1 = this.terms;
        Term[] terms2 = p.terms;
        int i1 = 0;
        int i2 = 0;
        
        while( i1 < terms1.length && i2 < terms2.length )
        {
            int lexCompare = terms1[ i1 ].lexCompare( terms2[ i2 ] );
            if( lexCompare < 0 )
            {
                resultTerms.add( new Term( terms1 [ i1++ ] ) );
            }
            else if( lexCompare > 0 )
            {
                resultTerms.add( new Term( terms2 [ i2++ ] ) );
            }
            else
            {
                // add terms
                Term sum = new Term( terms1 [ i1++ ] );
                sum.coeff += terms2[ i2++ ].coeff;
                resultTerms.add( sum );
            }
        }
                
        while( i1 < terms1.length )
            resultTerms.add( new Term( terms1 [ i1++ ] ) );
        while( i2 < terms2.length )
            resultTerms.add( new Term( terms2 [ i2++ ] ) );
        
        return new XYPolynomial( resultTerms.toArray( new Term[ 0 ] ) );
    }

    public XYPolynomial mult( XYPolynomial p )
    {
        XYPolynomial result = new XYPolynomial();
        for( Term t1 : this.terms )
        {
            Term[] partialProductTerms = new Term[ p.terms.length ];
            for( int i = 0; i < p.terms.length; i++ )
            {
                Term t2 = p.terms[ i ];
                partialProductTerms[ i ] = t1.mult( t2 );
            }
            result = result.add( new XYPolynomial( partialProductTerms ) );
        }
        return result;
    }
    
    public XYPolynomial mult( double d )
    {
        Term[] resultTerms = new Term[ this.terms.length ];
        for( int i = 0; i < this.terms.length; i++ )
        {
            Term t = this.terms[ i ];
            resultTerms[ i ] = new Term( d * t.coeff, t.xExp, t.yExp );
        }
        return new XYPolynomial( resultTerms );
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
    
    public double evaluateAt( double x, double y )
    {
        double result = 0.0;
        for( Term t : this.terms )
            result += t.evaluateAt( x, y );
        return result;
    }
    
    public XYPolynomial deriveX()
    {
        Term[] result_terms_temp = new Term[ this.terms.length ];
        int result_length = 0;
        for( Term t : this.terms )
            if( t.getXExp() > 0 && t.getCoeff() != 0.0 )
                result_terms_temp[ result_length++ ] = new Term( t.getCoeff() * t.getXExp(), t.getXExp() - 1, t.getYExp() );
        Term[] result_terms = new Term[ result_length ];
        System.arraycopy( result_terms_temp, 0, result_terms, 0, result_length );
        return new XYPolynomial( result_terms, false, 0, 0 );
    }
    
    public XYPolynomial deriveY()
    {
        Term[] result_terms_temp = new Term[ this.terms.length ];
        int result_length = 0;
        for( Term t : this.terms )
            if( t.getYExp() > 0 && t.getCoeff() != 0.0 )
                result_terms_temp[ result_length++ ] = new Term( t.getCoeff() * t.getYExp(), t.getXExp(), t.getYExp() - 1 );
        Term[] result_terms = new Term[ result_length ];
        System.arraycopy( result_terms_temp, 0, result_terms, 0, result_length );
        return new XYPolynomial( result_terms, false, 0, 0 );
    }
    
    public UnivariatePolynomial substituteY( double y )
    {
        /*
        // find degree of polynomial
        int degree = 0;
        for( Term t : this.terms )
            degree = Math.max( degree, Math.max( t.xExp, t.yExp ) );
        
        double[][] xyCoeffs = new double[ degree + 1 ][ degree + 1 ];
            
        for( Term t : this.terms )
            xyCoeffs[ t.xExp ][ t.yExp ] = t.coeff;

        double[] xCoeffs = new double[ degree + 1 ];
        for( int i = 0; i <= degree; i++ )
        {
            xCoeffs[ i ] = xyCoeffs[ i ][ degree ];
            for( int j = degree - 1; j >= 0; j-- )
                xCoeffs[ i ] = xCoeffs[ i ] * y + xyCoeffs[ i ][ j ];
        }
    
        return new UnivariatePolynomial( false, xCoeffs );
        */
        
            double[] xTerms = new double[ this.terms[ this.terms.length - 1 ].xExp + 1 ];
            double[] yPowers = new double[ this.terms.length ];
            yPowers[ 0 ] = 1.0;
            for( int i = 1; i < yPowers.length; i++ )
                yPowers[ i ] = yPowers[ i - 1 ] * y;
            
            for( Term t : this.terms )
                xTerms[ t.xExp ] += t.coeff * yPowers[ t.yExp ];
            
            return new UnivariatePolynomial( xTerms, false );
    }
    
    /**
     * 
     * @return This polynomial without non-constant terms that have a zero coefficient. 
     */
    public XYPolynomial eliminateZeroTerms()
    {
        int j = 0;
        for( int i = 0; i < this.terms.length; i++ )
            if( this.terms[ i ].coeff != 0.0 )
                this.terms[ j++ ] = this.terms[ i ];
        if( j == 0 )
            this.terms[ j++ ] = new Term( 0.0, 0, 0 );
        Term[] resultTerms = new Term[ j ];
        System.arraycopy( this.terms, 0, resultTerms, 0, resultTerms.length );
        this.terms = resultTerms;
        
        return this;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for( Term t : this.terms )
            sb.append( ( t.coeff > 0.0 ? "+" : "" ) + t  );
        if( sb.charAt( 0 ) == '+' )
            sb.deleteCharAt( 0 );
        return sb.toString();
    }
}

