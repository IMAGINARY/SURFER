/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package de.mfo.jsurfer.algebra;

import java.util.*;
import java.math.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class BernsteinDescartesRootFinder implements RealRootFinder
{
    boolean makeSquarefree;
    public static final double EPSILON = 1e-6;

    class PolyInterval
    {
        public double[] a;
        public double l;
        public double u;
        public PolyInterval( double[] a, double l, double u )
        {
            this.a = a;
            this.l = l;
            this.u = u;
        }
        public PolyInterval( double[] a, boolean shift, double l, double u )
        {
            this.a = a;
            this.l = l;
            this.u = u;
        }
    }

    public BernsteinDescartesRootFinder( boolean makeSquarefree )
    {
        this.makeSquarefree = makeSquarefree;
    }

    /**
     * Find all real roots of p.
     * @param p
     * @return
     */
    public double[] findAllRoots( UnivariatePolynomial p )
    {
        return findAllRootsIn( p, -p.stretch( -1.0 ).maxPositiveRootBound() * ( 1.0 + 2.220446049250313E-16 ), p.maxPositiveRootBound() * ( 1.0 + 2.220446049250313E-16 ) );
    }

    public double[] findAllRootsIn( UnivariatePolynomial p, double lowerBound, double upperBound )
    {
        p = p.shrink();
        if( makeSquarefree )
        {
            // make p squarefree
            UnivariatePolynomial gcd = UnivariatePolynomial.gcd( p, p.derive() );
            if( gcd.degree() > 0 )
                // Polynomial not squarefree!
                p = p.div( gcd );
        }

        double[] negRoots = findAllNegRootsIn( p, lowerBound, upperBound );
        double[] posRoots = findAllPosRootsIn( p, lowerBound, upperBound );

        double[] roots = new double[ negRoots.length + posRoots.length ];
        System.arraycopy( negRoots, 0, roots, 0, negRoots.length );
        System.arraycopy( posRoots, 0, roots, negRoots.length, posRoots.length );
        return roots;
    }

    double[] findAllPosRootsIn( UnivariatePolynomial p, double lowerBound, double upperBound )
    {
        if( upperBound < 0.0 )
            return new double[ 0 ];

        double bound2 = nextPowerOfTwo( upperBound );

        p = p.shrink();
        double tlb = lowerBound / bound2;
        double tub = upperBound / bound2;


        // move all roots in (0,upperBound) into (0,1)
        p = p.stretch( bound2 );

        double[] results = new double[ p.degree() ];
        int results_length = 0;

        if( p.getCoeff( 0 ) == 0.0 )
        {
            if( lowerBound <= 0.0 )
                results[ results_length++ ] = 0.0;
            p = new UnivariatePolynomial( deflate0( p.getCoeffs() ) );
        }

        if( lowerBound < 0.0 )
            lowerBound = 0.0;

        if( results_length != results.length )
        {
            // isolate and refine all roots in (0,1)
            PolyInterval[] cand = new PolyInterval[ 10 ];
            int cand_length = 0;
            cand[ cand_length++ ] = new PolyInterval( p.getCoeff( 0 ) == 0.0 ? deflate0( p.getCoeffs() ) : p.getCoeffs(), 0.0, 1.0 );
            while( cand_length != 0 )
            {
                PolyInterval pi = cand[ --cand_length ];

                if( results_length == results.length )
                    break;
                int v = countSignChanges( pi.a );
                if( v == 1 )
                {
                    double tmp_root = adjustIntervalAndBisect( p, pi.l, pi.u, tlb, tub ) * bound2;
                    if( !java.lang.Double.isNaN( tmp_root ) )
                        results[ results_length++ ] = tmp_root;
                }
                if( results_length == results.length )
                    break;
                else if( v > 1 )
                {
                    // evtl. mehr als eine NST in (0,1) -> teile Interval (0,1) in zwei teile
                    double c = 0.5 * ( pi.l + pi.u );
                    if( Math.abs( pi.u - pi.l ) < 0.5 * EPSILON ) // we have reached maximum precision
                    {
                        if( pi.l <= tlb )
                            results[ results_length++ ] = lowerBound;
                        else
                            results[ results_length++ ] = pi.l * bound2;
                        continue;
                    }
                    if( cand_length + 2 >= cand.length )
                    {
                        // resize candidate stack
                        PolyInterval[] newCand = new PolyInterval[ 2 * cand.length ];
                        System.arraycopy( cand, 0, newCand, 0, cand.length );
                        cand = newCand;
                    }
                    double[] first = pi.a;
                    double[] second = new double[ pi.a.length ];
                    deCasteljau( first, second );

                    if( c <= tub )
                        cand[ cand_length++ ] = new PolyInterval( second, c, pi.u );
                    if( c >= tlb )
                        cand[ cand_length++ ] = new PolyInterval( first, pi.l, c );
                }
            }
        }

        double[] roots = new double[ results_length ];
        System.arraycopy( results, 0, roots, 0, results_length );
        return roots;
    }

    double[] findAllNegRootsIn( UnivariatePolynomial p, double lowerBound, double upperBound )
    {
        if( lowerBound >= 0.0 )
            return new double[ 0 ];

        // map the negative roots on positive ones, find them and transform back
        p = p.stretch( -1.0 );
        double[] roots = findAllPosRootsIn( p, -upperBound, -lowerBound );
        for( int i = 0, j = roots.length - 1; i < ( roots.length + 1 ) / 2; ++i, --j )
        {
            // reverse order and negate elements
            double tmp = -roots[ i ];
            roots[ i ] = -roots[ j ];
            roots[ j ] = tmp;
        }
        return roots;
    }

    enum WhichRoot { SMALLEST, LARGEST };


    /**
     * Find the smallest real root of p within lowerBound and upperBound (bounds may or may not be included).
     * If no real root exists in this interval, Double.NaN ist returned.
     * @param p
     * @param lowerBound
     * @param upperBound
     * @return
     */
    public double findFirstRootIn( UnivariatePolynomial p, double lowerBound, double upperBound )
    {
        if( makeSquarefree )
        {
            // make p squarefree
            UnivariatePolynomial gcd = UnivariatePolynomial.gcd( p, p.derive() );
            if( gcd.degree() > 0 )
                // Polynomial not squarefree!
                p = p.div( gcd );
        }

        double root = -findPosRootIn( p.stretch( -1.0 ), -upperBound, -lowerBound, WhichRoot.LARGEST );
        return java.lang.Double.isNaN( root ) ? findPosRootIn( p, lowerBound, upperBound, WhichRoot.SMALLEST ) : root;
    }

    double findPosRootIn( UnivariatePolynomial p, double lowerBound, double upperBound, WhichRoot w )
    {
        if( upperBound <= 0.0 || p.degree() == 0 )
            return java.lang.Double.NaN;

        p = p.shrink();

        double bound2 = nextPowerOfTwo( upperBound );
        double tlb = lowerBound / bound2;
        double tub = upperBound / bound2;

        // move all roots in (0,bound2) into (0,1)
        p = p.stretch( bound2 );
        double tmp_result = java.lang.Double.NaN;
        if( p.getCoeff( 0 ) == 0.0 )
        {
            if( lowerBound <= 0.0 )
            {
                if( w == WhichRoot.SMALLEST )
                        return 0.0;
                else
                    tmp_result = 0.0;
            }
            p = new UnivariatePolynomial( deflate0( p.getCoeffs() ) );
        }

        if( lowerBound <= 0.0 )
            lowerBound = 0.0;

        // isolate and refine first root in (0,1)
        PolyInterval[] cand = new PolyInterval[ 10 ];
        int cand_length = 0;
        cand[ cand_length++ ] = new PolyInterval( bernsteinCoefficients( p.getCoeffs() ), 0.0, 1.0 );
        while( cand_length != 0 )
        {
            PolyInterval pi = cand[ --cand_length ];
            if( pi.a[ 0 ] == 0.0 )
            {
                double tmp_root = pi.l * bound2;
                if( lowerBound <= tmp_root && tmp_root <= upperBound )
                {
                    if( w == WhichRoot.SMALLEST )
                        return pi.l * bound2;
                    else
                        tmp_result = tmp_root;
                    pi.a = deflate0( pi.a );
                }
            }
            int v = countSignChanges( pi.a );
            if( v == 1 )
            {
                double tmp_root = adjustIntervalAndBisect( p, pi.l, pi.u, tlb, tub ) * bound2;
                if( !java.lang.Double.isNaN( tmp_root ) )
                    return tmp_root;
            }
            else if( v > 1 )
            {
                // evtl. mehr als eine NST in (0,1) -> teile Interval (0,1) in zwei teile
                double c = 0.5 * ( pi.l + pi.u );
                if( Math.abs( pi.u - pi.l ) < 0.5 * EPSILON ) // we have reached maximum precision
                {
                    if( pi.l <= tlb )
                        return lowerBound;
                    else
                        return pi.l * bound2;
                }
                if( cand_length + 2 >= cand.length )
                {
                    // resize candidate stack
                    PolyInterval[] newCand = new PolyInterval[ 2 * cand.length ];
                    System.arraycopy( cand, 0, newCand, 0, cand.length );
                    cand = newCand;
                }

                double[] first = pi.a;
                double[] second = new double[ pi.a.length ];
                deCasteljau( first, second );

                if( w == WhichRoot.SMALLEST )
                {
                    // search interval with smallest lower bound first
                    if( c <= tub )
                        cand[ cand_length++ ] = new PolyInterval( second, c, pi.u );
                    if( c >= tlb )
                        cand[ cand_length++ ] = new PolyInterval( first, pi.l, c );
                }
                else
                {
                    // search interval with largest lower bound first
                    if( c >= tlb )
                        cand[ cand_length++ ] = new PolyInterval( first, pi.l, c );
                    if( c <= tub )
                        cand[ cand_length++ ] = new PolyInterval( second, c, pi.u );
                }
            }
        }
        return tmp_result;
    }

    private static double adjustIntervalAndBisect( UnivariatePolynomial p, double lowerBound, double upperBound, double strictLowerBound, double strictUpperBound )
    {
        double fl = p.evaluateAt( lowerBound );
        if( lowerBound < strictLowerBound )
        {
            // intervals overlap
            if( upperBound < strictLowerBound )
                return java.lang.Double.NaN; // search interval and strict interval do not overlap
            else
            {
                double fsl = p.evaluateAt( strictLowerBound );
                if( fl * fsl < 0.0 || fl == 0.0 )
                    return java.lang.Double.NaN; // root is below strictLowerBound
                else
                {
                    lowerBound = strictLowerBound; // root is between strictLowerBound and upperBound
                    fl = fsl;
                }
            }
        }

        double fu = p.evaluateAt( upperBound );
        if( strictUpperBound < upperBound )
        {
            // intervals overlap
            if( strictUpperBound < lowerBound )
                return java.lang.Double.NaN; // search interval and strict interval do not overlap
            else
            {
                double fsu = p.evaluateAt( strictUpperBound );
                if( fu * fsu < 0.0 || fu == 0.0 )
                    return java.lang.Double.NaN; // root is above strictLowerBound
                else
                {
                    upperBound = strictUpperBound;
                    fu = fsu;
                }
            }
        }

        return bisect( p, lowerBound, upperBound, fl, fu );
    }

    private double[] deflate0( double[] a )
    {
        if( a.length == 0 )
            return a;
        double[] deflated_a = new double[ a.length - 1 ];
        System.arraycopy( a, 1, deflated_a, 0, deflated_a.length );
        return deflated_a;
    }

    private static double bisect( UnivariatePolynomial p, double lowerBound, double upperBound )
    {
        return bisect( p, lowerBound, upperBound, p.evaluateAt( lowerBound ), p.evaluateAt( upperBound ) );
    }


    private static double bisect( UnivariatePolynomial p, double lowerBound, double upperBound, double fl, double fu )
    {
        double[] a = p.getCoeffs();

        assert fl * fu < 0.0 : "tried bisection on interval without sign change";
        while( Math.abs( upperBound - lowerBound ) > EPSILON )
        {
            double center = 0.5 * ( lowerBound + upperBound );
            double fc = a[ a.length - 1 ];
            for( int i = a.length - 2; i >= 0; i-- )
                fc = fc * center + a[ i ];

            if( fc * fl < 0.0 )
            {
                upperBound = center;
                fu = fc;
            }
            else if( fc == 0.0 )
            {
                return center;
            }
            else
            {
                lowerBound = center;
                fl = fc;
            }
        }
        return lowerBound;
    }

    static double nextPowerOfTwo( double d ) // computes the next power of two with respect to outward rounding
    {
        long bits = java.lang.Double.doubleToLongBits( d );
        if( ( bits & 0x000fffffffffffffL ) != 0L )
        {
            bits = java.lang.Double.doubleToLongBits( 2.0 * d ); // "round up" -> increase exponent by one
            bits &= 0xfff0000000000000L; // remove mantissa bits
        }
        return java.lang.Double.longBitsToDouble( bits );
    }

    private int countSignChanges( double[] a )
    {
        int signChanges = 0;

        double lastNonZeroCoeff = java.lang.Double.NaN;
        for( int i = 1; i <= a.length; i++ )
        {
            if( a[ i - 1 ] != 0.0 )
            {
                if( a[ i - 1 ] * lastNonZeroCoeff < 0.0 )
                    signChanges++;
                if( signChanges > 1 )
                    return signChanges;
                lastNonZeroCoeff = a[ i - 1 ];
            }
        }

        return signChanges;
    }

    /**
     * Computes new Bernstein coefficients for a basis in the interval (0,0.5) and (0.5,1).
     * @param a Input coefficients and output for (0,0.5)
     * @param b output for (0.5,1)
     */
    private void deCasteljau( double a[], double[] b )
    {
        b[ a.length - 1 ] = a[ a.length - 1 ];
        for( int i = 1; i < a.length; i++ )
        {
            for( int j = a.length - 1; j >= i; j-- )
                a[ j ] = ( a[ j - 1 ] + a[ j ] ) * 0.5;
            b[ a.length - 1 - i ] = a[ a.length - 1 ];
        }
    }

    private double[] bernsteinCoefficients( double a[] )
    {
        double[] result = new double[ a.length ];
        for( int i = 0; i < a.length; i++ )
            result[ i ] = a[ i ] / MultinomialCoefficients.binomialCoefficient( a.length - 1, i );
        for( int i = 1; i < a.length; i++ )
            for( int j = a.length - 1; j >= i; j-- )
                result[ j ] = result[ j - 1 ] + result[ j ];
        return result;
    }
}