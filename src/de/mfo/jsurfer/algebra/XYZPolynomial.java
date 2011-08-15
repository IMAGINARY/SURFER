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
    public static final XYZPolynomial X;
    public static final XYZPolynomial Y;
    public static final XYZPolynomial Z;
    public static final XYZPolynomial ZERO;
    public static final XYZPolynomial ONE;

    static
    {
        X = new XYZPolynomial( new Term( 1.0, (byte) 1, (byte) 0, (byte) 0 ) );
        Y = new XYZPolynomial( new Term( 1.0, (byte) 0, (byte) 1, (byte) 0 ) );
        Z = new XYZPolynomial( new Term( 1.0, (byte) 0, (byte) 0, (byte) 1 ) );
        ZERO = new XYZPolynomial( 0.0 );
        ONE = new XYZPolynomial( 1.0 );
        System.out.println( "zero: " + ZERO );
    }

    public static class Term implements Comparable< Term >
    {
        double coeff;
        byte xExp;
        byte yExp;
        byte zExp;

        public Term( Term t )
        {
            this.coeff = t.coeff;
            this.xExp = t.xExp;
            this.yExp = t.yExp;
            this.zExp = t.zExp;
        }

        public Term( double coeff, byte xExp, byte yExp, byte zExp )
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
            return new Term( this.coeff * t.coeff, (byte) ( this.xExp + t.xExp ), (byte) ( this.yExp + t.yExp ), (byte) ( this.zExp + t.zExp ) );
        }

        public Term mult( double d )
        {
            Term result = new Term( this );
            result.coeff *= d;
            return result;
        }

        public Term pow( int exp )
        {
            return new Term( Math.pow( this.coeff, exp ), (byte) ( this.xExp * exp ), (byte) ( this.yExp * exp ), (byte) ( this.zExp * exp ) );
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

        public String longToString()
        {
            StringBuffer result = new StringBuffer();
            if( coeff >= 0.0 )
                result.append( '+' );
            result.append( coeff );
            result.append( "x^" + xExp + "y^" + yExp + "z^" + zExp );
            return result.toString();
        }
    }

    private Term[] terms; // ordered list of terms, such that terms[ i ].lexCompare( terms[ i + 1 ] ) < 0
    private byte xDegree;
    private byte yDegree;
    private byte zDegree;
    private byte minDegree;
    private byte maxDegree;
    private byte degree;
    private int numXyTerms; // number of terms in result of evaluateZ
    private boolean isCompact = false;

    public XYZPolynomial()
    {
        this( 0.0 );
    }

    public XYZPolynomial( double value )
    {
        this( new Term( value, (byte) 0, (byte) 0, (byte) 0 ) );
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
        xDegree = terms[ terms.length - 1 ].xExp;
        yDegree = 0;
        zDegree = 0;
        numXyTerms = 1;

        Term last_t = terms[ 0 ];
        for( Term t : this.terms )
        {
            yDegree = (byte) Math.max( yDegree, t.yExp );
            zDegree = (byte) Math.max( zDegree, t.zExp );
            degree = (byte) Math.max( degree, t.xExp + t.yExp + t.zExp );
            if( last_t.xExp != t.xExp || last_t.yExp != t.yExp )
            {
                last_t = t;
                ++numXyTerms;
            }
        }

        minDegree = (byte) Math.min( xDegree, Math.min( yDegree, zDegree ) );
        maxDegree = (byte) Math.max( xDegree, Math.max( yDegree, zDegree ) );        
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
        return mult( this, p );
        /*
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
        */
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
            return ONE;
        else if( exp == 1 )
            return this;
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

    public double evaluateXYZ( double x, double y, double z )
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

    public XYPolynomial evaluateZ( double z )
    {
        if( terms.length == 0 )
            return new XYPolynomial( 0.0 );

        if( !isCompact )
        {
            terms = collect( terms, true );
            isCompact = true;
        }

        double[] zPowers = new double[ zDegree * 2 + 1 ];
        for( int i = 0; i <= zDegree; ++i )
            zPowers[ zDegree + i ] = Math.pow( z, i );


        double[] xy_coeffs = new double[ numXyTerms ];
        byte[] xy_xExps = new byte[ numXyTerms ];
        byte[] xy_yExps = new byte[ numXyTerms ];

        int term_index, incr, last_index;
        int xy_term_index;
        if( Math.abs( z ) > 1.0 )
        {
            term_index = 0; // iterate forwards
            incr = 1;
            last_index = terms.length - 1;
            xy_term_index = 0;
            for( int i = 1; i <= zDegree; ++i )
                zPowers[ zDegree - i ] = 1.0 / zPowers[ zDegree + i ];
        }
        else
        {
            term_index =  terms.length - 1; // iterate backwards
            incr = -1;
            last_index = 0;
            xy_term_index = xy_coeffs.length - 1;
        }
        Term last_t = terms[ term_index ];

        Term t = last_t;
        double xy_coeff = last_t.coeff;
        xy_xExps[ xy_term_index ] = last_t.xExp;
        xy_yExps[ xy_term_index ] = last_t.yExp;

        //java.math.BigDecimal xyt_bd = new java.math.BigDecimal( last_t.coeff );
        //java.math.BigDecimal z_bd = new java.math.BigDecimal( z );
        while( incr * term_index < incr * last_index  )
        {
            term_index += incr;
            t = terms[ term_index ];

            if( last_t.xExp == t.xExp && last_t.yExp == t.yExp )
            {
                xy_coeff = xy_coeff * zPowers[ zDegree + ( last_t.zExp - t.zExp ) ] + t.coeff; // stardard Horner step for abs(z)<=1.0, inverser Horner otherwise (mult with 1/z)
                //xyt_bd = xyt_bd.multiply( z_bd.pow( last_t.zExp - t.zExp ) ).add( new java.math.BigDecimal( t.coeff ) );
            }
            else
            {
                xy_coeff = xy_coeff * zPowers[ zDegree + last_t.zExp ]; // finalize term
                //xyt_bd = xyt_bd.multiply( z_bd.pow( last_t.zExp ) );
                //xyt.coeff = xyt_bd.doubleValue();
                xy_coeffs[ xy_term_index ] = xy_coeff;
                xy_term_index += incr;

                xy_coeff = t.coeff;
                xy_xExps[ xy_term_index ] = t.xExp;
                xy_yExps[ xy_term_index ] = t.yExp;
                //xyt_bd = new java.math.BigDecimal( t.coeff );
            }
            last_t = t;
        }

        // finalize last term
        xy_coeff = xy_coeff * zPowers[ zDegree + last_t.zExp ];
        xy_coeffs[ xy_term_index ] = xy_coeff;
        //xyt_bd = xyt_bd.multiply( z_bd.pow( last_t.zExp ) );
        //xyt.coeff = xyt_bd.doubleValue();

        return new XYPolynomial( xy_coeffs, xy_xExps, xy_yExps, false );
    }

/*
    public XYPolynomial evaluateZ( double z )
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
*/
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
            this.terms[ j++ ] = new Term( 0.0, (byte) 0, (byte) 0, (byte) 0 );
        Term[] resultTerms = new Term[ j ];
        System.arraycopy( this.terms, 0, resultTerms, 0, resultTerms.length );
        this.terms = resultTerms;

        // degree of polynomial may have changed -> renew stored values
        calculateDegree();

        return this;
    }

    public UnivariatePolynomial substitute( UnivariatePolynomial xPoly, UnivariatePolynomial yPoly, UnivariatePolynomial zPoly )
    {
        if( terms.length == 0 )
            return new UnivariatePolynomial();

        if( !isCompact )
        {
            terms = collect( terms, true );
            isCompact = true;
        }

        int term_index = terms.length;
        Term last_t = terms[ --term_index ];
        UnivariatePolynomial x_result = UnivariatePolynomial.ZERO;
        UnivariatePolynomial y_result = UnivariatePolynomial.ZERO;
        UnivariatePolynomial z_result = new UnivariatePolynomial( last_t.coeff );

        UnivariatePolynomial[] xPolyPowers = new UnivariatePolynomial[ this.xDegree + 1 ];
        UnivariatePolynomial[] yPolyPowers = new UnivariatePolynomial[ this.yDegree + 1 ];
        UnivariatePolynomial[] zPolyPowers = new UnivariatePolynomial[ this.zDegree + 1 ];

        Term t = last_t;
        while( term_index > 0 )
        {
            t = terms[ --term_index ];

            boolean x_equal = last_t.xExp == t.xExp;
            boolean y_equal = last_t.yExp == t.yExp;

            if( x_equal && y_equal )
                z_result = z_result.mult_add( zPoly.pow( last_t.zExp - t.zExp, zPolyPowers ), t.coeff ); // horner step for z
            else
            {
                z_result = z_result.mult( zPoly.pow( last_t.zExp, zPolyPowers ) ); // finalize z_result

                if( x_equal )
                {
                    y_result = y_result.add( z_result ).mult( yPoly.pow( last_t.yExp - t.yExp, yPolyPowers ) ); // horner step for y
                }
                else
                {
                    y_result = y_result.add( z_result ).mult( yPoly.pow( last_t.yExp, yPolyPowers ) ); // finalize y_result
                    x_result = x_result.add( y_result ).mult( xPoly.pow( last_t.xExp - t.xExp, xPolyPowers ) ); // horner step for x
                    y_result = UnivariatePolynomial.ZERO;
                }
                z_result = new UnivariatePolynomial( t.coeff );
            }
            last_t = t;
        }

        // finalize last term
        z_result = z_result.mult( zPoly.pow( last_t.zExp, zPolyPowers ) ); // finalize z_result
        y_result = y_result.add( z_result ).mult( yPoly.pow( last_t.yExp, yPolyPowers ) ); // finalize y_result
        x_result = x_result.add( y_result ).mult( xPoly.pow( last_t.xExp, zPolyPowers ) ); // finalize x_result
        return x_result;
    }
/*
    // mutlivariate polynomial substitution by multinomial expansion
    public XYZPolynomial substitute( XYZPolynomial xPoly, XYZPolynomial yPoly, XYZPolynomial zPoly )
    {
        assert xPoly.terms.length == 4;

        LinkedList< Term > result_terms = new LinkedList< Term >();
        for( Term t : this.terms )
        {
            XYZPolynomial x_subst = new XYZPolynomial( multinomialExpansion( xPoly.terms, t.xExp ) );
            XYZPolynomial y_subst = new XYZPolynomial( multinomialExpansion( yPoly.terms, t.yExp ) );
            XYZPolynomial z_subst = new XYZPolynomial( multinomialExpansion( zPoly.terms, t.zExp ) );

            result_terms.addAll( Arrays.asList( x_subst.mult( y_subst ).mult( z_subst ).mult( t.coeff ).terms ) );
        }
        return new XYZPolynomial( collect( result_terms.toArray( new Term[ result_terms.size() ] ), true ) );
    }

    static Term[] multinomialExpansion( Term[] terms, int exp )
    {
        synchronized( System.out )
        {
        System.out.println(  "(" + new XYZPolynomial( terms ) + ")^" + exp );

        assert terms.length == 4;
        Term A = terms[ 0 ];
        Term B = terms[ 1 ];
        Term C = terms[ 2 ];
        Term D = terms[ 3 ];
        LinkedList< Term > result_terms = new LinkedList< Term >();
        for( int i = 0; i <= exp; ++i )
        {
            long exp_i = MultinomialCoefficients.binomialCoefficient( exp, i );
            for( int j = 0; j <= i; ++j )
            {
                long i_j = MultinomialCoefficients.binomialCoefficient( i, j );
                for( int k = 0; k <= j; ++k )
                {
                    long j_k = MultinomialCoefficients.binomialCoefficient( j, k );
                    result_terms.add( A.pow( exp - i ).mult( B.pow( i - j ) ).mult( C.pow( j - k ) ).mult( D.pow( k ) ).mult( ( double ) ( exp_i * i_j * j_k ) ) );
                }
            }
        }

        System.out.println( "=" + new XYZPolynomial( result_terms.toArray( new Term[ result_terms.size() ] ) ) );
        System.out.println();
        System.out.flush();
        return result_terms.toArray( new Term[ result_terms.size() ] );
        }
    }
*/

    // multivariate polynomial substitution by horner evaluation
    public XYZPolynomial substitute( XYZPolynomial xPoly, XYZPolynomial yPoly, XYZPolynomial zPoly )
    {
        if( terms.length == 0 )
            return new XYZPolynomial();

        if( !isCompact )
        {
            terms = collect( terms, true );
            isCompact = true;
        }

        int term_index = terms.length;
        Term last_t = terms[ --term_index ];
        XYZPolynomial x_result = new XYZPolynomial();
        XYZPolynomial y_result = new XYZPolynomial();
        XYZPolynomial z_result = new XYZPolynomial( last_t.coeff );

        //System.out.println( "z_result=" + z_result );
        //System.out.println( "y_result=" + y_result );
        //System.out.println( "x_result=" + x_result );

        Term t = last_t;
        while( term_index > 0 )
        {
            t = terms[ --term_index ];

            boolean x_equal = last_t.xExp == t.xExp;
            boolean y_equal = last_t.yExp == t.yExp;

            if( x_equal && y_equal )
                z_result = z_result.mult( zPoly.pow( last_t.zExp - t.zExp ) ).add( new XYZPolynomial( t.coeff ) ); // horner step for z
            else
            {
                z_result = z_result.mult( zPoly.pow( last_t.zExp ) ); // finalize z_result

                if( x_equal )
                {
                    y_result = y_result.add( z_result ).mult( yPoly.pow( last_t.yExp - t.yExp ) ); // horner step for y
                }
                else
                {
                    y_result = y_result.add( z_result ).mult( yPoly.pow( last_t.yExp ) ); // finalize y_result
                    x_result = x_result.add( y_result ).mult( xPoly.pow( last_t.xExp - t.xExp ) ); // horner step for x
                    y_result = new XYZPolynomial();
                }
                z_result = new XYZPolynomial( t.coeff );
            }
            last_t = t;
        }


        //System.out.println( "last_t=" + last_t.longToString() );
        //System.out.println( "z_result=" + z_result );
        //System.out.println( "y_result=" + y_result );
        //System.out.println( "x_result=" + x_result );

        // finalize last term
        z_result = z_result.mult( zPoly.pow( last_t.zExp ) ); // finalize z_result
        //System.out.println( z_result );
        y_result = y_result.add( z_result ).mult( yPoly.pow( last_t.yExp ) ); // finalize y_result
        //System.out.println( y_result );
        x_result = x_result.add( y_result ).mult( xPoly.pow( last_t.xExp ) ); // finalize x_result
        //System.out.println( x_result );
        return x_result;
    }
/*
    // trivial way to substitute the multivariate polynomials into this
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
*/
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
                resultTerms.add( new Term( 0.0, (byte) 0, (byte) 0, (byte) 0 ) );
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

    public double[][][] recursiveView()
    {
        double[][][] a = new double[ zDegree + 1 ][][];
        for( int i = 0; i <= zDegree; ++i )
        {
            double[][] tmp = new double[ yDegree + 1 ][];
            for( int j = 0; j <= yDegree; ++j )
                tmp[ j ] = new double[ xDegree ];
            a[ i ] = tmp;
        }
        for( Term t : this.terms )
            a[ t.zExp ][ t.yExp ][ t.xExp ] = t.coeff;
        return a;
    }
}