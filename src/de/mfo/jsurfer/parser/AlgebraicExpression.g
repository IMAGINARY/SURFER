grammar AlgebraicExpression;

options { language = Java; output = AST; }

tokens {
	PLUS 	= '+' ;
	MINUS	= '-' ;
	MULT	= '*' ;
	DIV	= '/' ;
	POW	= '^' ;
	LPAR	= '(' ;
	RPAR	= ')' ;
}

@header
{
package de.mfo.jsurfer.parser;

import de.mfo.jsurfer.algebra.*;
}
@lexer::header
{
package de.mfo.jsurfer.parser;
}

@members {
    public static PolynomialOperation parse( String s )
        throws Exception
    {
        // Create a string
        ANTLRStringStream input = new ANTLRStringStream( s );

        // Create an ExprLexer that feeds from that stream
        AlgebraicExpressionLexer lexer = new AlgebraicExpressionLexer( input );

        // Create a stream of tokens fed by the lexer
        CommonTokenStream tokens = new CommonTokenStream( lexer );

        // Create a parser that feeds off the token stream
        AlgebraicExpressionParser parser = new AlgebraicExpressionParser( tokens );

        // Begin parsing at start rule
        AlgebraicExpressionParser.start_return r = parser.start();

        // Create a stream of nodes fed by the parser
        CommonTreeNodeStream nodes = new CommonTreeNodeStream( ( CommonTree ) r.getTree() );
        
        // Create a tree parser that feeds off the node stream
        AlgebraicExpressionWalker walker = new AlgebraicExpressionWalker( nodes );
        
        // Begin tree parsing at start rule
        return walker.start();        
    }

    protected void mismatch( IntStream input, int ttype, BitSet follow )
        throws RecognitionException
    {
        throw new MismatchedTokenException(ttype, input);
    }

    public java.lang.Object recoverFromMismatchedSet( IntStream input, RecognitionException e, BitSet follow )
        throws RecognitionException
    {
        throw e;
    }
}

@rulecatch {
    catch( RecognitionException e )
    {
        throw e;
    }
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
 
start
	: add_expr EOF!
	;

add_expr
	: mult_expr ( PLUS^ mult_expr | MINUS^ mult_expr )*
	;

mult_expr
	: neg_expr ( MULT^ neg_expr | DIV^ neg_expr )*
	;

neg_expr
        : MINUS^ pow_expr
        | pow_expr
        ;

pow_expr
	: unary_expr ( POW^ pow_expr )?
	;

unary_expr
        : primary_expr
	| IDENTIFIER^ LPAR! add_expr RPAR!
	;

primary_expr
	: DECIMAL_LITERAL
	| FLOATING_POINT_LITERAL
	| IDENTIFIER
	| LPAR! add_expr RPAR!
	;

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

DECIMAL_LITERAL : ( '0' | '1'..'9' '0'..'9'* ) ;

FLOATING_POINT_LITERAL
	: DIGIT+ '.' DIGIT* EXPONENT?
	| '.' DIGIT+ EXPONENT?
	| DIGIT+ EXPONENT
	;

fragment
EXPONENT : ( 'e' | 'E' ) ( PLUS | MINUS )? DIGIT+ ;

IDENTIFIER : LETTER ( LETTER | DIGIT )*;

WHITESPACE : ( '\t' | ' ' | '\r' | '\n' | '\u000C' )+ 	{ $channel = HIDDEN; } ;

fragment DIGIT	: '0'..'9' ;

fragment
LETTER
	:	'A'..'Z'
	|	'a'..'z'
	|	'_'
	;
