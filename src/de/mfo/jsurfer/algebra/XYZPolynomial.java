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
public class XYZPolynomial
{
    public static class Term implements Comparable< Term >
    {
        double coeff;
        int xExp;
        int yExp;
        int zExp;

        public Term( Term t )
        {
            this.coeff = t.coeff;
            this.xExp = t.xExp;
            this.yExp = t.yExp;
            this.zExp = t.zExp;
        }
        
        public Term( double coeff, int xExp, int yExp, int zExp )
        {
            this.coeff = coeff;
            this.xExp = xExp;
            this.yExp = yExp;
            this.zExp = zExp;
        }

        public int lexCompare( Term t )
        {
            int result = this.xExp < t.xExp ? -4 : ( this.xExp > t.xExp ? 4 : 0 );
            if( result == 0 )
            {
                result += this.yExp < t.yExp ? -2 : ( this.yExp > t.yExp ? 2 : 0 );
                if( result == 0 )
                {
                    result += this.zExp < t.zExp ? -1 : ( this.zExp > t.zExp ? 1 : 0 );
                }
            }            
            return result;
        }
        
        public int compareTo( Term t )
        {
            int result = this.xExp < t.xExp ? -8 : ( this.xExp > t.xExp ? 8 : 0 );
            if( result == 0 )
            {
                result += this.yExp < t.yExp ? -4 : ( this.yExp > t.yExp ? 4 : 0 );
                if( result == 0 )
                {
                    result += this.zExp < t.zExp ? -2 : ( this.zExp > t.zExp ? 2 : 0 );
                    if( result == 0 )
                    {
                        result += this.coeff < t.coeff ? -1 : ( this.coeff > t.coeff ? 2 : 0 );
                    }
                }
            }            
            return result;            
        }
        
        public Term mult( Term t )
        {
            return new Term( this.coeff * t.coeff, this.xExp + t.xExp, this.yExp + t.yExp, this.zExp + t.zExp );
        }
        
        public double evaluateAt( double x, double y, double z )
        {
            return coeff * Helper.pow( x, xExp ) * Helper.pow( y, yExp ) * Helper.pow( z, zExp );
        }

        public String toString()
        {
            StringBuffer result = new StringBuffer();
            if( coeff != 1.0 )
            {
                if( coeff == -1.0 && !( xExp == 0 && yExp == 0 && zExp == 0 ) )
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
            if( zExp >= 1 )
                result.append( 'z' );
            if( zExp > 1 )
                result.append( "^" + zExp );
            return result.toString();
        }
    }

    private Term[] terms; // ordered list of terms, such that terms[ i ].lexCompare( terms[ i + 1 ] ) < 0
    private int xDegree;
    private int yDegree;
    private int zDegree;
    private int minDegree;
    private int maxDegree;
    private int degree;
    private int xyTermsLength; // number of terms after substitions of z
    
    public XYZPolynomial()
    {
        this( 0.0 );
    }
    
    public XYZPolynomial( double value )
    {
        this( new Term( value, 0, 0, 0 ) );
    }

    public XYZPolynomial( XYZPolynomial p )
    {
        this.terms = new Term[ p.terms.length ];
        for( int i = 0; i < this.terms.length; i++ )
            this.terms[ i ] = new Term( p.terms[ i ] );
        calculateDegree();
    }
    
    public XYZPolynomial( Term t )
    {
        terms = new Term[ 1 ];
        terms[ 0 ] = new Term( t );
        calculateDegree();
    }
    
    XYZPolynomial( Term[] terms )
    {
        this.terms = terms;
        calculateDegree();
    }
    
    private void calculateDegree()
    {
        xDegree = 0;
        yDegree = 0;
        zDegree = 0;
        
        Term lastTerm = this.terms[ 0 ];
        xyTermsLength = 1;
        for( Term t : this.terms )
        {
            xDegree = Math.max( xDegree, t.xExp );
            yDegree = Math.max( yDegree, t.yExp );
            zDegree = Math.max( zDegree, t.zExp );
            if( lastTerm.xExp != t.xExp || lastTerm.yExp != t.yExp )
                xyTermsLength++;
            lastTerm = t;
        }

        minDegree = Math.min( xDegree, Math.min( yDegree, zDegree ) );
        maxDegree = Math.max( xDegree, Math.max( yDegree, zDegree ) );
        degree = xDegree + yDegree + zDegree;
    }
    
    Term[] getTerms()
    {
        return this.terms;
    }
    
    public XYZPolynomial neg()
    {
        Term[] resultTerms = new Term[ this.terms.length ];
        for( int i = 0; i < this.terms.length; i++ )
        {
            Term t = this.terms[ i ];
            resultTerms[ i ] = new Term( -t.coeff, t.xExp, t.yExp, t.zExp );
        }
        return new XYZPolynomial( resultTerms );
    }
    
    public XYZPolynomial sub( XYZPolynomial p )
    {
        return this.add( p.neg() );
    }
    
    public XYZPolynomial add( XYZPolynomial p )
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
        
        return new XYZPolynomial( resultTerms.toArray( this.terms ) );
    }
    
    public XYZPolynomial mult( XYZPolynomial p )
    {
        XYZPolynomial result = new XYZPolynomial();
        for( Term t1 : this.terms )
        {
            Term[] partialProductTerms = new Term[ p.terms.length ];
            for( int i = 0; i < p.terms.length; i++ )
            {
                Term t2 = p.terms[ i ];
                partialProductTerms[ i ] = t1.mult( t2 );
            }
            result = result.add( new XYZPolynomial( partialProductTerms ) );
        }
        return result;
    }

    private static XYZPolynomial mult( XYZPolynomial p1, XYZPolynomial p2 )
    {
        LinkedList< Term > resultTerms = new LinkedList< Term >();
        for( Term t1 : p1.terms )
        {
            for( int i = 0; i < p2.terms.length; i++ )
                resultTerms.add( t1.mult( p2.terms[ i ] ) );
        }
        return new XYZPolynomial( collect( resultTerms.toArray( new Term[ 0 ] ), true ) );
    }

//    // standard polynomial multiplication (faster, but less numerically stable)
//    private static XYZPolynomial mult( XYZPolynomial p1, XYZPolynomial p2 )
//    {
//        XYZPolynomial result = new XYZPolynomial();
//        for( Term t1 : p1.terms )
//        {
//            Term[] partialProductTerms = new Term[ p2.terms.length ];
//            for( int i = 0; i < p2.terms.length; i++ )
//            {
//                Term t2 = p2.terms[ i ];
//                partialProductTerms[ i ] = t1.mult( t2 );
//            }
//            result = result.add( new XYZPolynomial( partialProductTerms ) );
//        }
//        return result;
//    }

    public XYZPolynomial mult( double d )
    {
        Term[] resultTerms = new Term[ this.terms.length ];
        for( int i = 0; i < this.terms.length; i++ )
        {
            Term t = this.terms[ i ];
            resultTerms[ i ] = new Term( d * t.coeff, t.xExp, t.yExp, t.zExp );
        }
        return new XYZPolynomial( resultTerms );
    }
    
    public XYZPolynomial pow( int exp )
    {
        if( exp == 0 )
        {
            return new XYZPolynomial( 1.0 );
        }
        else
        {
            XYZPolynomial result = this;
            XYZPolynomial x = this;
            
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
    
    public double evaluateAt( double x, double y, double z )
    {
        double[] xPowers = new double[ maxDegree + 1 ];
        double[] yPowers = new double[ maxDegree + 1 ];
        double[] zPowers = new double[ maxDegree + 1 ];
        xPowers[ 0 ] = 1.0;
        yPowers[ 0 ] = 1.0;
        zPowers[ 0 ] = 1.0;
        
        for( int i = 1; i <= maxDegree; i++ )
        {
            xPowers[ i ] = xPowers[ i - 1 ] * x;
            yPowers[ i ] = yPowers[ i - 1 ] * y;
            zPowers[ i ] = zPowers[ i - 1 ] * z;
        }
        
        double result = 0.0;
        for( Term t : this.terms )
            result += t.coeff * xPowers[ t.xExp ] * yPowers[ t.yExp ] * zPowers[ t.zExp ];
        
        return result;
    }

    public XYPolynomial substituteZ( double z )
    {
        double[] zPowers = new double[ zDegree + 1 ];
        zPowers[ 0 ] = 1.0;
        for( int i = 1; i < zPowers.length; i++ )
            zPowers[ i ] = zPowers[ i - 1 ] * z;

        XYPolynomial.Term[] xyTerms = new XYPolynomial.Term[ this.xyTermsLength ];
        xyTerms[ 0 ] = new XYPolynomial.Term( 0.0, this.terms[ 0 ].xExp, this.terms[ 0 ].yExp );
        int xyTermsIndex = 0;
        for( Term t : this.terms )
            if( t.xExp == xyTerms[ xyTermsIndex ].xExp && t.yExp == xyTerms[ xyTermsIndex ].yExp )
                xyTerms[ xyTermsIndex ].coeff += t.coeff * zPowers[ t.zExp ];
            else
                xyTerms[ ++xyTermsIndex ] = new XYPolynomial.Term( t.coeff * zPowers[ t.zExp ], t.xExp, t.yExp );
        
        return new XYPolynomial( xyTerms, false, 0, 0 );
    }
    
    /**
     * 
     * @return This polynomial without non-constant terms that have a zero coefficient. 
     */
    public XYZPolynomial eliminateZeroTerms()
    {
        int j = 0;
        for( int i = 0; i < this.terms.length; i++ )
            if( this.terms[ i ].coeff != 0.0 )
                this.terms[ j++ ] = this.terms[ i ];
        if( j == 0 )
            this.terms[ j++ ] = new Term( 0.0, 0, 0, 0 );
        Term[] resultTerms = new Term[ j ];
        System.arraycopy( this.terms, 0, resultTerms, 0, resultTerms.length );
        this.terms = resultTerms;
        
        // degree of polynomial may have changed -> renew stored values
        calculateDegree();
        
        return this;
    }
    
    public XYZPolynomial substitute( XYZPolynomial xPoly, XYZPolynomial yPoly, XYZPolynomial zPoly )
    {
        LinkedList< Term > terms = new LinkedList< Term >();
        for( Term t : this.terms )
        {
            XYZPolynomial xPolyXExp = xPoly.pow( t.xExp );
            XYZPolynomial yPolyYExp = yPoly.pow( t.yExp );
            XYZPolynomial zPolyZExp = zPoly.pow( t.zExp );
            
            XYZPolynomial expandedTerm = ( xPolyXExp.mult( yPolyYExp ).mult( zPolyZExp ) ).mult( t.coeff );
            for( Term e : expandedTerm.terms )
                terms.add( e );
        }
        XYZPolynomial result = new XYZPolynomial( collect( terms.toArray( new Term[ 0 ] ), true ) );
        return result;
    }

    private static Term[] collect( Term[] terms, boolean sort )
    {
        if( terms.length > 0 )
        {
            if( sort )
                Arrays.sort( terms, null );
            LinkedList< Term > resultTerms = new LinkedList< Term >();
            int j = 0, i = 1;
            for( ; i < terms.length; i++ )
            {
                if( terms[ j ].lexCompare( terms[ i ] ) != 0 )
                {
                    double kahanSum = kahanSum( terms, j, i - j );
                    if( kahanSum != 0.0 )
                        resultTerms.add( new Term( kahanSum, terms[ j ].xExp, terms[ j ].yExp, terms[ j ].zExp ) );
                    j = i;
                }
            }
            if( j != i )
            {
                double kahanSum = kahanSum( terms, j, i - j );
                if( kahanSum != 0.0 )
                    resultTerms.add( new Term( kahanSum, terms[ j ].xExp, terms[ j ].yExp, terms[ j ].zExp ) );
            }
            
            if( resultTerms.size() == 0 )
                resultTerms.add( new Term( 0.0, 0, 0, 0 ) );
            return resultTerms.toArray( new Term[ 0 ] );
        }
        else
        {
            return terms;
        }
    }

    // numerical stable sum over an array of numbers
    private static double kahanSum( Term[] terms, int from, int num )
    {
        double sum = 0.0;
        if( num != 0 )
        {
            sum = terms[ from ].coeff;
            double c = 0.0;
            for( int i = from + 1; i < from + num; i++ )
            {
                double y = terms[ i ].coeff - c;
                double t = sum + y;
                c = ( t - sum ) - y;
                sum = t;
            }
        }
        
        return sum;
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

