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
public class DescartesRootFinder implements RealRootFinder
{
    boolean makeSquarefree;

    class PolyInterval
    {
        public double[] a;
        public boolean shift;
        public double l;
        public double u;
        public PolyInterval( double[] a, double l, double u )
        {
            this.a = a;
            this.shift = false;
            this.l = l;
            this.u = u;
        }
        public PolyInterval( double[] a, boolean shift, double l, double u )
        {
            this.a = a;
            this.shift = shift;
            this.l = l;
            this.u = u;
        }
    }    
    
    public DescartesRootFinder( boolean makeSquarefree )
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
        
        double[] results = new double[ p.degree() ];
        int results_length = 0;

        if( p.getCoeff( 0 ) == 0.0 )
        {
            results[ results_length++ ] = lowerBound;
            p = new UnivariatePolynomial( deflate0( p.getCoeffs() ) );
        }
        
        if( results_length != results.length )
        {
            // isolate and refine all roots in (0,1)
            PolyInterval[] cand = new PolyInterval[ 10 ];
            int cand_length = 0;
            cand[ cand_length++ ] = new PolyInterval( p.getCoeff( 0 ) == 0.0 ? deflate0( p.getCoeffs() ) : p.getCoeffs(), 0.0, 1.0 );
            while( cand_length != 0 )
            {
                PolyInterval pi = cand[ --cand_length ];
                if( pi.shift )
                    pi.a = shift1( pi.a );
                if( pi.a[ 0 ] == 0.0 )
                {
                    results[ results_length++ ] = pi.l;
                    p = new UnivariatePolynomial( deflate( p.getCoeffs(), pi.l ) );
                }
                if( results_length == results.length )
                    break;
                int v = descartesRuleOfSignReverseShift1( pi.a );
                if( v == 1 )
                    results[ results_length++ ] = bisect( p, pi.l, pi.u ) * ( upperBound - lowerBound ) + lowerBound;
                if( results_length == results.length )
                    break;
                else if( v > 1 )
                {
                    // evtl. mehr als eine NST in (0,1) -> teile Interval (0,1) in zwei teile
                    double c = 0.5 * ( pi.l + pi.u );
                    if( c == pi.l ) // we have reached maximum precision
                    {
                        results[ results_length++ ] = pi.l;
                        break;
                    }
                    double[] stretchedA = stretchNormalize0_5( pi.a );
                    if( cand_length + 2 >= cand.length )
                    {
                        // resize candidate stack
                        PolyInterval[] newCand = new PolyInterval[ 2 * cand.length ];
                        System.arraycopy( cand, 0, newCand, 0, cand.length );
                        cand = newCand;
                    }
                    cand[ cand_length++ ] = new PolyInterval( stretchedA, true, c, pi.u );
                    cand[ cand_length++ ] = new PolyInterval( stretchedA, false, pi.l, c );
                }
            }
        }
        
        double[] roots = new double[ results_length ];
        System.arraycopy( results, 0, roots, 0, results_length );
        return roots;
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
        cand[ cand_length++ ] = new PolyInterval( p.getCoeffs(), 0.0, 1.0 );
        while( cand_length != 0 )
        {
            PolyInterval pi = cand[ --cand_length ];
            if( pi.shift )
                pi.a = shift1( pi.a );
            if( pi.a[ 0 ] == 0.0 )
                return pi.l;
            int v = descartesRuleOfSignReverseShift1( pi.a );
            if( v == 1 )
                return bisect( p, pi.l, pi.u ) * ( upperBound - lowerBound ) + lowerBound;
            else if( v > 1 )
            {
                // evtl. mehr als eine NST in (0,1) -> teile Interval (0,1) in zwei teile
                double c = 0.5 * ( pi.l + pi.u );
                if( c == pi.l ) // we have reached maximum precision
                    return c;
                double[] stretchedA = stretchNormalize0_5( pi.a );
                if( cand_length + 2 >= cand.length )
                {
                    // resize candidate stack
                    PolyInterval[] newCand = new PolyInterval[ 2 * cand.length ];
                    System.arraycopy( cand, 0, newCand, 0, cand.length );
                    cand = newCand;
                }
                cand[ cand_length++ ] = new PolyInterval( stretchedA, true, c, pi.u );
                cand[ cand_length++ ] = new PolyInterval( stretchedA, false, pi.l, c );
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

    /**
     * Computes 2^n*p(0.5*x).
     * @param a
     * @return
     */
    public double[] stretchNormalize0_5( double[] a )
    {
        double[] resultCoeffs = new double[ a.length ];
        resultCoeffs[ resultCoeffs.length - 1 ] = a[ resultCoeffs.length - 1 ];
        double multiplier = 2.0;
        for( int i = resultCoeffs.length - 2; i >= 0; i-- )
        {
            resultCoeffs[ i ] = a[ i ] * multiplier;
            multiplier *= 2.0;
        }
        
        return resultCoeffs;
    }
    
    /**
     * Computes p(1+x).
     * @param a
     * @return
     */
    private double[] shift1( double[] a )
    {
        double[] hornerCoeffs = new double[ a.length ];
        System.arraycopy( a, 0, hornerCoeffs, 0, a.length );
        
        for( int i = 1; i <= hornerCoeffs.length; i++ )
            for( int j = hornerCoeffs.length - 2; j >= i - 1; j-- )
                hornerCoeffs[ j ] = hornerCoeffs[ j ] + hornerCoeffs[ j + 1 ];
 
        return hornerCoeffs;
    }
    
    private double[] deflate0( double[] a )
    {
        if( a.length == 0 )
            return a;
        double[] deflated_a = new double[ a.length - 1 ];
        System.arraycopy( a, 1, deflated_a, 0, deflated_a.length );
        return deflated_a;
    }
    
    private double[] deflate( double[] a, double b )
    {
        if( a.length == 0 )
            return a;
        double[] result = new double[ a.length - 1 ];
        result[ result.length - 1 ] = a[ a.length - 1 ];
        for( int i = a.length - 3; i >= 0; i++ )
            result[ i ] = result[ i + 1 ] * b + a[ i + 1 ];
        return result;
    }
    
    private int descartesRuleOfSignReverseShift1( double[] a )
    {
        int signChanges = 0;
        double[] hornerCoeffs = new double[ a.length ];
            for( int i = 0; i < a.length; i++ )
                hornerCoeffs[ i ] = a[ a.length - i - 1 ];
        
        double lastNonZeroCoeff = Double.NaN;
        for( int i = 1; i <= a.length; i++ )
        {
            for( int j = hornerCoeffs.length - 2; j >= i - 1; j-- )
                hornerCoeffs[ j ] = hornerCoeffs[ j ] + hornerCoeffs[ j + 1 ];
            if( hornerCoeffs[ i - 1 ] != 0.0 )
            {
                if( hornerCoeffs[ i - 1 ] * lastNonZeroCoeff < 0.0 )
                    signChanges++;
                if( signChanges > 1 )
                    return signChanges;
                lastNonZeroCoeff = hornerCoeffs[ i - 1 ];
            }
        }
        
        return signChanges;
    }
    
/*    
    public static void main( String[] args )
    {
        UnivariatePolynomial p = new UnivariatePolynomial( 0.0, 0.05328577011823654, -0.2756684795022011, 0.0, -0.25 );
        double[] roots = new DescartesRootFinder( false ).findAllRoots( p );
        double[] root_values = new double[ roots.length ];
        for( int i = 0; i < roots.length; i++ )
            root_values[ i ] = p.evaluateAt( roots[ i ] );
        System.out.println( Arrays.toString( roots ) );
        System.out.println( Arrays.toString( root_values ) );
        System.out.println( p.evaluateAt(0.1873343809));
    }
 */ 
}
