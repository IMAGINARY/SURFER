// $ANTLR 3.3 Nov 30, 2010 12:50:56 AlgebraicExpression.g 2011-03-21 16:26:58

package de.mfo.jsurfer.parser;

import de.mfo.jsurfer.algebra.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class AlgebraicExpressionParser extends Parser {
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


        public AlgebraicExpressionParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public AlgebraicExpressionParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return AlgebraicExpressionParser.tokenNames; }
    public String getGrammarFileName() { return "AlgebraicExpression.g"; }


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


    public static class start_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "start"
    // AlgebraicExpression.g:79:1: start : add_expr EOF ;
    public final AlgebraicExpressionParser.start_return start() throws RecognitionException {
        AlgebraicExpressionParser.start_return retval = new AlgebraicExpressionParser.start_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF2=null;
        AlgebraicExpressionParser.add_expr_return add_expr1 = null;


        Object EOF2_tree=null;

        try {
            // AlgebraicExpression.g:80:2: ( add_expr EOF )
            // AlgebraicExpression.g:80:4: add_expr EOF
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_add_expr_in_start129);
            add_expr1=add_expr();

            state._fsp--;

            adaptor.addChild(root_0, add_expr1.getTree());
            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_start131); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

            catch( RecognitionException e )
            {
                throw e;
            }
        finally {
        }
        return retval;
    }
    // $ANTLR end "start"

    public static class add_expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "add_expr"
    // AlgebraicExpression.g:83:1: add_expr : mult_expr ( PLUS mult_expr | MINUS mult_expr )* ;
    public final AlgebraicExpressionParser.add_expr_return add_expr() throws RecognitionException {
        AlgebraicExpressionParser.add_expr_return retval = new AlgebraicExpressionParser.add_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS4=null;
        Token MINUS6=null;
        AlgebraicExpressionParser.mult_expr_return mult_expr3 = null;

        AlgebraicExpressionParser.mult_expr_return mult_expr5 = null;

        AlgebraicExpressionParser.mult_expr_return mult_expr7 = null;


        Object PLUS4_tree=null;
        Object MINUS6_tree=null;

        try {
            // AlgebraicExpression.g:84:2: ( mult_expr ( PLUS mult_expr | MINUS mult_expr )* )
            // AlgebraicExpression.g:84:4: mult_expr ( PLUS mult_expr | MINUS mult_expr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_mult_expr_in_add_expr143);
            mult_expr3=mult_expr();

            state._fsp--;

            adaptor.addChild(root_0, mult_expr3.getTree());
            // AlgebraicExpression.g:84:14: ( PLUS mult_expr | MINUS mult_expr )*
            loop1:
            do {
                int alt1=3;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==PLUS) ) {
                    alt1=1;
                }
                else if ( (LA1_0==MINUS) ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // AlgebraicExpression.g:84:16: PLUS mult_expr
            	    {
            	    PLUS4=(Token)match(input,PLUS,FOLLOW_PLUS_in_add_expr147); 
            	    PLUS4_tree = (Object)adaptor.create(PLUS4);
            	    root_0 = (Object)adaptor.becomeRoot(PLUS4_tree, root_0);

            	    pushFollow(FOLLOW_mult_expr_in_add_expr150);
            	    mult_expr5=mult_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, mult_expr5.getTree());

            	    }
            	    break;
            	case 2 :
            	    // AlgebraicExpression.g:84:34: MINUS mult_expr
            	    {
            	    MINUS6=(Token)match(input,MINUS,FOLLOW_MINUS_in_add_expr154); 
            	    MINUS6_tree = (Object)adaptor.create(MINUS6);
            	    root_0 = (Object)adaptor.becomeRoot(MINUS6_tree, root_0);

            	    pushFollow(FOLLOW_mult_expr_in_add_expr157);
            	    mult_expr7=mult_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, mult_expr7.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

            catch( RecognitionException e )
            {
                throw e;
            }
        finally {
        }
        return retval;
    }
    // $ANTLR end "add_expr"

    public static class mult_expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mult_expr"
    // AlgebraicExpression.g:87:1: mult_expr : neg_expr ( MULT neg_expr | DIV neg_expr )* ;
    public final AlgebraicExpressionParser.mult_expr_return mult_expr() throws RecognitionException {
        AlgebraicExpressionParser.mult_expr_return retval = new AlgebraicExpressionParser.mult_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MULT9=null;
        Token DIV11=null;
        AlgebraicExpressionParser.neg_expr_return neg_expr8 = null;

        AlgebraicExpressionParser.neg_expr_return neg_expr10 = null;

        AlgebraicExpressionParser.neg_expr_return neg_expr12 = null;


        Object MULT9_tree=null;
        Object DIV11_tree=null;

        try {
            // AlgebraicExpression.g:88:2: ( neg_expr ( MULT neg_expr | DIV neg_expr )* )
            // AlgebraicExpression.g:88:4: neg_expr ( MULT neg_expr | DIV neg_expr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_neg_expr_in_mult_expr171);
            neg_expr8=neg_expr();

            state._fsp--;

            adaptor.addChild(root_0, neg_expr8.getTree());
            // AlgebraicExpression.g:88:13: ( MULT neg_expr | DIV neg_expr )*
            loop2:
            do {
                int alt2=3;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==MULT) ) {
                    alt2=1;
                }
                else if ( (LA2_0==DIV) ) {
                    alt2=2;
                }


                switch (alt2) {
            	case 1 :
            	    // AlgebraicExpression.g:88:15: MULT neg_expr
            	    {
            	    MULT9=(Token)match(input,MULT,FOLLOW_MULT_in_mult_expr175); 
            	    MULT9_tree = (Object)adaptor.create(MULT9);
            	    root_0 = (Object)adaptor.becomeRoot(MULT9_tree, root_0);

            	    pushFollow(FOLLOW_neg_expr_in_mult_expr178);
            	    neg_expr10=neg_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, neg_expr10.getTree());

            	    }
            	    break;
            	case 2 :
            	    // AlgebraicExpression.g:88:32: DIV neg_expr
            	    {
            	    DIV11=(Token)match(input,DIV,FOLLOW_DIV_in_mult_expr182); 
            	    DIV11_tree = (Object)adaptor.create(DIV11);
            	    root_0 = (Object)adaptor.becomeRoot(DIV11_tree, root_0);

            	    pushFollow(FOLLOW_neg_expr_in_mult_expr185);
            	    neg_expr12=neg_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, neg_expr12.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

            catch( RecognitionException e )
            {
                throw e;
            }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mult_expr"

    public static class neg_expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "neg_expr"
    // AlgebraicExpression.g:91:1: neg_expr : ( MINUS pow_expr | pow_expr );
    public final AlgebraicExpressionParser.neg_expr_return neg_expr() throws RecognitionException {
        AlgebraicExpressionParser.neg_expr_return retval = new AlgebraicExpressionParser.neg_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MINUS13=null;
        AlgebraicExpressionParser.pow_expr_return pow_expr14 = null;

        AlgebraicExpressionParser.pow_expr_return pow_expr15 = null;


        Object MINUS13_tree=null;

        try {
            // AlgebraicExpression.g:92:9: ( MINUS pow_expr | pow_expr )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==MINUS) ) {
                alt3=1;
            }
            else if ( (LA3_0==LPAR||(LA3_0>=IDENTIFIER && LA3_0<=FLOATING_POINT_LITERAL)) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // AlgebraicExpression.g:92:11: MINUS pow_expr
                    {
                    root_0 = (Object)adaptor.nil();

                    MINUS13=(Token)match(input,MINUS,FOLLOW_MINUS_in_neg_expr206); 
                    MINUS13_tree = (Object)adaptor.create(MINUS13);
                    root_0 = (Object)adaptor.becomeRoot(MINUS13_tree, root_0);

                    pushFollow(FOLLOW_pow_expr_in_neg_expr209);
                    pow_expr14=pow_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, pow_expr14.getTree());

                    }
                    break;
                case 2 :
                    // AlgebraicExpression.g:93:11: pow_expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_pow_expr_in_neg_expr221);
                    pow_expr15=pow_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, pow_expr15.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

            catch( RecognitionException e )
            {
                throw e;
            }
        finally {
        }
        return retval;
    }
    // $ANTLR end "neg_expr"

    public static class pow_expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "pow_expr"
    // AlgebraicExpression.g:96:1: pow_expr : unary_expr ( POW pow_expr )? ;
    public final AlgebraicExpressionParser.pow_expr_return pow_expr() throws RecognitionException {
        AlgebraicExpressionParser.pow_expr_return retval = new AlgebraicExpressionParser.pow_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token POW17=null;
        AlgebraicExpressionParser.unary_expr_return unary_expr16 = null;

        AlgebraicExpressionParser.pow_expr_return pow_expr18 = null;


        Object POW17_tree=null;

        try {
            // AlgebraicExpression.g:97:2: ( unary_expr ( POW pow_expr )? )
            // AlgebraicExpression.g:97:4: unary_expr ( POW pow_expr )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_expr_in_pow_expr239);
            unary_expr16=unary_expr();

            state._fsp--;

            adaptor.addChild(root_0, unary_expr16.getTree());
            // AlgebraicExpression.g:97:15: ( POW pow_expr )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==POW) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // AlgebraicExpression.g:97:17: POW pow_expr
                    {
                    POW17=(Token)match(input,POW,FOLLOW_POW_in_pow_expr243); 
                    POW17_tree = (Object)adaptor.create(POW17);
                    root_0 = (Object)adaptor.becomeRoot(POW17_tree, root_0);

                    pushFollow(FOLLOW_pow_expr_in_pow_expr246);
                    pow_expr18=pow_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, pow_expr18.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

            catch( RecognitionException e )
            {
                throw e;
            }
        finally {
        }
        return retval;
    }
    // $ANTLR end "pow_expr"

    public static class unary_expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unary_expr"
    // AlgebraicExpression.g:100:1: unary_expr : ( primary_expr | IDENTIFIER LPAR add_expr RPAR );
    public final AlgebraicExpressionParser.unary_expr_return unary_expr() throws RecognitionException {
        AlgebraicExpressionParser.unary_expr_return retval = new AlgebraicExpressionParser.unary_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER20=null;
        Token LPAR21=null;
        Token RPAR23=null;
        AlgebraicExpressionParser.primary_expr_return primary_expr19 = null;

        AlgebraicExpressionParser.add_expr_return add_expr22 = null;


        Object IDENTIFIER20_tree=null;
        Object LPAR21_tree=null;
        Object RPAR23_tree=null;

        try {
            // AlgebraicExpression.g:101:9: ( primary_expr | IDENTIFIER LPAR add_expr RPAR )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==LPAR||(LA5_0>=DECIMAL_LITERAL && LA5_0<=FLOATING_POINT_LITERAL)) ) {
                alt5=1;
            }
            else if ( (LA5_0==IDENTIFIER) ) {
                int LA5_2 = input.LA(2);

                if ( (LA5_2==LPAR) ) {
                    alt5=2;
                }
                else if ( (LA5_2==EOF||(LA5_2>=PLUS && LA5_2<=POW)||(LA5_2>=RPAR && LA5_2<=IDENTIFIER)) ) {
                    alt5=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // AlgebraicExpression.g:101:11: primary_expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_expr_in_unary_expr267);
                    primary_expr19=primary_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, primary_expr19.getTree());

                    }
                    break;
                case 2 :
                    // AlgebraicExpression.g:102:4: IDENTIFIER LPAR add_expr RPAR
                    {
                    root_0 = (Object)adaptor.nil();

                    IDENTIFIER20=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_unary_expr272); 
                    IDENTIFIER20_tree = (Object)adaptor.create(IDENTIFIER20);
                    root_0 = (Object)adaptor.becomeRoot(IDENTIFIER20_tree, root_0);

                    LPAR21=(Token)match(input,LPAR,FOLLOW_LPAR_in_unary_expr275); 
                    pushFollow(FOLLOW_add_expr_in_unary_expr278);
                    add_expr22=add_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, add_expr22.getTree());
                    RPAR23=(Token)match(input,RPAR,FOLLOW_RPAR_in_unary_expr280); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

            catch( RecognitionException e )
            {
                throw e;
            }
        finally {
        }
        return retval;
    }
    // $ANTLR end "unary_expr"

    public static class primary_expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primary_expr"
    // AlgebraicExpression.g:105:1: primary_expr : ( DECIMAL_LITERAL | FLOATING_POINT_LITERAL | IDENTIFIER ( IDENTIFIER )* | LPAR add_expr RPAR );
    public final AlgebraicExpressionParser.primary_expr_return primary_expr() throws RecognitionException {
        AlgebraicExpressionParser.primary_expr_return retval = new AlgebraicExpressionParser.primary_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DECIMAL_LITERAL24=null;
        Token FLOATING_POINT_LITERAL25=null;
        Token IDENTIFIER26=null;
        Token IDENTIFIER27=null;
        Token LPAR28=null;
        Token RPAR30=null;
        AlgebraicExpressionParser.add_expr_return add_expr29 = null;


        Object DECIMAL_LITERAL24_tree=null;
        Object FLOATING_POINT_LITERAL25_tree=null;
        Object IDENTIFIER26_tree=null;
        Object IDENTIFIER27_tree=null;
        Object LPAR28_tree=null;
        Object RPAR30_tree=null;

        try {
            // AlgebraicExpression.g:106:2: ( DECIMAL_LITERAL | FLOATING_POINT_LITERAL | IDENTIFIER ( IDENTIFIER )* | LPAR add_expr RPAR )
            int alt7=4;
            switch ( input.LA(1) ) {
            case DECIMAL_LITERAL:
                {
                alt7=1;
                }
                break;
            case FLOATING_POINT_LITERAL:
                {
                alt7=2;
                }
                break;
            case IDENTIFIER:
                {
                alt7=3;
                }
                break;
            case LPAR:
                {
                alt7=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // AlgebraicExpression.g:106:4: DECIMAL_LITERAL
                    {
                    root_0 = (Object)adaptor.nil();

                    DECIMAL_LITERAL24=(Token)match(input,DECIMAL_LITERAL,FOLLOW_DECIMAL_LITERAL_in_primary_expr292); 
                    DECIMAL_LITERAL24_tree = (Object)adaptor.create(DECIMAL_LITERAL24);
                    adaptor.addChild(root_0, DECIMAL_LITERAL24_tree);


                    }
                    break;
                case 2 :
                    // AlgebraicExpression.g:107:4: FLOATING_POINT_LITERAL
                    {
                    root_0 = (Object)adaptor.nil();

                    FLOATING_POINT_LITERAL25=(Token)match(input,FLOATING_POINT_LITERAL,FOLLOW_FLOATING_POINT_LITERAL_in_primary_expr297); 
                    FLOATING_POINT_LITERAL25_tree = (Object)adaptor.create(FLOATING_POINT_LITERAL25);
                    adaptor.addChild(root_0, FLOATING_POINT_LITERAL25_tree);


                    }
                    break;
                case 3 :
                    // AlgebraicExpression.g:108:4: IDENTIFIER ( IDENTIFIER )*
                    {
                    root_0 = (Object)adaptor.nil();

                    IDENTIFIER26=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary_expr302); 
                    IDENTIFIER26_tree = (Object)adaptor.create(IDENTIFIER26);
                    adaptor.addChild(root_0, IDENTIFIER26_tree);

                    // AlgebraicExpression.g:108:15: ( IDENTIFIER )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==IDENTIFIER) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // AlgebraicExpression.g:108:17: IDENTIFIER
                    	    {
                    	    IDENTIFIER27=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary_expr306); 
                    	    IDENTIFIER27_tree = (Object)adaptor.create(IDENTIFIER27);
                    	    adaptor.addChild(root_0, IDENTIFIER27_tree);


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;
                case 4 :
                    // AlgebraicExpression.g:109:4: LPAR add_expr RPAR
                    {
                    root_0 = (Object)adaptor.nil();

                    LPAR28=(Token)match(input,LPAR,FOLLOW_LPAR_in_primary_expr314); 
                    pushFollow(FOLLOW_add_expr_in_primary_expr317);
                    add_expr29=add_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, add_expr29.getTree());
                    RPAR30=(Token)match(input,RPAR,FOLLOW_RPAR_in_primary_expr319); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

            catch( RecognitionException e )
            {
                throw e;
            }
        finally {
        }
        return retval;
    }
    // $ANTLR end "primary_expr"

    // Delegated rules


 

    public static final BitSet FOLLOW_add_expr_in_start129 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_start131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr143 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_PLUS_in_add_expr147 = new BitSet(new long[]{0x0000000000003A20L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr150 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_MINUS_in_add_expr154 = new BitSet(new long[]{0x0000000000003A20L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr157 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_neg_expr_in_mult_expr171 = new BitSet(new long[]{0x00000000000000C2L});
    public static final BitSet FOLLOW_MULT_in_mult_expr175 = new BitSet(new long[]{0x0000000000003A20L});
    public static final BitSet FOLLOW_neg_expr_in_mult_expr178 = new BitSet(new long[]{0x00000000000000C2L});
    public static final BitSet FOLLOW_DIV_in_mult_expr182 = new BitSet(new long[]{0x0000000000003A20L});
    public static final BitSet FOLLOW_neg_expr_in_mult_expr185 = new BitSet(new long[]{0x00000000000000C2L});
    public static final BitSet FOLLOW_MINUS_in_neg_expr206 = new BitSet(new long[]{0x0000000000003A20L});
    public static final BitSet FOLLOW_pow_expr_in_neg_expr209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pow_expr_in_neg_expr221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_expr_in_pow_expr239 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_POW_in_pow_expr243 = new BitSet(new long[]{0x0000000000003A20L});
    public static final BitSet FOLLOW_pow_expr_in_pow_expr246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_expr_in_unary_expr267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_unary_expr272 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_LPAR_in_unary_expr275 = new BitSet(new long[]{0x0000000000003A20L});
    public static final BitSet FOLLOW_add_expr_in_unary_expr278 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_RPAR_in_unary_expr280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_LITERAL_in_primary_expr292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOATING_POINT_LITERAL_in_primary_expr297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary_expr302 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary_expr306 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_LPAR_in_primary_expr314 = new BitSet(new long[]{0x0000000000003A20L});
    public static final BitSet FOLLOW_add_expr_in_primary_expr317 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_RPAR_in_primary_expr319 = new BitSet(new long[]{0x0000000000000002L});

}