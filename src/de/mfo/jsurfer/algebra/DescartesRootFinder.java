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
public class DescartesRootFinder implements RealRootFinder
{
    public static final double EPSILON = 1e-7;

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
                if( pi.shift )
                    pi.a = shift1( pi.a );
                if( pi.a[ 0 ] == 0.0 )
                {
                    double tmp_root = pi.l * bound2;
                    if( lowerBound <= tmp_root && tmp_root <= upperBound )
                        results[ results_length++ ] = tmp_root;
                    p = new UnivariatePolynomial( deflate( p.getCoeffs(), pi.l ) );
                }
                if( results_length == results.length )
                    break;
                int v = descartesRuleOfSignReverseShift1( pi.a );
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
                    double[] stretchedA = stretchNormalize0_5( pi.a );
                    if( cand_length + 2 >= cand.length )
                    {
                        // resize candidate stack
                        PolyInterval[] newCand = new PolyInterval[ 2 * cand.length ];
                        System.arraycopy( cand, 0, newCand, 0, cand.length );
                        cand = newCand;
                    }
                    if( c <= tub )
                        cand[ cand_length++ ] = new PolyInterval( stretchedA, true, c, pi.u );
                    if( c >= tlb )
                        cand[ cand_length++ ] = new PolyInterval( stretchedA, false, pi.l, c );
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
        cand[ cand_length++ ] = new PolyInterval( p.getCoeffs(), 0.0, 1.0 );
        while( cand_length != 0 )
        {
            PolyInterval pi = cand[ --cand_length ];
            if( pi.shift )
                pi.a = shift1( pi.a );
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
            int v = descartesRuleOfSignReverseShift1( pi.a );
            if( v == 1 )
            {
                double tmp_root = adjustIntervalAndBisect( p, pi.l, pi.u, tlb, tub ) * bound2;
                if( !java.lang.Double.isNaN( tmp_root ) )
                {
                    if( w == WhichRoot.LARGEST && !java.lang.Double.isNaN( tmp_result ) && tmp_result > tmp_root )
                        return tmp_result;
                    else
                        return tmp_root;
                }
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
                double[] stretchedA = stretchNormalize0_5( pi.a );
                if( cand_length + 2 >= cand.length )
                {
                    // resize candidate stack
                    PolyInterval[] newCand = new PolyInterval[ 2 * cand.length ];
                    System.arraycopy( cand, 0, newCand, 0, cand.length );
                    cand = newCand;
                }
                if( w == WhichRoot.SMALLEST )
                {
                    // search interval with smallest lower bound first
                    if( c <= tub )
                        cand[ cand_length++ ] = new PolyInterval( stretchedA, true, c, pi.u );
                    if( c >= tlb )
                        cand[ cand_length++ ] = new PolyInterval( stretchedA, false, pi.l, c );
                }
                else
                {
                    // search interval with largest lower bound first
                    if( c >= tlb )
                        cand[ cand_length++ ] = new PolyInterval( stretchedA, false, pi.l, c );
                    if( c <= tub )
                        cand[ cand_length++ ] = new PolyInterval( stretchedA, true, c, pi.u );
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

        if( fl * fu <= 0.0 )
            return bisect( p, lowerBound, upperBound, fl, fu );
        else
            return java.lang.Double.NaN;
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
        for( int i = a.length - 3; i >= 0; --i )
            result[ i ] = result[ i + 1 ] * b + a[ i + 1 ];
        return result;
    }

    private int descartesRuleOfSignReverseShift1( double[] a )
    {
        int signChanges = 0;
        double[] hornerCoeffs = new double[ a.length ];
            for( int i = 0; i < a.length; i++ )
                hornerCoeffs[ i ] = a[ a.length - i - 1 ];

        double lastNonZeroCoeff = java.lang.Double.NaN;
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
        if( hornerCoeffs[ 0 ] == 0.0 )
            ++signChanges;

        return signChanges;
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

    public static void main( String args[] )
    {
        double[] coeffs1 = { java.lang.Double.longBitsToDouble( 4612077628474687022L ),
                java.lang.Double.longBitsToDouble( -4609042608566403538L ),
                java.lang.Double.longBitsToDouble( 4607182418800017408L )
        };
        double[] coeffs2 = { java.lang.Double.longBitsToDouble( 4601663213748960204L ),
                java.lang.Double.longBitsToDouble( 4609180317257780979L ),
                java.lang.Double.longBitsToDouble( 4607182418800017408L )
        };
        UnivariatePolynomial p = new UnivariatePolynomial( coeffs2 );
        while( true )
            new DescartesRootFinder( false ).findFirstRootIn( p, -2, 2 );
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

// <editor-fold defaultstate="collapsed" desc="hacked BigDecimal version of DescartesRootFinder">
//class DescartesRootFinderBigDecimal
//{
//    class PolyInterval
//    {
//        public BigDecimal[] a;
//        public boolean shift;
//        public BigDecimal l;
//        public BigDecimal u;
//        public PolyInterval( BigDecimal[] a, BigDecimal l, BigDecimal u )
//        {
//            this.a = a;
//            this.shift = false;
//            this.l = l;
//            this.u = u;
//        }
//        public PolyInterval( BigDecimal[] a, boolean shift, BigDecimal l, BigDecimal u )
//        {
//            this.a = a;
//            this.shift = shift;
//            this.l = l;
//            this.u = u;
//        }
//    }
//
//    public DescartesRootFinderBigDecimal()
//    {
//    }
//
//    /**
//     * Find the smallest real root of p within lowerBound and upperBound (bounds may or may not be included).
//     * If no real root exists in this interval, BigDecimal.NaN ist returned.
//     * @param p
//     * @param lowerBound
//     * @param upperBound
//     * @return
//     */
//    public BigDecimal findFirstRootIn( BigDecimal[] a, BigDecimal lowerBound, BigDecimal upperBound )
//    {
//        a = shrink( a );
//
//        // move all roots in (lowerBound,upperBound) into (0,1)
//        a = stretch( shift( a, lowerBound ), upperBound.subtract( lowerBound ) );
//        if( a[ 0 ].equals( BigDecimal.ZERO ) )
//            return lowerBound;
//
//        // isolate and refine first root in (0,1)
//        PolyInterval[] cand = new PolyInterval[ 10 ];
//        int cand_length = 0;
//        cand[ cand_length++ ] = new PolyInterval( a, BigDecimal.ZERO, BigDecimal.ONE );
//        while( cand_length != 0 )
//        {
//            PolyInterval pi = cand[ --cand_length ];
//            if( pi.shift )
//                pi.a = shift1( pi.a );
//            if( pi.a[ 0 ].equals( BigDecimal.ZERO ) )
//                return pi.l;
//            int v = descartesRuleOfSignReverseShift1( pi.a );
//            if( v == 1 )
//                return bisect( a, pi.l, pi.u ).multiply( upperBound.subtract( lowerBound ) ).add( lowerBound );
//            else if( v > 1 )
//            {
//                // evtl. mehr als eine NST in (0,1) -> teile Interval (0,1) in zwei teile
//                BigDecimal c = new BigDecimal( 0.5 ).multiply( pi.l.add( pi.u ) );
//                if( c == pi.l ) // we have reached maximum precision
//                    return c;
//                BigDecimal[] stretchedA = stretchNormalize0_5( pi.a );
//                if( cand_length + 2 >= cand.length )
//                {
//                    // resize candidate stack
//                    PolyInterval[] newCand = new PolyInterval[ 2 * cand.length ];
//                    System.arraycopy( cand, 0, newCand, 0, cand.length );
//                    cand = newCand;
//                }
//                cand[ cand_length++ ] = new PolyInterval( stretchedA, true, c, pi.u );
//                cand[ cand_length++ ] = new PolyInterval( stretchedA, false, pi.l, c );
//            }
//        }
//        return BigDecimal.ZERO;
//    }
//
//    private static BigDecimal bisect( BigDecimal[] a, BigDecimal lowerBound, BigDecimal upperBound )
//    {
//        BigDecimal center = lowerBound;
//        BigDecimal fl = evaluateAt( a, lowerBound );
//        BigDecimal fu = evaluateAt( a, upperBound );
//
//        while( upperBound.subtract( lowerBound ).compareTo( new BigDecimal( 0.0000000000000001 ) ) > 0 )
//        {
//            center = new BigDecimal( 0.5 ).multiply( lowerBound.add( upperBound ) );
//            BigDecimal fc = evaluateAt( a, center );
//
//            if( fc.signum() * fl.signum() < 0 )
//            {
//                upperBound = center;
//                fu = fc;
//            }
//            else if( fc.equals( BigDecimal.ZERO ) )
//            {
//                break;
//            }
//            else
//            {
//                lowerBound = center;
//                fl = fc;
//            }
//        }
//        return center;
//    }
//
//    public BigDecimal[] shrink( BigDecimal[] a )
//    {
//        int degree = a.length - 1;
//        while( a[ degree ].equals( BigDecimal.ZERO ) && degree > 0 )
//            degree--;
//        if( degree != a.length - 1 )
//        {
//            BigDecimal[] result = new BigDecimal[ degree + 1 ];
//            System.arraycopy( a, 0, result, 0, result.length );
//            return result;
//        }
//        else
//            return a;
//    }
//
//    public static BigDecimal[] convertFromDouble( double[] a )
//    {
//        BigDecimal[] result = new BigDecimal[ a.length ];
//        for( int i = 0; i< a.length; ++i )
//            result[ i ] = new BigDecimal( a[ i ]);
//        return result;
//    }
//
//    public static BigDecimal evaluateAt( BigDecimal[] a, BigDecimal where )
//    {
//        BigDecimal result = a[ a.length - 1 ];
//        for( int i = a.length - 2; i >= 0; i-- )
//            result = result.multiply( where ).add( a[ i ] );
//        return result;
//    }
//
//    private BigDecimal[] shift( BigDecimal[] a, BigDecimal s )
//    {
//        // divide this polynomial repeatedly by ( x + a ) by using ruffini's rule
//        // and store the coeffs of the resulting polynomial and the remainder.
//        // the remainders give the coeffs of the shifted polynomial.
//        BigDecimal[] hornerCoeffs = new BigDecimal[ a.length ];
//            // perform x -> x + a
//            System.arraycopy( a , 0, hornerCoeffs, 0, a.length );
//
//        for( int i = 1; i <= a.length; i++ )
//            for( int j = hornerCoeffs.length - 2; j >= i - 1; j-- )
//                hornerCoeffs[ j ] = hornerCoeffs[ j ].add( s.multiply( hornerCoeffs[ j + 1 ] ) );
//
//        return hornerCoeffs;
//    }
//
//    public BigDecimal[] stretch( BigDecimal[] a, BigDecimal v )
//    {
//        BigDecimal[] resultCoeffs = new BigDecimal[ a.length ];
//        BigDecimal multiplier = BigDecimal.ONE;
//        for( int i = 0; i < resultCoeffs.length; i++ )
//        {
//            resultCoeffs[ i ] = a[ i ].multiply( multiplier );
//            multiplier = multiplier.multiply( v );
//        }
//        return resultCoeffs;
//    }
//
//    /**
//     * Computes 2^n*p(0.5*x).
//     * @param a
//     * @return
//     */
//    public BigDecimal[] stretchNormalize0_5( BigDecimal[] a )
//    {
//        BigDecimal[] resultCoeffs = new BigDecimal[ a.length ];
//        resultCoeffs[ resultCoeffs.length - 1 ] = a[ resultCoeffs.length - 1 ];
//        BigDecimal multiplier = new BigDecimal( 2.0 );
//        for( int i = resultCoeffs.length - 2; i >= 0; i-- )
//        {
//            resultCoeffs[ i ] = a[ i ].multiply( multiplier );
//            multiplier = multiplier.multiply( new BigDecimal( 2.0 ) );
//        }
//
//        return resultCoeffs;
//    }
//
//    /**
//     * Computes p(1+x).
//     * @param a
//     * @return
//     */
//    private BigDecimal[] shift1( BigDecimal[] a )
//    {
//        BigDecimal[] hornerCoeffs = new BigDecimal[ a.length ];
//        System.arraycopy( a, 0, hornerCoeffs, 0, a.length );
//
//        for( int i = 1; i <= hornerCoeffs.length; i++ )
//            for( int j = hornerCoeffs.length - 2; j >= i - 1; j-- )
//                hornerCoeffs[ j ] = hornerCoeffs[ j ].add( hornerCoeffs[ j + 1 ] );
//
//        return hornerCoeffs;
//    }
//
//    private BigDecimal[] deflate0( BigDecimal[] a )
//    {
//        if( a.length == 0 )
//            return a;
//        BigDecimal[] deflated_a = new BigDecimal[ a.length - 1 ];
//        System.arraycopy( a, 1, deflated_a, 0, deflated_a.length );
//        return deflated_a;
//    }
//
//    private BigDecimal[] deflate( BigDecimal[] a, BigDecimal b )
//    {
//        if( a.length == 0 )
//            return a;
//        BigDecimal[] result = new BigDecimal[ a.length - 1 ];
//        result[ result.length - 1 ] = a[ a.length - 1 ];
//        for( int i = a.length - 3; i >= 0; i++ )
//            result[ i ] = result[ i + 1 ].multiply( b ).add( a[ i + 1 ] );
//        return result;
//    }
//
//    private int descartesRuleOfSignReverseShift1( BigDecimal[] a )
//    {
//        int signChanges = 0;
//        BigDecimal[] hornerCoeffs = new BigDecimal[ a.length ];
//            for( int i = 0; i < a.length; i++ )
//                hornerCoeffs[ i ] = a[ a.length - i - 1 ];
//
//        BigDecimal lastNonZeroCoeff = BigDecimal.ZERO;
//        for( int i = 1; i <= a.length; i++ )
//        {
//            for( int j = hornerCoeffs.length - 2; j >= i - 1; j-- )
//                hornerCoeffs[ j ] = hornerCoeffs[ j ].add( hornerCoeffs[ j + 1 ] );
//            if( !hornerCoeffs[ i - 1 ].equals( BigDecimal.ZERO ) )
//            {
//                if( hornerCoeffs[ i - 1 ].signum() * lastNonZeroCoeff.signum() < 0 )
//                    signChanges++;
//                if( signChanges > 1 )
//                    return signChanges;
//                lastNonZeroCoeff = hornerCoeffs[ i - 1 ];
//            }
//        }
//
//        return signChanges;
//    }
//
///*
//    public static void main( String[] args )
//    {
//        UnivariatePolynomial p = new UnivariatePolynomial( 0.0, 0.05328577011823654, -0.2756684795022011, 0.0, -0.25 );
//        BigDecimal[] roots = new DescartesRootFinder( false ).findAllRoots( p );
//        BigDecimal[] root_values = new BigDecimal[ roots.length ];
//        for( int i = 0; i < roots.length; i++ )
//            root_values[ i ] = p.evaluateAt( roots[ i ] );
//        System.out.println( Arrays.toString( roots ) );
//        System.out.println( Arrays.toString( root_values ) );
//        System.out.println( p.evaluateAt(0.1873343809));
//    }
// */
//}
// </editor-fold>