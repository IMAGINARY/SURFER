/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class NewtonInterpolationCoefficientCalculator implements CoefficientCalculator
{
    private PolynomialOperation polynomialOperation;
    private int degree;
    private int size;
    
    public NewtonInterpolationCoefficientCalculator( PolynomialOperation polynomialOperation )
    {
        this.polynomialOperation = polynomialOperation;
        this.degree = polynomialOperation.accept( new DegreeCalculator(), ( Void ) null );
        this.size = this.degree + 1;
    }
       
    public UnivariatePolynomial calculateCoefficients( UnivariatePolynomial xPoly, UnivariatePolynomial yPoly, UnivariatePolynomial zPoly )
    {
        double[] x = new double[ size ];
        double[] y = new double[ size ];
        double[] newton_basis = new double[ size ];        
        ValueCalculator valueCalculator = new ValueCalculator();
        
        // DEGREE + 1 Stützpunkte auf Strahl eye + t * pos berechnen
        for( int i = 0; i <= degree; i++ )
        {
            x[ i ] = -1.0 + ( 2.0 * i ) / degree;
            valueCalculator.setX( xPoly.getCoeff( 0 ) + xPoly.getCoeff( 1 ) * x[ i ] );
            valueCalculator.setY( yPoly.getCoeff( 0 ) + yPoly.getCoeff( 1 ) * x[ i ] );
            valueCalculator.setZ( zPoly.getCoeff( 0 ) + zPoly.getCoeff( 1 ) * x[ i ] );
            y[ i ] = this.polynomialOperation.accept( valueCalculator, ( Void ) null );
        }

        // dividierte Differenzen berechen
        for( int i = 1; i <= degree; i++ )
            for( int j = degree; j >= i; j-- )
                y[ j ] = ( y[ j ] - y[ j - 1 ] ) / ( x[ j ] - x[ j - i ] );

        // schrittweise Koeffizienten mit Newton-Interpolationsformel berechnen
        double[] a = new double[ size ];

        newton_basis[ degree ] = 1.0;
        a[ 0 ] = y[ 0 ];

        for( int i = 1; i <= degree; i++ )
        {
            // ( ai*x^i + ... + a0 ) + ( ai*x^i + ... + a0 ) * ( x - x[ i ] ) * y[ i ] = ( ai*x^i + ... + a0 ) * x - x[ i ] * y[ i ] * ( ai*x^i + ... + a0 ) berechnen
            // 1. Koeffizienten der Newton-Basis um eine Potenz erhöhen (=shiften)
            newton_basis[ degree - i ] = 0.0;
            a[ i ] = 0.0;

            // 2. alte Koeffizienten der Newton-Basis multipliziert mit x[ i - 1 ] subtrahieren
            for( int j = degree - i; j < degree; j++ )
                newton_basis[ j ] = newton_basis[ j ] - newton_basis[ j + 1 ] * x[ i - 1 ];

            // 3. y[ i ] * ( neue Newton-Basis ) auf alte Koeffizienten addieren
            for( int j = 0; j <= i; j++ )
                a[ j ] += newton_basis[ degree - i + j ] * y[ i ];
        }
        
        return new UnivariatePolynomial( a );
    }
}
