/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 * Calculates the roots of polynomials up to degree 4 by closed form solutions.
 * Implementation is a modified version of the code presented in Graphics Gems
 * (http://read.pudn.com/downloads21/sourcecode/graph/71499/gems/Roots3And4.c__.htm).
 * The solver reports m-fold roots m times.
 * @author stussak
 */
public class ClosedFormRootFinder implements RealRootFinder
{
    public double[] findAllRoots( UnivariatePolynomial p )
    {
        switch( p.degree() )
        {
            case 0:
                return new double[ 0 ];
            case 1:
                return solveLinear( p );
            case 2:
                return solveQuadric( p );
            case 3:
                return solveCubic( p );
            case 4:
                return solveQuartic( p );
            default:
                throw new IllegalArgumentException( "no closed form solution exists for polynomials of degree > 4" );
        }
    }

    public double[] findAllRootsIn( UnivariatePolynomial p, double lowerBound, double upperBound )
    {
        return clip( findAllRoots( p ), lowerBound, upperBound );
    }

    public double findFirstRootIn( UnivariatePolynomial p, double lowerBound, double upperBound )
    {
        double[] roots = clip( findAllRoots( p ), lowerBound, upperBound );
        return roots.length > 0 ? roots[ 0 ] : java.lang.Double.NaN;
    }

    private double[] solveLinear( UnivariatePolynomial poly )
    {
        double[] roots = new double[ 1 ];
        roots[ 0 ] = -poly.getCoeff( 0 ) / poly.getCoeff( 1 );
        return roots;
    }

    private double[] solveQuadric( UnivariatePolynomial poly )
    {
        /* normal form: x^2 + px + q = 0 */

        double p = poly.getCoeff( 1 ) / (2 * poly.getCoeff( 2 ) );
        double q = poly.getCoeff( 0 ) / poly.getCoeff( 2 );

        double D = p * p - q;
        int num_roots = 0;

        if( isZero( D ) )
            return solutions( -p, -p );
        else if (D < 0)
            return solutions();
        else // D > 0
        {
            double sqrt_D = Math.sqrt(D);
            return solutions( sqrt_D - p, - sqrt_D - p );
        }
    }

    double[] solveCubic( UnivariatePolynomial poly )
    {
        /* normal form: x^3 + Ax^2 + Bx + C = 0 */

        double A = poly.getCoeff( 2 ) / poly.getCoeff( 3 );
        double B = poly.getCoeff( 1 ) / poly.getCoeff( 3 );
        double C = poly.getCoeff( 0 ) / poly.getCoeff( 3 );

        /*  substitute x = y - A/3 to eliminate quadric term:
        x^3 +px + q = 0 */

        double sq_A = A * A;
        double p = 1.0/3 * (- 1.0/3 * sq_A + B);
        double q = 1.0/2 * (2.0/27 * A * sq_A - 1.0/3 * A * B + C);

        /* use Cardano's formula */

        double cb_p = p * p * p;
        double D = q * q + cb_p;
        double[] roots;

        if( isZero( D ) )
        {
            if( isZero( q ) ) /* one triple solution */
                roots = solutions( 0.0, 0.0, 0.0 );
            else /* one single and one double solution */
            {
                double u = cbrt(-q );
                roots = solutions( 2 * u, -u, -u );
            }
        }
        else if( D < 0 ) /* Casus irreducibilis: three real solutions */
        {
            double phi = 1.0/3 * Math.acos(-q / Math.sqrt(-cb_p));
            double t = 2 * Math.sqrt(-p);
            roots = solutions( t * Math.cos(phi), - t * Math.cos(phi + Math.PI / 3 ), - t * Math.cos(phi - Math.PI / 3 ));
        }
        else /* one real solution */
        {
            double sqrt_D = Math.sqrt(D);
            double u = cbrt(sqrt_D - q);
            roots = solutions( cbrt( sqrt_D - q ) - cbrt( sqrt_D + q ) );
        }

        /* resubstitute */

        double sub = 1.0/3 * A;

        for( int i = 0; i < roots.length; ++i )
            roots[ i ] -= sub;

        return roots;
    }
    
    double[] solveQuartic( UnivariatePolynomial poly )
    {
        /* normal form: x^4 + Ax^3 + Bx^2 + Cx + D = 0 */

        double A = poly.getCoeff( 3 ) / poly.getCoeff( 4 );
        double B = poly.getCoeff( 2 ) / poly.getCoeff( 4 );
        double C = poly.getCoeff( 1 ) / poly.getCoeff( 4 );
        double D = poly.getCoeff( 0 ) / poly.getCoeff( 4 );

        /*  substitute x = y - A/4 to eliminate cubic term:
        x^4 + px^2 + qx + r = 0 */

        double sq_A = A * A;
        double p = - 3.0/8 * sq_A + B;
        double q = 1.0/8 * sq_A * A - 1.0/2 * A * B + C;
        double r = - 3.0/256*sq_A*sq_A + 1.0/16*sq_A*B - 1.0/4*A*C + D;

        double[] roots;
        if( isZero( r ) )
        {
            /* no absolute term: y(y^3 + py + q) = 0 */
            UnivariatePolynomial cubic = new UnivariatePolynomial( q, p, 0, 1 );

            roots = solveCubic( cubic );
            roots = solutions( roots[ 0 ], roots[ 1 ], roots[ 2 ], 0 );
        }
        else
        {
            /* solve the resolvent cubic ... */
            UnivariatePolynomial cubic = new UnivariatePolynomial( 1.0/2 * r * p - 1.0/8 * q * q, -r, - 1.0/2 * p, 1 );
            roots = solveCubic( cubic );

            /* ... and take the one real solution ... */

            double z = roots[ 0 ];

            /* ... to build two quadric equations */

            double u = z * z - r;
            double v = 2 * z - p;

            if (isZero(u))
                u = 0;
            else if (u > 0)
                u = Math.sqrt(u);
            else
                return solutions();

            if (isZero(v))
                v = 0;
            else if (v > 0)
                v = Math.sqrt(v);
            else
                return solutions();

            UnivariatePolynomial quadric1 = new UnivariatePolynomial( z - u, q < 0 ? -v : v, 1 );
            double[] roots1 = solveQuadric( quadric1 );

            UnivariatePolynomial quadric2 = new UnivariatePolynomial( z + u, q < 0 ? v : -v, 1 );
            double[] roots2 = solveQuadric( quadric2 );

            roots = new double[ roots1.length + roots2.length ];
            System.arraycopy( roots1, 0, roots, 0, roots1.length );
            System.arraycopy( roots2, 0, roots, roots1.length, roots2.length );
        }

        /* resubstitute */

        double sub = 1.0/4 * A;

        for( int i = 0; i < roots.length; ++i )
            roots[ i ] -= sub;

        java.util.Arrays.sort( roots );
        return roots;
    }

    private static double cbrt( double x ) { return x >= 0.0 ? Math.pow( x , 1.0/3.0 ) : -Math.pow(-x, 1.0/3.0); }

    private static boolean isZero( double d ) { return -1e-20 < d && d < 1e-20; }
    //{ return -1e-9 < d && d < 1e-9; }

    public static double[] solutions( double ... s ) { java.util.Arrays.sort( s ); return s; } // simplified array initialization

    private static double[] clip( double[] d, double l, double u )
    {
        double[] tmp_result = new double[ d.length ];
        int length = 0;
        for( int i = 0; i < d.length; ++i )
            if( l <= d[ i ] && d[ i ] <= u )
                tmp_result[ length++ ] = d[ i ];
        if( length == tmp_result.length )
            return tmp_result;
        else
        {
            double[] result = new double[ length ];
            System.arraycopy( tmp_result, 0, result, 0, length );
            return result;
        }
    }
}
