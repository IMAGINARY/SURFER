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
public class EVALRootFinder implements RealRootFinder
{
    public static final double EPSILON = 1e-7;

    boolean makeSquarefree;

    public EVALRootFinder( boolean makeSquarefree )
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

        return null;
    }


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

        return EVAL( p, lowerBound, upperBound, p.evaluateAt( lowerBound ), p.evaluateAt( upperBound ) );
    }

    public double EVAL( UnivariatePolynomial p, double lowerBound, double upperBound, double fl, double fu )
    {
        //System.out.println( "EVAL on (" + lowerBound + "," + upperBound + ") |-> (" + fl + "," + fu + ")" );
        double m = ( lowerBound + upperBound ) / 2.0;
        double r = ( upperBound - lowerBound ) / 2.0;
        //System.out.println( "(m,r)=("+m+","+r+")" );
        if( upperBound - lowerBound < EPSILON )
        {
          //  System.out.println( "epsilon reached" );
            // prevent infinite loop (e.g. for non-squarefree polynomials)
            if( fl * fu < 0.0 )
                return m;
            else
                return m;//java.lang.Double.NaN;
        }

        // test for exclusion predicate: no real root in (m-r,m+r)
        UnivariatePolynomial t = p.shift( m );
        //System.out.println( "Taylor exp. of f(x-" + m + "): " + t );
        double fm = t.getCoeff( 0 );
        double ta[] = t.getCoeffs();
        ta[ 0 ] = -Math.abs( ta[ 0 ] );
        for( int i = 1; i < ta.length; ++i )
            ta[ i ] = Math.abs( ta[ i ] );
        if( 0.0 > new UnivariatePolynomial( ta ).evaluateAt( r ) )
            return java.lang.Double.NaN;
        //System.out.println( "Exclusion predicate false" );
        
        // test for inclusion predicate: exactly one real root in (m-r,m+r)
        UnivariatePolynomial t_der = p.derive().shift( m );
        //System.out.println( "Taylor exp. of f'(x-" + m + "): " + t_der );
        double ta_der[] = t_der.getCoeffs();
        ta_der[ 0 ] = -Math.abs( ta_der[ 0 ] );
        for( int i = 1; i < ta_der.length; ++i )
            ta_der[ i ] = Math.abs( ta_der[ i ] );

        if( fl*fu <= 0.0 && 0.0 > new UnivariatePolynomial( ta_der ).evaluateAt( r ) )
            return bisect( p, lowerBound, upperBound, fl, fu );
        //System.out.println( "Inclusion predicate false" );        System.out.println();
        // bisect
        double result_left = EVAL( p, lowerBound, m, fl, fm );
        if( !java.lang.Double.isNaN( result_left ) )
            return result_left;
        else
            return EVAL( p, m, upperBound, fm, fu );
    }

    private double evaluateAt( double a[], int start, double where )
    {
        int deg = a.length - 1;
        if( Math.abs( where ) <= 1.0 )
        {
            double result = a[ deg ];
            for( int i = deg - 2; i >= start; i-- )
                result = result * where + a[ i ];
            return result;
        }
        else
        {
            double result = a[ 0 + start ];
            for( int i = 1 + start; i < deg; i++ )
                result = result / where + a[ i ];
            return result * Math.pow( where, deg - start );
        }
    }


    private static void taylor_exp( double a[], double r[], double x0 )
    {
        double rr;
        int n = a.length - 1;
        rr = a[ n ];
        for( int i = 0; i <= n; ++i )
            r[ i ] = rr;
        for( int j = 1; j <= n; ++j )
        {
            r[ n ] = r[ n ] * x0 + a[ j ];
            for( int i = 1; i <= n - j; ++i )
                r[ n - i ] = r[ n - i ] * x0 + r[ n - ( i - 1 ) ];
        }
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

    public static void main( String args[] )
    {
        double a[] = { -6, 11, -6, 1 };
        EVALRootFinder rf = new EVALRootFinder( false );
        rf.findFirstRootIn( new UnivariatePolynomial( a ), 0.0, 5.0 );
        double t[] = new double[ a.length ];
        taylor_exp( a, t, 0.0 );
        System.out.println( java.util.Arrays.toString( t ) );
    }

}