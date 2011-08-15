// $ANTLR 3.3 Nov 30, 2010 12:50:56 AlgebraicExpressionWalker.g 2011-06-20 15:15:19

package de.mfo.jsurfer.parser;

import de.mfo.jsurfer.algebra.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class AlgebraicExpressionWalker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PLUS", "MINUS", "MULT", "DIV", "POW", "LPAR", "RPAR", "IDENTIFIER", "DECIMAL_LITERAL", "FLOATING_POINT_LITERAL", "DIGIT", "EXPONENT", "LETTER", "WHITESPACE"
    };
    public static final int EOF=-1;
    public static final int PLUS=4;
    public static final int MINUS=5;
    public static final int MULT=6;
    public static final int DIV=7;
    public static final int POW=8;
    public static final int LPAR=9;
    public static final int RPAR=10;
    public static final int IDENTIFIER=11;
    public static final int DECIMAL_LITERAL=12;
    public static final int FLOATING_POINT_LITERAL=13;
    public static final int DIGIT=14;
    public static final int EXPONENT=15;
    public static final int LETTER=16;
    public static final int WHITESPACE=17;

    // delegates
    // delegators


        public AlgebraicExpressionWalker(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public AlgebraicExpressionWalker(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return AlgebraicExpressionWalker.tokenNames; }
    public String getGrammarFileName() { return "AlgebraicExpressionWalker.g"; }


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
                return java.lang.Double.parseDouble( text );
            }
            catch( NumberFormatException nfe )
            {
                return java.lang.Double.NaN;
            }
        }



    // $ANTLR start "start"
    // AlgebraicExpressionWalker.g:51:1: start returns [ PolynomialOperation op ] : e= expr ;
    public final PolynomialOperation start() throws RecognitionException {
        PolynomialOperation op = null;

        AlgebraicExpressionWalker.expr_return e = null;


        try {
            // AlgebraicExpressionWalker.g:52:5: (e= expr )
            // AlgebraicExpressionWalker.g:52:7: e= expr
            {
            pushFollow(FOLLOW_expr_in_start55);
            e=expr();

            state._fsp--;

             op = (e!=null?e.op:null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return op;
    }
    // $ANTLR end "start"

    public static class expr_return extends TreeRuleReturnScope {
        public PolynomialOperation op;
        public Integer decimal;
    };

    // $ANTLR start "expr"
    // AlgebraicExpressionWalker.g:55:1: expr returns [ PolynomialOperation op, Integer decimal ] : ( ^( PLUS e1= expr e2= expr ) | ^( MINUS e1= expr (e2= expr )? ) | ^( MULT e1= expr e2= expr ) | ^( DIV e1= expr e2= expr ) | ^( POW e1= expr e2= expr ) | ^(id= IDENTIFIER e1= expr (e2= expr )? ) | pe= primary_expr );
    public final AlgebraicExpressionWalker.expr_return expr() throws RecognitionException {
        AlgebraicExpressionWalker.expr_return retval = new AlgebraicExpressionWalker.expr_return();
        retval.start = input.LT(1);

        CommonTree id=null;
        AlgebraicExpressionWalker.expr_return e1 = null;

        AlgebraicExpressionWalker.expr_return e2 = null;

        AlgebraicExpressionWalker.primary_expr_return pe = null;


        try {
            // AlgebraicExpressionWalker.g:56:2: ( ^( PLUS e1= expr e2= expr ) | ^( MINUS e1= expr (e2= expr )? ) | ^( MULT e1= expr e2= expr ) | ^( DIV e1= expr e2= expr ) | ^( POW e1= expr e2= expr ) | ^(id= IDENTIFIER e1= expr (e2= expr )? ) | pe= primary_expr )
            int alt3=7;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt3=1;
                }
                break;
            case MINUS:
                {
                alt3=2;
                }
                break;
            case MULT:
                {
                alt3=3;
                }
                break;
            case DIV:
                {
                alt3=4;
                }
                break;
            case POW:
                {
                alt3=5;
                }
                break;
            case IDENTIFIER:
                {
                int LA3_6 = input.LA(2);

                if ( (LA3_6==DOWN) ) {
                    alt3=6;
                }
                else if ( (LA3_6==EOF||(LA3_6>=UP && LA3_6<=POW)||(LA3_6>=IDENTIFIER && LA3_6<=FLOATING_POINT_LITERAL)) ) {
                    alt3=7;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 3, 6, input);

                    throw nvae;
                }
                }
                break;
            case DECIMAL_LITERAL:
            case FLOATING_POINT_LITERAL:
                {
                alt3=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // AlgebraicExpressionWalker.g:56:4: ^( PLUS e1= expr e2= expr )
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_expr77); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expr_in_expr83);
                    e1=expr();

                    state._fsp--;

                    pushFollow(FOLLOW_expr_in_expr89);
                    e2=expr();

                    state._fsp--;


                    match(input, Token.UP, null); 

                                    try
                                    {
                                        retval.op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.add, ( DoubleOperation ) (e1!=null?e1.op:null), ( DoubleOperation ) (e2!=null?e2.op:null) );
                                    }
                                    catch( ClassCastException cce )
                                    {
                                        retval.op = new PolynomialAddition( (e1!=null?e1.op:null), (e2!=null?e2.op:null) );
                                    }
                                

                    }
                    break;
                case 2 :
                    // AlgebraicExpressionWalker.g:67:11: ^( MINUS e1= expr (e2= expr )? )
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_expr119); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expr_in_expr125);
                    e1=expr();

                    state._fsp--;

                    // AlgebraicExpressionWalker.g:67:30: (e2= expr )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( ((LA1_0>=PLUS && LA1_0<=POW)||(LA1_0>=IDENTIFIER && LA1_0<=FLOATING_POINT_LITERAL)) ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // AlgebraicExpressionWalker.g:67:32: e2= expr
                            {
                            pushFollow(FOLLOW_expr_in_expr133);
                            e2=expr();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                                    if( e2 != null )
                                    {
                                        // subtraction
                                        try
                                        {
                                            retval.op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.sub, ( DoubleOperation ) (e1!=null?e1.op:null), ( DoubleOperation ) (e2!=null?e2.op:null) );
                                        }
                                        catch( ClassCastException cce )
                                        {
                                            retval.op = new PolynomialSubtraction( (e1!=null?e1.op:null), (e2!=null?e2.op:null) );
                                        }
                                    }
                                    else
                                    {
                                        try
                                        {
                                            retval.op = new DoubleUnaryOperation( DoubleUnaryOperation.Op.neg, ( DoubleOperation ) (e1!=null?e1.op:null) );
                                        }
                                        catch( ClassCastException cce )
                                        {
                                            retval.op = new PolynomialNegation( (e1!=null?e1.op:null) );
                                        }
                                    }
                                

                    }
                    break;
                case 3 :
                    // AlgebraicExpressionWalker.g:93:11: ^( MULT e1= expr e2= expr )
                    {
                    match(input,MULT,FOLLOW_MULT_in_expr167); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expr_in_expr173);
                    e1=expr();

                    state._fsp--;

                    pushFollow(FOLLOW_expr_in_expr179);
                    e2=expr();

                    state._fsp--;


                    match(input, Token.UP, null); 

                                    try
                                    {
                                        retval.op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.mult, ( DoubleOperation ) (e1!=null?e1.op:null), ( DoubleOperation ) (e2!=null?e2.op:null) );
                                    }
                                    catch( ClassCastException cce )
                                    {
                                        retval.op = new PolynomialMultiplication( (e1!=null?e1.op:null), (e2!=null?e2.op:null) );
                                    }
                                

                    }
                    break;
                case 4 :
                    // AlgebraicExpressionWalker.g:104:11: ^( DIV e1= expr e2= expr )
                    {
                    match(input,DIV,FOLLOW_DIV_in_expr209); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expr_in_expr215);
                    e1=expr();

                    state._fsp--;

                    pushFollow(FOLLOW_expr_in_expr221);
                    e2=expr();

                    state._fsp--;


                    match(input, Token.UP, null); 

                                    try
                                    {
                                        retval.op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.div, ( DoubleOperation ) (e1!=null?e1.op:null), ( DoubleOperation ) (e2!=null?e2.op:null) );
                                    }
                                    catch( ClassCastException cce1 )
                                    {
                                        try
                                        {
                                            retval.op = new PolynomialDoubleDivision( (e1!=null?e1.op:null), ( DoubleOperation ) (e2!=null?e2.op:null) );
                                        }
                                        catch( ClassCastException cce2 )
                                        {
                                            throw new RecognitionException();
                                        }
                                    }
                                

                    }
                    break;
                case 5 :
                    // AlgebraicExpressionWalker.g:122:11: ^( POW e1= expr e2= expr )
                    {
                    match(input,POW,FOLLOW_POW_in_expr251); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expr_in_expr257);
                    e1=expr();

                    state._fsp--;

                    pushFollow(FOLLOW_expr_in_expr263);
                    e2=expr();

                    state._fsp--;


                    match(input, Token.UP, null); 

                                    try
                                    {
                                        retval.op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.pow, ( DoubleOperation ) (e1!=null?e1.op:null), ( DoubleOperation ) (e2!=null?e2.op:null) );
                                    }
                                    catch( ClassCastException cce )
                                    {
                                        if( (e2!=null?e2.decimal:null) == null )
                                        {
                                            throw new RecognitionException();
                                        }
                                        else
                                        {
                                            retval.op = new PolynomialPower( (e1!=null?e1.op:null), (e2!=null?e2.decimal:null) );
                                        }
                                    }
                                

                    }
                    break;
                case 6 :
                    // AlgebraicExpressionWalker.g:140:11: ^(id= IDENTIFIER e1= expr (e2= expr )? )
                    {
                    id=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_expr297); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expr_in_expr303);
                    e1=expr();

                    state._fsp--;

                    // AlgebraicExpressionWalker.g:140:40: (e2= expr )?
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( ((LA2_0>=PLUS && LA2_0<=POW)||(LA2_0>=IDENTIFIER && LA2_0<=FLOATING_POINT_LITERAL)) ) {
                        alt2=1;
                    }
                    switch (alt2) {
                        case 1 :
                            // AlgebraicExpressionWalker.g:140:42: e2= expr
                            {
                            pushFollow(FOLLOW_expr_in_expr311);
                            e2=expr();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                                    if( e2 != null )
                                    {
                                        try
                                        {
                                            retval.op = new DoubleBinaryOperation( DoubleBinaryOperation.Op.valueOf( (id!=null?id.getText():null) ), ( DoubleOperation ) (e1!=null?e1.op:null), ( DoubleOperation ) (e2!=null?e2.op:null) );
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
                                            retval.op = new DoubleUnaryOperation( DoubleUnaryOperation.Op.valueOf( (id!=null?id.getText():null) ), ( DoubleOperation ) (e1!=null?e1.op:null) );
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
                    break;
                case 7 :
                    // AlgebraicExpressionWalker.g:174:11: pe= primary_expr
                    {
                    pushFollow(FOLLOW_primary_expr_in_expr346);
                    pe=primary_expr();

                    state._fsp--;

                     retval.op = (pe!=null?pe.op:null); retval.decimal = pe.decimal; 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expr"

    public static class primary_expr_return extends TreeRuleReturnScope {
        public PolynomialOperation op;
        public Integer decimal;
    };

    // $ANTLR start "primary_expr"
    // AlgebraicExpressionWalker.g:177:1: primary_expr returns [ PolynomialOperation op, Integer decimal ] : (i= DECIMAL_LITERAL | f= FLOATING_POINT_LITERAL | id= IDENTIFIER );
    public final AlgebraicExpressionWalker.primary_expr_return primary_expr() throws RecognitionException {
        AlgebraicExpressionWalker.primary_expr_return retval = new AlgebraicExpressionWalker.primary_expr_return();
        retval.start = input.LT(1);

        CommonTree i=null;
        CommonTree f=null;
        CommonTree id=null;

        try {
            // AlgebraicExpressionWalker.g:178:2: (i= DECIMAL_LITERAL | f= FLOATING_POINT_LITERAL | id= IDENTIFIER )
            int alt4=3;
            switch ( input.LA(1) ) {
            case DECIMAL_LITERAL:
                {
                alt4=1;
                }
                break;
            case FLOATING_POINT_LITERAL:
                {
                alt4=2;
                }
                break;
            case IDENTIFIER:
                {
                alt4=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // AlgebraicExpressionWalker.g:178:4: i= DECIMAL_LITERAL
                    {
                    i=(CommonTree)match(input,DECIMAL_LITERAL,FOLLOW_DECIMAL_LITERAL_in_primary_expr367); 
                     retval.op = new DoubleValue( createDouble( (i!=null?i.getText():null) ) ); retval.decimal = Integer.valueOf( createInteger( (i!=null?i.getText():null) ) ); 

                    }
                    break;
                case 2 :
                    // AlgebraicExpressionWalker.g:179:4: f= FLOATING_POINT_LITERAL
                    {
                    f=(CommonTree)match(input,FLOATING_POINT_LITERAL,FOLLOW_FLOATING_POINT_LITERAL_in_primary_expr378); 
                     retval.op = new DoubleValue( createDouble( (f!=null?f.getText():null) ) ); 

                    }
                    break;
                case 3 :
                    // AlgebraicExpressionWalker.g:180:4: id= IDENTIFIER
                    {
                    id=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary_expr389); 
                     retval.op = createVariable( (id!=null?id.getText():null) ); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "primary_expr"

    // Delegated rules


 

    public static final BitSet FOLLOW_expr_in_start55 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_expr77 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expr_in_expr83 = new BitSet(new long[]{0x00000000000039F0L});
    public static final BitSet FOLLOW_expr_in_expr89 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MINUS_in_expr119 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expr_in_expr125 = new BitSet(new long[]{0x00000000000039F8L});
    public static final BitSet FOLLOW_expr_in_expr133 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MULT_in_expr167 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expr_in_expr173 = new BitSet(new long[]{0x00000000000039F0L});
    public static final BitSet FOLLOW_expr_in_expr179 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DIV_in_expr209 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expr_in_expr215 = new BitSet(new long[]{0x00000000000039F0L});
    public static final BitSet FOLLOW_expr_in_expr221 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_POW_in_expr251 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expr_in_expr257 = new BitSet(new long[]{0x00000000000039F0L});
    public static final BitSet FOLLOW_expr_in_expr263 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IDENTIFIER_in_expr297 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expr_in_expr303 = new BitSet(new long[]{0x00000000000039F8L});
    public static final BitSet FOLLOW_expr_in_expr311 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_primary_expr_in_expr346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_LITERAL_in_primary_expr367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOATING_POINT_LITERAL_in_primary_expr378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary_expr389 = new BitSet(new long[]{0x0000000000000002L});

}