/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class SturmChainRootFinder2 implements RealRootFinder
{
    /**
     * Find all real roots of p.
     * @param p
     * @return
     */
    public double[] findAllRoots( UnivariatePolynomial p ) { return null; }
    
    /**
     * Find all real roots of p within lowerBound and upperBound (bounds may or may not be included).
     * @param p
     * @param lowerBound
     * @param upperBound
     * @return
     */
    public double[] findAllRootsIn( UnivariatePolynomial p, double lowerBound, double upperBound ) { return null; }
    
    /**
     * Find the smallest real root of p within lowerBound and upperBound (bounds may or may not be included).
     * If no real root exists in this interval, Double.NaN ist returned.
     * @param p
     * @param lowerBound
     * @param upperBound
     * @return
     */
    public double findFirstRootIn( UnivariatePolynomial p, double lowerBound, double upperBound ) { return Double.NaN; }
    
/*
 
// das hier irgendwie nach java übersetzen
 
float epsilon = 0.0001;
float sturm_chain[ ( SIZE * ( SIZE + 1 ) ) / 2 ];

int sc_index( int f_index, int c_index )
{
	
	//int result = 0;
	//for( int i = DEGREE; i > f_index; i-- )
	//	result += i;
	//return result + c_index;
	return ( SIZE * ( SIZE + 1 ) ) / 2 - 1 - ( ( f_index + 1 ) * ( f_index + 2 ) ) / 2 + c_index;
}

float eval_f( float where )
{
	float res = 0.0;
	for( int i = DEGREE; i >= 0; i-- )
		res = sturm_chain[ sc_index( DEGREE, i ) ] + where * res;
	return res;
}

float bisection( float x0, float x1 )
{
	float f0 = eval_f( x0 );
	float f1 = eval_f( x1 );
	float x2 = x0;
	while( abs( x0 - x1 ) > epsilon )
	{
		x2 = 0.5 * ( x0 + x1 );
		float f2 = eval_f( x2 );
		if( f2 * f0 < 0 )
		{
			x1 = x2;
			f1 = f2;
		}
		else
		{
			x0 = x2;
			f0 = f2;
		}
	}
	return x2;
}

bool contains( const in vec2 interval, const in float value )
{
	return ( interval[ 0 ] < value && value < interval[ 1 ] ) || ( interval[ 0 ] > value && value > interval[ 1 ] );
}

void polynom_div( int dividend, int divisor, int remainder )
{
	// copy dividend to be the current remainder
	int i;
	for( i = 0; i <= dividend; i++ )
		sturm_chain[ sc_index( remainder, i )] = sturm_chain[ sc_index( dividend, i ) ];

	int degree_diff = dividend - divisor;
	for( i = dividend; i >= divisor; i-- )
	{
		// calculate quotient of highest coefficient
		float quotient = sturm_chain[ sc_index( remainder, i ) ] / sturm_chain[ sc_index( divisor, divisor ) ];
		
		// after this step the highest coeff. of the old remainder is actually zero
		//sturm_chain[ remainder * SIZE + i ] = 0.0; // unnecessary calculation, because value is known
		
		// calculate new coeffs. of the remainder
		for( int j = 0; j < divisor; j++ )
			sturm_chain[ sc_index( remainder, j + degree_diff ) ] = sturm_chain[ sc_index( remainder, j + degree_diff ) ] - sturm_chain[ sc_index( divisor, j ) ] * quotient;

		degree_diff--;
	}
}

void construct_sturm_chain()
{
	// calculate first derivate of f
	int i;
	//sturm_chain[ DEGREE - 1 ][ DEGREE ] = 0.0f;
	for( i = 1; i <= DEGREE; i++ )
		sturm_chain[ sc_index( DEGREE - 1, i - 1 ) ] = float( i ) * sturm_chain[ sc_index( DEGREE, i ) ];// / ( DEGREE * sturm_chain[ DEGREE ][ DEGREE ] );
	
	// calculate sturm chain
	for( i = DEGREE - 2; i >= 0; i-- )
	{
		// polynom division, which outputs the remainder to the sturm_chain-array
		polynom_div( i + 2, i + 1, i );
		
		// flip the sign of the remainder and normalize polynom
		for( int j = 0; j <= i; j++ )
			sturm_chain[ sc_index( i, j ) ] = -sturm_chain[ sc_index( i, j ) ];
	}
}

float f_sturm( int num, float t )
{
	float res = 0.0;
	for( int i = num; i >= 0; i-- )
		res = sturm_chain[ sc_index( num, i ) ] + t * res;
	return res;
}

int sign_change( float t )
{
	int sign_sum = 0;
	float last_sign, cur_sign;
	
	// #Vorzeichenwechsel an t berechnen
    last_sign = sign( f_sturm( DEGREE, t ) );

	for( int i = DEGREE - 1; i >= 0; i-- )
	{
		cur_sign = sign( f_sturm( i, t ) );
		sign_sum += ( last_sign != cur_sign ) ? 1 : 0;
		if( cur_sign != 0.0 )
			last_sign = cur_sign;
	}
    
	return sign_sum;
}

float bisection_sturm( float x0, float x1 )
{
	float x2 = x0 - 1.0;
	float sign_change_0 = sign_change( x0 );
	float sign_change_1 = sign_change( x1 );
	
	if( ( sign_change_0 - sign_change_1 ) != 0 )
	{
		{
			float f0 = eval_f( x0 );
			float f1 = eval_f( x1 );
			
			while( !( sign_change_0 - sign_change_1 == 1 && f0 * f1 < 0.0 ) )
			{
				x2 = 0.5 * ( x0 + x1 );
				
				float sign_change_2 = sign_change( x2 );
				float f2 = eval_f( x2 );
				
				if( sign_change_0 - sign_change_2 > 0 )
				{
					// there is a root in the first interval -> search in first
					x1 = x2;
					f1 = f2;
					sign_change_1 = sign_change_2;
				}
				else
				{
					// there is no root in the first interval -> search in second
					x0 = x2;
					f0 = f2;
					sign_change_0 = sign_change_2;
				}
			}
		}
		x2 = bisection( x0, x1 );
	}
	
	return x2;
}

struct roots
{
	float x[ DEGREE + 2 ];
	bool valid[ DEGREE + 2 ];
};

roots solve( const in polynomial p, const in vec2 trace_interval )
{
#if DEGREE > 1
	// fill	sturm chain array
	for( int i = 0; i < SIZE; i++ )
		sturm_chain[ sc_index( DEGREE, i ) ] = p.a[ i ];
	construct_sturm_chain();
#endif

	// init result array
	roots res;
	res.x[ 0 ] = trace_interval[ 0 ];
	res.valid[ 0 ] = false;
	for( int i = 1; i < DEGREE + 2; i++ )
	{
		res.x[ i ] = trace_interval[ 1 ];
		res.valid[ i ] = false;
	}

#if DEGREE > 1
	// apply sturm's algorithm
	res.x[ 1 ] = bisection_sturm( trace_interval[ 0 ], trace_interval[ 1 ] );
#else
	// solve linear equation directly
	res.x[ 1 ] = -p.a[ 0 ] / p.a[ 1 ];
#endif
	res.valid[ 1 ] = contains( trace_interval, res.x[ 1 ] );

	return res;
}
 */    
}
