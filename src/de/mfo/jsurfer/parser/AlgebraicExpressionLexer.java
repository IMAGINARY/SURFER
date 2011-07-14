// $ANTLR 3.3 Nov 30, 2010 12:50:56 AlgebraicExpression.g 2011-07-14 17:31:42

package de.mfo.jsurfer.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class AlgebraicExpressionLexer extends Lexer {
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
    public static final int ERRCHAR=18;

    // delegates
    // delegators

    public AlgebraicExpressionLexer() {;} 
    public AlgebraicExpressionLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public AlgebraicExpressionLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "AlgebraicExpression.g"; }

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:11:6: ( '+' )
            // AlgebraicExpression.g:11:8: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:12:7: ( '-' )
            // AlgebraicExpression.g:12:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "MULT"
    public final void mMULT() throws RecognitionException {
        try {
            int _type = MULT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:13:6: ( '*' )
            // AlgebraicExpression.g:13:8: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MULT"

    // $ANTLR start "DIV"
    public final void mDIV() throws RecognitionException {
        try {
            int _type = DIV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:14:5: ( '/' )
            // AlgebraicExpression.g:14:7: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIV"

    // $ANTLR start "POW"
    public final void mPOW() throws RecognitionException {
        try {
            int _type = POW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:15:5: ( '^' )
            // AlgebraicExpression.g:15:7: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "POW"

    // $ANTLR start "LPAR"
    public final void mLPAR() throws RecognitionException {
        try {
            int _type = LPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:16:6: ( '(' )
            // AlgebraicExpression.g:16:8: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LPAR"

    // $ANTLR start "RPAR"
    public final void mRPAR() throws RecognitionException {
        try {
            int _type = RPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:17:6: ( ')' )
            // AlgebraicExpression.g:17:8: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RPAR"

    // $ANTLR start "DECIMAL_LITERAL"
    public final void mDECIMAL_LITERAL() throws RecognitionException {
        try {
            int _type = DECIMAL_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:124:17: ( ( '0' | '1' .. '9' ( '0' .. '9' )* ) )
            // AlgebraicExpression.g:124:19: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            {
            // AlgebraicExpression.g:124:19: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='0') ) {
                alt2=1;
            }
            else if ( ((LA2_0>='1' && LA2_0<='9')) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // AlgebraicExpression.g:124:21: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // AlgebraicExpression.g:124:27: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // AlgebraicExpression.g:124:36: ( '0' .. '9' )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // AlgebraicExpression.g:124:36: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DECIMAL_LITERAL"

    // $ANTLR start "FLOATING_POINT_LITERAL"
    public final void mFLOATING_POINT_LITERAL() throws RecognitionException {
        try {
            int _type = FLOATING_POINT_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:127:2: ( ( DIGIT )+ '.' ( DIGIT )* ( EXPONENT )? | '.' ( DIGIT )+ ( EXPONENT )? | ( DIGIT )+ EXPONENT )
            int alt9=3;
            alt9 = dfa9.predict(input);
            switch (alt9) {
                case 1 :
                    // AlgebraicExpression.g:127:4: ( DIGIT )+ '.' ( DIGIT )* ( EXPONENT )?
                    {
                    // AlgebraicExpression.g:127:4: ( DIGIT )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // AlgebraicExpression.g:127:4: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt3 >= 1 ) break loop3;
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);

                    match('.'); 
                    // AlgebraicExpression.g:127:15: ( DIGIT )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // AlgebraicExpression.g:127:15: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);

                    // AlgebraicExpression.g:127:22: ( EXPONENT )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='E'||LA5_0=='e') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // AlgebraicExpression.g:127:22: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // AlgebraicExpression.g:128:4: '.' ( DIGIT )+ ( EXPONENT )?
                    {
                    match('.'); 
                    // AlgebraicExpression.g:128:8: ( DIGIT )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // AlgebraicExpression.g:128:8: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);

                    // AlgebraicExpression.g:128:15: ( EXPONENT )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0=='E'||LA7_0=='e') ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // AlgebraicExpression.g:128:15: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // AlgebraicExpression.g:129:4: ( DIGIT )+ EXPONENT
                    {
                    // AlgebraicExpression.g:129:4: ( DIGIT )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // AlgebraicExpression.g:129:4: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);

                    mEXPONENT(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOATING_POINT_LITERAL"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // AlgebraicExpression.g:133:10: ( ( 'e' | 'E' ) ( PLUS | MINUS )? ( DIGIT )+ )
            // AlgebraicExpression.g:133:12: ( 'e' | 'E' ) ( PLUS | MINUS )? ( DIGIT )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // AlgebraicExpression.g:133:26: ( PLUS | MINUS )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='+'||LA10_0=='-') ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // AlgebraicExpression.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // AlgebraicExpression.g:133:44: ( DIGIT )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>='0' && LA11_0<='9')) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // AlgebraicExpression.g:133:44: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "IDENTIFIER"
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:135:12: ( LETTER ( LETTER | DIGIT )* )
            // AlgebraicExpression.g:135:14: LETTER ( LETTER | DIGIT )*
            {
            mLETTER(); 
            // AlgebraicExpression.g:135:21: ( LETTER | DIGIT )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>='0' && LA12_0<='9')||(LA12_0>='A' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='a' && LA12_0<='z')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // AlgebraicExpression.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IDENTIFIER"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:137:12: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
            // AlgebraicExpression.g:137:14: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            {
            // AlgebraicExpression.g:137:14: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0>='\t' && LA13_0<='\n')||(LA13_0>='\f' && LA13_0<='\r')||LA13_0==' ') ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // AlgebraicExpression.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
            } while (true);

             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHITESPACE"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // AlgebraicExpression.g:139:16: ( '0' .. '9' )
            // AlgebraicExpression.g:139:18: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "LETTER"
    public final void mLETTER() throws RecognitionException {
        try {
            // AlgebraicExpression.g:143:2: ( 'A' .. 'Z' | 'a' .. 'z' | '_' )
            // AlgebraicExpression.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "LETTER"

    // $ANTLR start "ERRCHAR"
    public final void mERRCHAR() throws RecognitionException {
        try {
            int _type = ERRCHAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AlgebraicExpression.g:148:9: ( . )
            // AlgebraicExpression.g:148:11: .
            {
            matchAny(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ERRCHAR"

    public void mTokens() throws RecognitionException {
        // AlgebraicExpression.g:1:8: ( PLUS | MINUS | MULT | DIV | POW | LPAR | RPAR | DECIMAL_LITERAL | FLOATING_POINT_LITERAL | IDENTIFIER | WHITESPACE | ERRCHAR )
        int alt14=12;
        alt14 = dfa14.predict(input);
        switch (alt14) {
            case 1 :
                // AlgebraicExpression.g:1:10: PLUS
                {
                mPLUS(); 

                }
                break;
            case 2 :
                // AlgebraicExpression.g:1:15: MINUS
                {
                mMINUS(); 

                }
                break;
            case 3 :
                // AlgebraicExpression.g:1:21: MULT
                {
                mMULT(); 

                }
                break;
            case 4 :
                // AlgebraicExpression.g:1:26: DIV
                {
                mDIV(); 

                }
                break;
            case 5 :
                // AlgebraicExpression.g:1:30: POW
                {
                mPOW(); 

                }
                break;
            case 6 :
                // AlgebraicExpression.g:1:34: LPAR
                {
                mLPAR(); 

                }
                break;
            case 7 :
                // AlgebraicExpression.g:1:39: RPAR
                {
                mRPAR(); 

                }
                break;
            case 8 :
                // AlgebraicExpression.g:1:44: DECIMAL_LITERAL
                {
                mDECIMAL_LITERAL(); 

                }
                break;
            case 9 :
                // AlgebraicExpression.g:1:60: FLOATING_POINT_LITERAL
                {
                mFLOATING_POINT_LITERAL(); 

                }
                break;
            case 10 :
                // AlgebraicExpression.g:1:83: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 11 :
                // AlgebraicExpression.g:1:94: WHITESPACE
                {
                mWHITESPACE(); 

                }
                break;
            case 12 :
                // AlgebraicExpression.g:1:105: ERRCHAR
                {
                mERRCHAR(); 

                }
                break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA9_eotS =
        "\5\uffff";
    static final String DFA9_eofS =
        "\5\uffff";
    static final String DFA9_minS =
        "\2\56\3\uffff";
    static final String DFA9_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA9_acceptS =
        "\2\uffff\1\2\1\1\1\3";
    static final String DFA9_specialS =
        "\5\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "126:1: FLOATING_POINT_LITERAL : ( ( DIGIT )+ '.' ( DIGIT )* ( EXPONENT )? | '.' ( DIGIT )+ ( EXPONENT )? | ( DIGIT )+ EXPONENT );";
        }
    }
    static final String DFA14_eotS =
        "\10\uffff\2\25\1\15\14\uffff\1\25\2\uffff";
    static final String DFA14_eofS =
        "\32\uffff";
    static final String DFA14_minS =
        "\1\0\7\uffff\2\56\1\60\14\uffff\1\56\2\uffff";
    static final String DFA14_maxS =
        "\1\uffff\7\uffff\2\145\1\71\14\uffff\1\145\2\uffff";
    static final String DFA14_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\3\uffff\1\12\1\13\1\14\1\1"+
        "\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\uffff\1\12\1\13";
    static final String DFA14_specialS =
        "\1\0\31\uffff}>";
    static final String[] DFA14_transitionS = {
            "\11\15\2\14\1\15\2\14\22\15\1\14\7\15\1\6\1\7\1\3\1\1\1\15\1"+
            "\2\1\12\1\4\1\10\11\11\7\15\32\13\3\15\1\5\1\13\1\15\32\13\uff85"+
            "\15",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\26\1\uffff\12\26\13\uffff\1\26\37\uffff\1\26",
            "\1\26\1\uffff\12\27\13\uffff\1\26\37\uffff\1\26",
            "\12\26",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\26\1\uffff\12\27\13\uffff\1\26\37\uffff\1\26",
            "",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( PLUS | MINUS | MULT | DIV | POW | LPAR | RPAR | DECIMAL_LITERAL | FLOATING_POINT_LITERAL | IDENTIFIER | WHITESPACE | ERRCHAR );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA14_0 = input.LA(1);

                        s = -1;
                        if ( (LA14_0=='+') ) {s = 1;}

                        else if ( (LA14_0=='-') ) {s = 2;}

                        else if ( (LA14_0=='*') ) {s = 3;}

                        else if ( (LA14_0=='/') ) {s = 4;}

                        else if ( (LA14_0=='^') ) {s = 5;}

                        else if ( (LA14_0=='(') ) {s = 6;}

                        else if ( (LA14_0==')') ) {s = 7;}

                        else if ( (LA14_0=='0') ) {s = 8;}

                        else if ( ((LA14_0>='1' && LA14_0<='9')) ) {s = 9;}

                        else if ( (LA14_0=='.') ) {s = 10;}

                        else if ( ((LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')) ) {s = 11;}

                        else if ( ((LA14_0>='\t' && LA14_0<='\n')||(LA14_0>='\f' && LA14_0<='\r')||LA14_0==' ') ) {s = 12;}

                        else if ( ((LA14_0>='\u0000' && LA14_0<='\b')||LA14_0=='\u000B'||(LA14_0>='\u000E' && LA14_0<='\u001F')||(LA14_0>='!' && LA14_0<='\'')||LA14_0==','||(LA14_0>=':' && LA14_0<='@')||(LA14_0>='[' && LA14_0<=']')||LA14_0=='`'||(LA14_0>='{' && LA14_0<='\uFFFF')) ) {s = 13;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 14, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}