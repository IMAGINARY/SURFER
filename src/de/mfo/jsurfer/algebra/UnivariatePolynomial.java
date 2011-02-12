/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class UnivariatePolynomial {

    private double[] a;
    private int degree;
    
    private UnivariatePolynomial() {}
    
    public UnivariatePolynomial( int degree )
    {
        this.a = new double[ degree + 1 ];
        this.degree = degree;
    }
    
    public UnivariatePolynomial( double... coeffs )
    {
        this.setCoeffs( coeffs, false );
    }

    public UnivariatePolynomial( double[] coeffs, boolean copy )
    {
        this.setCoeffs( coeffs, copy );
    }
    
    public UnivariatePolynomial( UnivariatePolynomial p )
    {
        this.setCoeffs( p.a, true );
    }
    
    public void setCoeff( int which, double value )
    {
        this.a[ which ] = value;
    }

    public void setCoeffs( double[] coeffs, boolean copy )
    {
        if( copy )
        {
            this.a = new double[ coeffs.length ];
            System.arraycopy( coeffs, 0, this.a, 0, coeffs.length );
        }
        else
        {
            this.a = coeffs;
        }
        this.degree = coeffs.length - 1;
    }
    
    public double getCoeff( int which )
    {
        return this.a[ which ];
    }
    
    public double[] getCoeffs()
    {
        double[] result = new double[ a.length ];
        System.arraycopy( a, 0, result, 0, a.length );
        return result;
    }
    
    public int degree()
    {
        return this.degree;
    }
       
    public UnivariatePolynomial neg()
    {
        UnivariatePolynomial result = new UnivariatePolynomial( this.degree );
        for( int i = 0; i < this.a.length; i++ )
            result.a[ i ] = -this.a[ i ];
        return result;
    }
    
    public UnivariatePolynomial add( UnivariatePolynomial p    )
    {
        UnivariatePolynomial result;
        UnivariatePolynomial summand;
        if( this.degree > p.degree )
        {
            result = new UnivariatePolynomial( this );
            summand = p;
        }
        else
        {
            result = new UnivariatePolynomial( p );
            summand = this;            
        }
        for( int i = 0; i < summand.a.length; i++ )
                result.a[ i ] += summand.a[ i ];
        return result;
    }

    public UnivariatePolynomial sub( UnivariatePolynomial p    )
    {
        return this.add( p.neg() );
    }
    
    public UnivariatePolynomial mult( UnivariatePolynomial p    )
    {
        UnivariatePolynomial result = new UnivariatePolynomial( this.degree + p.degree );
        for( int i = 0; i < this.a.length; i++ )
            for( int j = 0; j < p.a.length; j++ )
                result.a[ i + j ] += this.a[ i ] * p.a[ j ];
        return result;
    }
    
    public UnivariatePolynomial mult( double d )
    {
        UnivariatePolynomial result = new UnivariatePolynomial( this.degree );
        for( int i = 0; i < this.a.length; i++ )
            result.a[ i ] = this.a[ i ] * d;
        return result;
    }    

    public UnivariatePolynomial pow( int exp )
    {
        if( exp == 0 )
        {
            return new UnivariatePolynomial( 1.0 );
        }
        else
        {
            UnivariatePolynomial result = this;
            UnivariatePolynomial x = this;
            
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
    
    public UnivariatePolynomial div( double d )
    {
        UnivariatePolynomial result = new UnivariatePolynomial( this );
        for( int i = 0; i < result.a.length; i++ )
            result.a[ i ] = result.a[ i ] / d;
        return result;
    }

    private double[] _reduce(double[] a, int degA, double[] b, int degB) {
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

    private double[] _mod(double[] a, int degA, double[] b, int degB) {
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
            double[] result = _reduce(a, degA, b, degB);

            // calculate the degree of the result
            int newDeg = degA - 1;
            while (newDeg >= 0 && result[newDeg] == 0) {
                newDeg--;
            }

            // do recursion
            return _mod(result, newDeg, b, degB);
        }
    }
    
    public UnivariatePolynomial mod( UnivariatePolynomial other )
    {
        return new UnivariatePolynomial( _mod( this.a, degree(), other.a, other.degree() ), false );
    }
    
    public UnivariatePolynomial div( UnivariatePolynomial other) {
        return new UnivariatePolynomial( _reduce( this.a, degree(), other.a, other.degree() ), false );
    }
    
    public double evaluateAt( double where )
    {
        double result = this.a[ this.a.length - 1 ];
        for( int i = this.a.length - 2; i >= 0; i-- )
            result = result * where + this.a[ i ];
        return result;
    }

    public UnivariatePolynomial shrink()
    {
        while( this.a[ this.degree ] == 0.0 && this.degree > 0 )
            this.degree--;
        UnivariatePolynomial result = new UnivariatePolynomial();
        result.a = new double[ this.degree + 1 ];
        result.degree = result.a.length - 1;
        System.arraycopy( a, 0, result.a, 0, result.a.length );
        return result;
    }
    
    public UnivariatePolynomial derive()
    {
        UnivariatePolynomial result = new UnivariatePolynomial( Math.max( 0, this.degree - 1 ) );
        for( int i = 1; i < this.a.length; i++ )
            result.a[ i - 1 ] = i * this.a[ i ];
        return result;
    }
    
    /**
     * Shifts the polynomial to position {@link a}. The new origin is at {@link a}.
     * map: x -> x - a
     */
    public UnivariatePolynomial shift2( double a )
    {
        double[] shiftedDerValues = evaluateDerivativesAt( a );
        double[] result = new double[ this.a.length ];
        double fac = 1.0;
        result[ 0 ] = shiftedDerValues[ 0 ];
        for( int i = 1; i < shiftedDerValues.length; i++ )
        {
            fac *= i;
            result[ i ] = shiftedDerValues[ i ] / fac;
        }
        return new UnivariatePolynomial( result );
    }
    
    /**
     * Shifts the polynomial to position {@link where}. The new origin is at {@link where}.
     * map: x -> x - where
     */
    public UnivariatePolynomial shift( double a )
    {
        return _shift( a, false );
    }

    /**
     * Performs the transformation x -> 1 / ( x + a )
     */
    public UnivariatePolynomial reverseShift( double a )
    {
        return _shift( a, true );
    }
    
    private UnivariatePolynomial _shift( double a, boolean revert )
    {
        // divide this polynomial repeatedly by ( x + a ) by using ruffini's rule
        // and store the coeffs of the resulting polynomial and the remainder.
        // the remainders give the coeffs of the shifted polynomial.
        double[] hornerCoeffs = new double[ this.a.length ];
        if( revert )
            // perform x -> 1 / ( x + a )
            for( int i = 0; i < this.a.length; i++ )
                hornerCoeffs[ i ] = this.a[ this.a.length - i - 1 ];
        else
            // perform x -> x + a
            System.arraycopy( this.a , 0, hornerCoeffs, 0, this.a.length );
        
        for( int i = 1; i <= this.a.length; i++ )
            for( int j = hornerCoeffs.length - 2; j >= i - 1; j-- )
                hornerCoeffs[ j ] = hornerCoeffs[ j ] + a * hornerCoeffs[ j + 1 ];
 
        return new UnivariatePolynomial( hornerCoeffs, false );
    }
    
    /**
     * simplified _shift( 1.0, false )
     */
    public UnivariatePolynomial shift1()
    {
        double[] hornerCoeffs = new double[ this.a.length ];
        System.arraycopy( this.a , 0, hornerCoeffs, 0, this.a.length );
        
        for( int i = 1; i <= this.a.length; i++ )
            for( int j = hornerCoeffs.length - 2; j >= i - 1; j-- )
                hornerCoeffs[ j ] = hornerCoeffs[ j ] + hornerCoeffs[ j + 1 ];
 
        return new UnivariatePolynomial( hornerCoeffs, false );
    }

    /**
     * simplified _shift( 1.0, true )
     */
    public UnivariatePolynomial reverseShift1()
    {
        double[] hornerCoeffs = new double[ this.a.length ];
        for( int i = 0; i < this.a.length; i++ )
            hornerCoeffs[ i ] = this.a[ this.a.length - i - 1 ];
        
        for( int i = 1; i <= this.a.length; i++ )
            for( int j = hornerCoeffs.length - 2; j >= i - 1; j-- )
                hornerCoeffs[ j ] = hornerCoeffs[ j ] + hornerCoeffs[ j + 1 ];
 
        return new UnivariatePolynomial( hornerCoeffs, false );
    }
    
    public int descartesRuleOfSignShift1()
    {
        int signChanges = 0;
        double[] hornerCoeffs = new double[ this.a.length ];
        System.arraycopy( this.a , 0, hornerCoeffs, 0, this.a.length );
        
        double lastNonZeroCoeff = Double.NaN;
        for( int i = 1; i <= this.a.length; i++ )
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

    public int descartesRuleOfSignReverseShift1()
    {
        int signChanges = 0;
        double[] hornerCoeffs = new double[ this.a.length ];
        for( int i = 0; i < this.a.length; i++ )
            hornerCoeffs[ i ] = this.a[ this.a.length - i - 1 ];
        
        double lastNonZeroCoeff = Double.NaN;
        for( int i = 1; i <= this.a.length; i++ )
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
    
    /**
     * Performs the transformation x -> 1/x.
     */
    public UnivariatePolynomial revert()
    {
        UnivariatePolynomial result = new UnivariatePolynomial( this.degree() );
        for( int i = 0; i < this.a.length; i++ )
            result.a[ i ] = this.a[ this.a.length - 1 - i ];
        return result;
    }
    
    public UnivariatePolynomial stretch( double v )
    {
        double[] resultCoeffs = new double[ this.a.length ];
        double multiplier = 1.0;
        for( int i = 0; i < resultCoeffs.length; i++ )
        {
            resultCoeffs[ i ] = this.a[ i ] * multiplier;
            multiplier *= v;
        }
        return new UnivariatePolynomial( resultCoeffs, false );
    }
    
    public double[] evaluateDerivativesAt( double where )
    {
        double[] c = this.a;
        int nc = this.degree();
        double x = where;
        int nd = nc;                
        double[] pd = new double[ nc + 1 ];
        
        double cnst = 1.0;

        pd[ 0 ] = c[ nc ];
        for( int j = 1; j <= nd; j++ )
            pd[ j ] = 0.0;
        for( int i = nc - 1; i >= 0; i-- )
        {
            int nnd = ( nd < ( nc - i ) ? nd : nc - i );
            for( int j = nnd; j >= 1; j-- )
                pd[ j ] = pd[ j ] * x + pd[ j - 1 ];
            pd[ 0 ] = pd[ 0 ] * x + c[ i ];
        }
        for( int i = 2; i <= nd; i++ )
        {
            cnst *= i;
            pd[ i ] *= cnst;
        }
        
        UnivariatePolynomial der = this;
        for( int i = 0; i < nc; i++ )
        {
            pd[ i ] = der.evaluateAt( x );
            der = der.derive();
        }
        
        return pd;
    }
    
    public int coeffSignChanges()
    {
        int signChanges = 0;
        double lastNonZeroCoeff = this.a[ this.a.length - 1 ];
        for( int i = this.a.length - 2; i >= 0; i-- )
        {
            if( this.a[ i ] != 0.0 )
            {
                if( this.a[ i ] * lastNonZeroCoeff < 0.0 )
                    signChanges++;
                lastNonZeroCoeff = this.a[ i ];
            }
        }
        return signChanges;
    }
    
    public double rootBound()
    {
        double rootBound = 0.0;
        for( int i = 0; i < a.length - 1; i++ )
            rootBound = Math.max( rootBound, 2 * Math.pow( Math.abs( a[ i ] / a[ a.length - 1 ] ), 1.0 / ( this.degree() - i ) ) );
        return rootBound;
    }
    
    public double maxPositiveRootBound()
    {
        double[] normCoeff = new double[ this.a.length ];
        for( int i = 0; i < this.a.length - 1; i++ )
            normCoeff[ i ] = this.a[ i ] / this.a[ this.a.length - 1 ];
        
        int negCoeffs = 0;
        for( int i = 0; i < normCoeff.length - 1; i++ )
            if( normCoeff[ i ] < 0.0 )
                negCoeffs++;
        double rootBound = 0.0;
        negCoeffs = -negCoeffs;
        for( int i = 0; i < normCoeff.length - 1; i++ )
            if( normCoeff[ i ] < 0.0 )
                rootBound = Math.max( rootBound, Math.pow( negCoeffs * normCoeff[ i ], 1.0 / ( this.degree - i ) ) );
        return rootBound;
    }
    
    public double minPositiveRootBound()
    {
        int negCoeffs = 0;
        for( int i = 0; i < this.a.length; i++ )
            if( this.a[ i ] < 0.0 )
                negCoeffs++;
        double rootBound = 0.0;
        negCoeffs = -negCoeffs;
        for( int i = 1; i < this.a.length; i++ )
        {
            if( this.a[ this.degree() - i ] < 0.0 )
            {
                double tempRootBound = Math.pow( negCoeffs * this.a[ i ], 1.0 / i );
                if( rootBound < tempRootBound )
                    rootBound = tempRootBound;
            }
        }
        return 1.0 / rootBound;
    }
    
    public String toString()
    {
        return java.util.Arrays.toString( this.a );
    }
    
    public static UnivariatePolynomial fromRealRoots( double ... roots )
    {
        UnivariatePolynomial result = new UnivariatePolynomial( 1.0 );
        for( int i = 0; i < roots.length; i++ )
            result = result.mult( new UnivariatePolynomial( -roots[ i ], 1.0 ) );
        return result;
    }
    
    public static UnivariatePolynomial gcd( UnivariatePolynomial a, UnivariatePolynomial b )
    {
        while( !( b.degree() == 0 && b.getCoeff( 0 ) == 0.0 ) )
        {
            UnivariatePolynomial t = b;
            b = a.mod( b );
            a = t;
        }
        return a;
    }
}