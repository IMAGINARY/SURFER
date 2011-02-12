/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class SturmChainRootFinder implements RealRootFinder
{
    /**
     * Find all real roots of p.
     * @param p
     * @return
     */
    public double[] findAllRoots( UnivariatePolynomial p )
    {
        assert false;
        return null;
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
        assert false;
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
        return Solve.solve( new MyPolynomial( p ), 1, lowerBound, upperBound, 20 );
    }
    
    
}

interface Function {
    /**
     * Evaluates a function at a given point
     * 
     * @param x
     *            the point, at which the function has to be evaluated
     * @return the function value at <code>x</code>
     */
    public double valueAt(double x);
}

abstract class Polynomial implements Function {
    /**
     * Returns the coefficients array of the polynomial
     * 
     * @return the coefficients array of the polynomial, where
     *         <code>result[n]</code> is the coefficient before X^n
     */
    public abstract double[] toArray();

    /**
     * Calculates the first derivative of the polynomial
     * 
     * @return the derivation
     */
    public abstract Polynomial diff();

    /**
     * Calculates the remainder of the polynomial division of <code>this</code>
     * by <code>other</code>
     * 
     * @param other
     *            the divisor (must not be constant)
     * @return the remainder of the polynomial division
     */
    public abstract Polynomial mod(Polynomial other);

    public abstract Polynomial div(Polynomial other);    
    
    /**
     * Multiplies the polynomial by a real scalar
     * 
     * @param scalar
     *            the scalar to multiply the polynomial by
     * @return the multiplied polynomial
     */
    public abstract Polynomial multiply(double scalar);

    /**
     * Determines the degree of the polynomial
     * 
     * @return the degree of the polynomial, where the degree of the zero
     *         polynomial is defined as -1
     */
    public abstract int degree();
    
    public static Polynomial gcd( Polynomial a, Polynomial b )
    {
        while( b.degree() != -1 )
        {
            Polynomial t = b;
            b = a.mod( b );
            a = t;
        }
        return a;
    }
}

class MyPolynomial extends Polynomial
{
    UnivariatePolynomial p;
    
    public MyPolynomial( UnivariatePolynomial p )
    {
        this.p = p;
    }
    
    public double valueAt( double x )
    {
        return p.evaluateAt( x );
    }
    
    public double[] toArray()
    {
        return p.getCoeffs();
    }

    public Polynomial diff()
    {
        return new MyPolynomial( p.derive() );
    }

    private double[] reduce(double[] a, int degA, double[] b, int degB) {
        int degDiff = degA - degB;

        double[] result = new double[degA];
        for (int i = degA - 1; i >= degDiff; i--) {
            result[i] = a[i] - b[i - degDiff] / b[degB] * a[degA];
        }

        for (int i = 0; i < degDiff; i++) {
            result[i] = a[i];
        }
        return result;
    }

    private double[] mod(double[] a, int degA, double[] b, int degB) {
        if (degB < 1)
        { // the illegal case
            if( degB == -1 )
            {
                throw new IllegalArgumentException(
                    "Cannot divide by constant polynomials");
            }
            else
            {
                double[] result = new double[ 1 ];
                result[ 0 ] = 0.0;
                return result;
            }
        } else if (degA < degB) { // the basic case
            return a;
        } else { // the recursion case
            // reduce a by b
            double[] result = reduce(a, degA, b, degB);

            // calculate the degree of the result
            int newDeg = degA - 1;
            while (newDeg >= 0 && result[newDeg] == 0) {
                newDeg--;
            }

            // do recursion
            return mod(result, newDeg, b, degB);
        }
    }

    public Polynomial mod(Polynomial other) {
        return new MyPolynomial( new UnivariatePolynomial( mod( toArray(), degree(), other.toArray(), other.degree() ) ) );
    }
    
    public Polynomial div(Polynomial other) {
        return new MyPolynomial( new UnivariatePolynomial( reduce( toArray(), degree(), other.toArray(), other.degree() ) ) );
    }

    public Polynomial multiply(double scalar)
    {
        return new MyPolynomial( p.mult( scalar ) );
    }

    public int degree()
    {
        double[] coeffs = p.getCoeffs();
        int deg = -1;
        for( int i = 0; i < coeffs.length; i++ )
            if( coeffs[ i ] != 0.0 )
                deg++;
        return deg;
    }
}

class Solve {

    private static final double FLOATING_POINT_PRECISION = 0;

    /**
     * Search zeroes of a polynomial function by executing a bisection algorithm
     * using Sturm's theorem
     * 
     * @param sturm
     *            the function, whose zeroes are searched
     * @param num
     *            the number of the wanted zero; counting starts from
     *            <code>lower</code>
     * @param lower
     *            lower bound of the interval, in which the zero is searched
     * @param upper
     *            upper bound of the interval, in which the zero is searched
     * @param precision
     *            tolerance in comparing function values
     * @param iterations
     *            maximum number of iterations (the more iterations, the more
     *            precise the result); the algorithm stops before that maximum
     *            number, when it reaches sufficient precision (machine
     *            precision)
     * @return the zero
     */
    public static double solve(Polynomial poly, int num, double lower,
            double upper, double precision, int iterations) {
        return bisection(calculateSturm(poly), num, lower, upper, precision,
                iterations);
    }

    /**
     * Search zeroes of a polynomial function by executing a bisection algorithm
     * using Sturm's theorem
     * 
     * @param sturm
     *            the function, whose zeroes are searched
     * @param num
     *            the number of the wanted zero; counting starts from
     *            <code>lower</code>
     * @param lower
     *            lower bound of the interval, in which the zero is searched
     * @param upper
     *            upper bound of the interval, in which the zero is searched
     * @param iterations
     *            maximum number of iterations (the more iterations, the more
     *            precise the result); the algorithm stops before that maximum
     *            number, when it reaches sufficient precision (machine
     *            precision)
     * @return the zero
     */
    public static double solve(Polynomial poly, int num, double lower,
            double upper, int iterations) {
        return bisection(calculateSturm(poly), num, lower, upper,
                FLOATING_POINT_PRECISION, iterations);
    }

    /**
     * Sturm's "w" function for counting zeroes
     * 
     * @param sturm
     *            the Sturm chain as array
     * @param x
     *            where to evaluate the "w" function
     * @param precision
     *            tolerance in comparing function values
     * @return the result of the "w" function defined by Sturm
     */
    private static int w(Polynomial[] sturm, double x, double precision) {
        int signChanges = 0;
        int lastNonZero = 0;
        // run through the array
        for (int i = 1; i < sturm.length; i++) {
            if (Math.abs(sturm[i].valueAt(x)) > precision) {
                // compare the sign to the last non-zero sign
                if (sturm[lastNonZero].valueAt(x) * sturm[i].valueAt(x) < 0) {
                    // sign change found: count up
                    signChanges++;
                }
                lastNonZero = i;
            }
        }
        return signChanges;
    }

    /**
     * Search zeroes of a polynomial function by executing a bisection algorithm
     * using Sturm's theorem
     * 
     * @param sturm
     *            the Sturm chain of the function
     * @param num
     *            the number of the wanted zero; counting starts from
     *            <code>lower</code>
     * @param lower
     *            lower bound of the interval, in which the zero is searched
     * @param upper
     *            upper bound of the interval, in which the zero is searched
     * @param precision
     *            tolerance in comparing function values
     * @param iterations
     *            maximum number of iterations (the more iterations, the more
     *            precise the result); the algorithm stops before that maximum
     *            number, when it reaches sufficient precision (machine
     *            precision)
     * @return the zero
     */
    private static double bisection(Polynomial[] sturm, int num, double lower,
            double upper, double precision, int iterations) {
        
        Polynomial p = sturm[ sturm.length - 1 ];
        
        // define the point where to start counting the zeroes
        double t = lower;

        // do the maximum number or iterations (if necessary)
        for (int i = 0; i < iterations; i++) {
            // determine the middle of the interval
            double c = (upper + lower) / 2;

            // Check, if we have already reached machine precision
            if (upper <= lower || c <= lower || c >= upper) {
                return lower;
            }

            // Left or right interval?
            // Are there less than "num" zeroes between t and c?
            int sign_changes = w(sturm, t, precision) - w(sturm, c, precision);
            if ( sign_changes < num) {
                // right
                lower = c;
            } else {
                // left
                upper = c;
            }
            
            double fl = p.valueAt( lower );
            double fu = p.valueAt( upper );
            if( fl * fu < 0.0 )
                return bisect( p, lower, upper, fl, fu );
        }
        // the wanted zero lies in the intervall [lower, upper],
        // so the middle of this interval might be a good guess
        
        if (w(sturm, upper, precision) - w(sturm, t, precision) == 0 )
            return Double.NaN;
        else
            return (upper + lower) / 2;
    }
    
    private static double bisect( Polynomial p, double lowerBound, double upperBound, double fl, double fu )
    {
        double center = lowerBound;
        double old_center = Double.NaN;
        double[] a = p.toArray();
        
        //for( int it = 14; it > 0; it-- ) // 14 iterations work quite good in most cases
        while( center != old_center )
        {
            old_center = center;
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
    }
    
    /**
     * Calculates the Sturm chain to a given polynomial
     * 
     * @param function
     *            the polynomial function
     * @return the Sturm chain of <code>function</code> as array
     */
    public static Polynomial[] calculateSturm(Polynomial function) {
        List<Polynomial> sturm = new LinkedList<Polynomial>();
        
        Polynomial gcd = Polynomial.gcd( function, function.diff() );
        
        if( gcd.degree() > 0 )
            // Polynomial not squarefree!
            function = function.div( gcd );

        // add the original function and its derivation
        sturm.add(0, function);
        sturm.add(0, function.diff());

        // iteratively perform polynomial divison
        while (sturm.get(0).degree() > 0) {
            sturm.add(0, sturm.get(1).mod(sturm.get(0)).multiply(-1));
        }

        // convert the list to an array for efficiency purposes
        Polynomial[] result = new MyPolynomial[sturm.size()];
        int i = 0;
        for (Polynomial poly : sturm) {
            result[i] = poly;
            i++;
        }
        return result;
    }
}
