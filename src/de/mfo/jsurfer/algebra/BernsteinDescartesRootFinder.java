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
public class BernsteinDescartesRootFinder implements RealRootFinder
{
    boolean makeSquarefree;

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
    
    /**
     * Find all real roots of p within lowerBound and upperBound (bounds may or may not be included).
     * @param p
     * @param lowerBound
     * @param upperBound
     * @return
     */
    public double[] findAllRootsIn( UnivariatePolynomial p, double lowerBound, double upperBound )
    {
        return new double[ 0 ];
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
        p = p.shrink();
        if( makeSquarefree )
        {
            // make p squarefree
            UnivariatePolynomial gcd = UnivariatePolynomial.gcd( p, p.derive() );
            if( gcd.degree() > 0 )
                // Polynomial not squarefree!
                p = p.div( gcd );
        }

        // move all roots in (lowerBound,upperBound) into (0,1)
        p = p.shift( lowerBound ).stretch( upperBound - lowerBound );
        if( p.getCoeff( 0 ) == 0.0 )
            return lowerBound;
        
        // isolate and refine first root in (0,1)
        PolyInterval[] cand = new PolyInterval[ 10 ];
        int cand_length = 0;
        cand[ cand_length++ ] = new PolyInterval( bernsteinCoefficients( p.getCoeffs() ), 0.0, 1.0 );
        while( cand_length != 0 )
        {
            PolyInterval pi = cand[ --cand_length ];
            int v = countSignChanges( pi.a );
            if( v == 1 )
                return bisect( p, pi.l, pi.u ) * ( upperBound - lowerBound ) + lowerBound;
            else if( v > 1 )
            {
                // evtl. mehr als eine NST in (0,1) -> teile Interval (0,1) in zwei teile
                double c = 0.5 * ( pi.l + pi.u );
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
                if( second[ 0 ] == 0.0 )
                    return c;
                cand[ cand_length++ ] = new PolyInterval( second, c, pi.u );
                
                pi.u = c;
                cand[ cand_length++ ] = pi;
            }
        }
        return Double.NaN;
    }
    
    private static double bisect( UnivariatePolynomial p, double lowerBound, double upperBound )
    {
        double center = lowerBound;
        float old_center = Float.NaN;
        double fl = p.evaluateAt( lowerBound );
        double fu = p.evaluateAt( upperBound );
        double[] a = p.getCoeffs();
       
        assert fl * fu < 0.0 : "tried bisection on interval without sign change";
        
        while( ( float ) center != old_center )
        {
            old_center = ( float ) center;
            center = 0.5 * ( lowerBound + upperBound );
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
                break;
            }
            else
            {
                lowerBound = center;
                fl = fc;
            }
        }
        return center;
    }
    
    private int countSignChanges( double[] a )
    {
        int signChanges = 0;
        
        double lastNonZeroCoeff = Double.NaN;
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
            result[ i ] = a[ i ] / binomialCoefficient( a.length - 1, i );
        for( int i = 1; i < a.length; i++ )
            for( int j = a.length - 1; j >= i; j-- )
                result[ j ] = result[ j - 1 ] + result[ j ];
        return result;
    }
    
    private static double[][] n_k = new double[ 100 ][ 100 ];
    
    private double binomialCoefficient( int n, int k )
    {
        if( n_k[ n ][ k ] == 0.0 )
        {
            double result;
            if( k == 0 )
                return 1.0;
            if( 2 * k > n )
                return binomialCoefficient( n, n - k );

            result = n;
            for( int i = 2; i <= k; i++ )
            {
                result *= ( n + 1 - i );
                result /= i;
            }
            n_k[ n ][ k ] = result;
        }
        return n_k[ n ][ k ];
    }
}
