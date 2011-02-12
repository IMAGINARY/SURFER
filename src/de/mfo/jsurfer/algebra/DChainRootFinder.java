/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class DChainRootFinder implements RealRootFinder
{
    private class SearchStruct
    {
        UnivariatePolynomial poly;
        double smallestPossibleRoot;
        double valueAtSmallestPossibleRoot;
        
        public SearchStruct( UnivariatePolynomial poly, double smallestPossibleRoot )
        {
            this.poly = poly;
            this.smallestPossibleRoot = smallestPossibleRoot;
            this.valueAtSmallestPossibleRoot = poly.evaluateAt( smallestPossibleRoot );
        }
    }
    
    public double[] findAllRoots( UnivariatePolynomial p )
    {
        double rootBound = 0.0;
        for( int i = 0; i < p.degree(); i++ )
            rootBound = Math.max( rootBound, 2 * Math.pow( Math.abs( p.getCoeff( i ) / p.getCoeff( p.degree() ) ), 1.0 / ( p.degree() - i ) ) );
        
        return findAllRootsIn( p, -rootBound, rootBound );
        //return findAllRootsIn( p, -Double.MAX_VALUE, Double.MAX_VALUE );
    }

    public double findFirstRootIn( UnivariatePolynomial p, double lowerBound, double upperBound )
    {
        switch( p.degree() )
        {
            case 0:
                return Double.NaN;
            case 1:
            {
                double[] roots = _solveLinear( p.getCoeff( 0 ), p.getCoeff( 1 ), lowerBound, upperBound );
                return roots.length > 0 ? roots[ 0 ] : Double.NaN;
            }
            case 2:
            {
                double[] roots = _solveQuadratic( p.getCoeff( 0 ), p.getCoeff( 1 ), p.getCoeff( 2 ), lowerBound, upperBound );
                return roots.length > 0 ? roots[ 0 ] : Double.NaN;
            }
            default:
            {
                // use special search algorithm, which calculates the roots
                // of the derivatives when needed and not always as the findAllRootsIn algorithm does
                double[] root = new double[ 1 ];
                SearchStruct[] searchStruct = new SearchStruct[ p.degree() ];
                for( int i = 0; i < searchStruct.length; i++ )
                    searchStruct[ i ] = new SearchStruct( ( i == 0 ) ? p : searchStruct[ i - 1 ].poly.derive(), lowerBound );
                if( _findFirstRootIn( searchStruct, searchStruct.length - 1, upperBound, upperBound, root ) )
                    return root[ 0 ];
                else
                    return Double.NaN;
 
//                // another possibility: calculate all roots in interval and choose smallest
//                double[] roots = findAllRootsIn( p, lowerBound, upperBound );
//                return roots.length > 0 ? roots[ 0 ] : Double.NaN;
            }
        }
    }
    
    private boolean _findFirstRootIn( SearchStruct[] searchStructs, int startWith, double searchUpperBound, double largestPossibleRoot, double[] root )
    {
        SearchStruct ss = searchStructs[ startWith ];
        double divider = Double.NaN;
        
        // calculate root of current polynomial in interval [lowerBound,upperBound)
        if( ss.poly.degree() == 1 )
        {
            divider = - ss.poly.getCoeff( 0 ) / ss.poly.getCoeff( 1 );
            if( divider < ss.smallestPossibleRoot || divider >= searchUpperBound )
                divider = Double.NaN;
        }
        else
        {
            double fl = ss.valueAtSmallestPossibleRoot;
            double fu = ss.poly.evaluateAt( searchUpperBound );
            
            if( fl * fu < 0.0 )
                // sign change in interval -> interval contains roots -> refine root with bisection
                divider = bisect( ss.poly, ss.smallestPossibleRoot, searchUpperBound, fl, fu );
            else if( fl == 0.0 )
                // root on lower interval bound
                divider = ss.smallestPossibleRoot;
            
            ss.smallestPossibleRoot = searchUpperBound;
            ss.valueAtSmallestPossibleRoot = fu;
        }
        
        // if base case isn't reached, search for root of lower derivate
        boolean dividerIsNotNaN = !Double.isNaN( divider );
        if( startWith == 0 && dividerIsNotNaN )
        {
            root[ 0 ] = divider;
            return true;
        }
        else
        {
                return ( dividerIsNotNaN && _findFirstRootIn( searchStructs, startWith - 1, divider, largestPossibleRoot, root ) )
                    || ( ( searchUpperBound == largestPossibleRoot && startWith != 0 ) && _findFirstRootIn( searchStructs, startWith - 1, largestPossibleRoot, largestPossibleRoot, root ) );
        }
    }

    public double[] findAllRootsIn( UnivariatePolynomial p, double lowerBound, double upperBound )
    {
        double[] result;
        switch( p.degree() )
        {
            case 0:
                result = new double[ 0 ];
                break;
            case 1:
                result = _solveLinear( p.getCoeff( 0 ), p.getCoeff( 1 ), lowerBound, upperBound );
                break;
            case 2:
                result = _solveQuadratic( p.getCoeff( 0 ), p.getCoeff( 1 ), p.getCoeff( 2 ), lowerBound, upperBound );
                break;
            default:
                int[] resultLength = new int[ 1 ];
                double[] tmpResult = _findAllRootsIn( p, lowerBound, upperBound, resultLength );
                result = new double[ resultLength[ 0 ] - 2 ];
                System.arraycopy( tmpResult, 1, result, 0, resultLength[ 0 ] - 2 );
                break;
        }
        return result;
    }
    
    private double[] _findAllRootsIn( UnivariatePolynomial p, double lowerBound, double upperBound, int[] resultLength )
    {
        double[] roots;
        if( p.degree() == 1 )
        {
            // base case: solve linear polynomial
            double root = - p.getCoeff( 0 ) / p.getCoeff( 1 );
            roots = new double[ 3 ];
            if( lowerBound < root && root < upperBound )
            {
                roots[ 0 ] = lowerBound;
                roots[ 1 ] = root;
                roots[ 2 ] = upperBound;
                resultLength[ 0 ] = 3;
            }
            else
            {
                roots[ 0 ] = lowerBound;
                roots[ 1 ] = upperBound;
                resultLength[ 0 ] = 2;
            }
        }
        else
        {
            // find roots between roots of derivative
                        
            // calculate derivative und its roots
            UnivariatePolynomial derivative = p.derive();
            int[] dRootsLength = new int[ 1 ];
            double[] dRoots = _findAllRootsIn( derivative, lowerBound, upperBound, dRootsLength );
            roots = new double[ dRootsLength[ 0 ] + 1 ];
            roots[ 0 ] = lowerBound;
            resultLength[ 0 ] = 1;
            
            // analyze each interval between roots of derivative for roots of p
            double fu = p.evaluateAt( dRoots[ 0 ] );
            double fl;
            for( int i = 1; i < dRootsLength[ 0 ]; i++ )
            {
                fl = fu;
                fu = p.evaluateAt( dRoots[ i ] );
                
                if( fl * fu < 0.0 )
                    // sign change in interval -> interval contains roots -> refine root with bisection
                    roots[ resultLength[ 0 ]++ ] = bisect( p, dRoots[ i - 1 ], dRoots[ i ], fl, fu );
                else if( fl == 0.0 )
                    // root on lower interval bound
                    roots[ resultLength[ 0 ]++ ] = dRoots[ i - 1 ];
            }
            roots[ resultLength[ 0 ]++ ] = upperBound;
        }
        return roots;
    }

    private double[] _solveLinear( double a0, double a1, double lowerBound, double upperBound )
    {
        double[] result;
        double root = - a0 / a1;
        if( lowerBound < root && root < upperBound )
        {
            result = new double[ 1 ];
            result[ 0 ] = root;
        }
        else
        {
            result = new double[ 0 ];
        }        
        return result;
    }
    
    private double[] _solveQuadratic( double a0, double a1, double a2, double lowerBound, double upperBound )
    {
        double diskriminante = a1 * a1 - 4.0 * a2 * a0;
        if( diskriminante >= 0.0f )
        {
            double q = -0.5 * ( a1 + ( a1 < 0.0 ? - 1.0 : 1.0 ) * Math.sqrt( diskriminante ) );

            double r1 = q / a2;
            double r2 = a0 / q;

            if( r1 > r2 )
            {
                double tmp = r1;
                r1 = r2;
                r2 = tmp;
            }

            int rootNum = 0;
            if( lowerBound < r1 && r1 < upperBound )
                rootNum++;
            if( lowerBound < r2 && r2 < upperBound )
                rootNum++;
            
            double[] result = new double[ rootNum ];
            rootNum = 0;
            if( lowerBound < r1 && r1 < upperBound )
                result[ rootNum++ ] = r1;
            if( lowerBound < r2 && r2 < upperBound )
                result[ rootNum++ ] = r2;

            return result;
        }
        else
        {
            return new double[ 0 ];
        }
    }
    
    private double newton( UnivariatePolynomial p, UnivariatePolynomial pDiff, double x )
    {
        double epsilon = 2.22045e-016; // double precision machine epsilon
        double xOld = x * 2.0 + 1;
        for( int i = 0; i < 99; i++ )
        {
            xOld = x;
            double f = p.evaluateAt( x );
            x -= f / pDiff.evaluateAt( x );
            if( Math.abs( x - xOld ) < epsilon || Math.abs( f ) < epsilon )
                break;
        }
        return x;
    }
   
    private double bisect( UnivariatePolynomial p, double lowerBound, double upperBound, double fl, double fu )
    {
        double epsilon = 2.22045e-016; // double precision machine epsilon
        
        double center = lowerBound;
        double[] a = p.getCoeffs();
        
        //int numOfIterations = ( int ) ( Math.log( ( upperBound - lowerBound ) / epsilon ) / Math.log( 2.0 ) );
        
        // guaranteed convergence criteria for newton iteration (from "Fundamental Problems of Algorithmic Algebra", 2000, p. 184)
        int m = p.degree();
        double M = 0.0;
        for( int i = 0; i < a.length; i++ )
        {
            double absCoeff = Math.abs( a[ i ] );
            if( absCoeff > M )
                absCoeff = M;
        }
        double delta = ( Math.pow( m, -3.0 * m - 9.0 ) * Math.pow( 1.0 + M, -6.0 * m ) );
        int numOfIterations = Math.min( ( int ) ( Math.log( ( upperBound - lowerBound ) / epsilon ) / Math.log( 2.0 ) ), ( int ) ( Math.log( delta / epsilon ) / Math.log( 2.0 ) ) );
        
        //numOfIterations = 14;
        for( ; numOfIterations > 0; numOfIterations-- )
        {
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
                return center;
            }
            else
            {
                lowerBound = center;
                fl = fc;
            }
        }
        return center;
        //return newton( p, p.derive(), center );
    }
}
