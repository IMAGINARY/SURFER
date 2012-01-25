/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.util;

/**
 *
 * @author stussak
 */
public class Texify
{
	private static int matchParenthesis( StringBuffer sb, int par_pos )
	{
		int par_count = 1;
		for( ; par_pos < sb.length(); par_pos++ )
		{
			if( sb.charAt( par_pos ) == '(' )
				par_count++;
			else if ( sb.charAt( par_pos ) == ')' )
				par_count--;
			//System.out.println( sb.charAt( par_pos ) + " at " + par_pos + " gives " +par_count );
			if( par_count == 0 )
				break;
		}
		return par_pos < sb.length() ? par_pos : -1;
	}

	public static String texify( String s )
	{
		// remove whitespace
		s = s.replaceAll( "\\p{Space}", "" );

		// special treatment of sqrt
		StringBuffer sb = new StringBuffer( s );
		int sqrt_pos = 0;
		while( ( sqrt_pos = sb.indexOf( "sqrt(", sqrt_pos ) ) != -1 )
		{

			int par_pos = matchParenthesis( sb, sqrt_pos + 5 );
			if( par_pos != -1 )
			{
				sb.setCharAt( sqrt_pos + 4, '{' );
				sb.setCharAt( par_pos, '}' );
			}
			sqrt_pos++;
		}
		// special treatment of ^(...)
		int exp_pos = 0;
		while( ( exp_pos = sb.indexOf( "^(", exp_pos ) ) != -1 )
		{
			int par_pos = matchParenthesis( sb, exp_pos + 2 );
			if( par_pos != -1 )
			{
				sb.setCharAt( exp_pos + 1, '{' );
				sb.setCharAt( par_pos, '}' );
			}
			exp_pos++;
		}
		s = sb.toString();


                // texify paranthesis
		s = s.replaceAll( "([a-zA-Z]+)\\(", "{$1(" ); // opname(...) -> {opname(...)}
		s = s.replaceAll( "([+-/\\*\\^])\\(", "$1{(" ); // infixop( -> infixop{(
		while( s.matches( ".*\\(\\(.*" ) )
		{
			s = s.replaceAll( "\\(\\(", "({(" ); // (( --> ({(
		}
		s = s.replaceAll( "^\\(", "{(" ); // ( at beginning -> {(
		s = s.replaceAll( "\\)", ")}" ); // ) -> )}
		s = s.replaceAll( "\\(", "\\\\left(" );
		s = s.replaceAll( "\\)", "\\\\right)" );


                // replace *
                s = s.replaceAll( "\\*", "\\\\cdot{}" );

		// texify numerical exponents
		s = s.replaceAll( "\\^(\\d+)", "\\^{$1}" );

		// texify special operators
		String[] ops1 = { "sin", "cos", "tan", "exp", "log", "sqrt" };
		String[] ops2 = { "neg", "ceil", "floor", "abs", "sign", "atan2" };
		String[] ops3 = { "asin", "acos", "atan" };
		for( int i = 0; i < ops1.length; i++ )
			s = s.replaceAll( ops1[ i ], "\\\\" + ops1[ i ] );
		for( int i = 0; i < ops2.length; i++ )
			s = s.replaceAll( ops2[ i ], "\\\\operatorname{" + ops1[ i ] + "}" );
		for( int i = 0; i < ops3.length; i++ )
			s = s.replaceAll( ops3[ i ], "\\\\" + ops1[ i ] + "^{-1}" );

		return "\\ensuremath{" + s + "}";
	}
}
