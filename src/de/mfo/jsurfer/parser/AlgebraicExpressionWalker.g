tree grammar AlgebraicExpressionWalker;

options { tokenVocab = AlgebraicExpression; ASTLabelType = CommonTree; }

@header
{
package de.mfo.jsurfer.parser;

import de.mfo.jsurfer.algebra.*;
}

@members
{
    public static PolynomialOperation createVariable( String name )
    {
        try
        {
            return new PolynomialVariable( PolynomialVariable.Var.valueOf( name ) );
        }
        catch( Exception e )
        {
            return new DoubleVariable( name );
        }
    }

    public static int createInteger( String text )
    {
        try
        {
            return Integer.parseInt( text );
        }
        catch( NumberFormatException nfe )
        {
            return 0;
        }
    }

    public static double createDouble( String text )
    {
        try
        {
            return Double.parseDouble( text );
        }
        catch( NumberFormatException nfe )
        {
            return Double.NaN;
        }
    }
}

start returns [ PolynomialOperation op ]
    : e = expr { $op = $e.op; }
    ;

expr returns [ PolynomialOperation op, Integer decimal ]
	: ^( PLUS e1 = expr e2 = expr )
            {
                try
                {
                    $op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.add, ( DoubleOperation ) $e1.op, ( DoubleOperation ) $e2.op );
                }
                catch( ClassCastException cce )
                {
                    $op = new PolynomialAddition( $e1.op, $e2.op );
                }
            }
        | ^( MINUS e1 = expr ( e2 = expr )?  )
            {
                if( e2 != null )
                {
                    // subtraction
                    try
                    {
                        $op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.sub, ( DoubleOperation ) $e1.op, ( DoubleOperation ) $e2.op );
                    }
                    catch( ClassCastException cce )
                    {
                        $op = new PolynomialSubtraction( $e1.op, $e2.op );
                    }
                }
                else
                {
                    try
                    {
                        $op = new DoubleUnaryOperation( DoubleUnaryOperation.Op.neg, ( DoubleOperation ) $e1.op );
                    }
                    catch( ClassCastException cce )
                    {
                        $op = new PolynomialNegation( $e1.op );
                    }
                }                
            }
        | ^( MULT e1 = expr e2 = expr )
            {
                try
                {
                    $op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.mult, ( DoubleOperation ) $e1.op, ( DoubleOperation ) $e2.op );
                }
                catch( ClassCastException cce )
                {
                    $op = new PolynomialMultiplication( $e1.op, $e2.op );
                }
            }
        | ^( DIV e1 = expr e2 = expr )
            {
                try
                {
                    $op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.div, ( DoubleOperation ) $e1.op, ( DoubleOperation ) $e2.op );
                }
                catch( ClassCastException cce1 )
                {
                    try
                    {
                        $op = new PolynomialDoubleDivision( $e1.op, ( DoubleOperation ) $e2.op );
                    }
                    catch( ClassCastException cce2 )
                    {
                        throw new RecognitionException();
                    }                    
                }
            }
        | ^( POW e1 = expr e2 = expr )
            {
                try
                {
                    $op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.pow, ( DoubleOperation ) $e1.op, ( DoubleOperation ) $e2.op );
                }
                catch( ClassCastException cce )
                {
                    if( $e2.decimal == null )
                    {
                        throw new RecognitionException();
                    }
                    else
                    {
                        $op = new PolynomialPower( $e1.op, $e2.decimal );
                    }
                }
            }
        | ^( id = IDENTIFIER e1 = expr ( e2 = expr )? )
            {
                if( e2 != null )
                {
                    try
                    {
                        $op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.valueOf( $id.text ), ( DoubleOperation ) $e1.op, ( DoubleOperation ) $e2.op );
                    }
                    catch( ClassCastException cce )
                    {
                        throw new RecognitionException();
                    }
                    catch( IllegalArgumentException iae )
                    {
                        throw new RecognitionException();
                    }
                }
                else
                {
                    try
                    {
                        $op = new DoubleUnaryOperation( DoubleUnaryOperation.Op.valueOf( $id.text ), ( DoubleOperation ) $e1.op );
                    }
                    catch( ClassCastException cce )
                    {
                        throw new RecognitionException();
                    }
                    catch( IllegalArgumentException iae )
                    {
                        throw new RecognitionException();
                    }
                }
                
            }
        | pe = primary_expr { $op = $pe.op; $decimal = pe.decimal; }
	;

primary_expr returns [ PolynomialOperation op, Integer decimal ]
	: i = DECIMAL_LITERAL { $op = new DoubleValue( createDouble( $i.text ) ); $decimal = Integer.valueOf( createInteger( $i.text ) ); }
	| f = FLOATING_POINT_LITERAL { $op = new DoubleValue( createDouble( $f.text ) ); }
	| id = IDENTIFIER { $op = createVariable( $id.text ); }
	;
