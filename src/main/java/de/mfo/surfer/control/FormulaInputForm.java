package de.mfo.surfer.control;

import de.mfo.surfer.Main;
import de.mfo.surfer.util.FXUtils;
import static de.mfo.surfer.util.L.lb;

import de.mfo.surfer.util.L;
import de.mfo.surfer.util.Preferences;
import de.mfo.surfer.util.TextFieldSelectionAndFocusManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Effect;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.DecimalFormatSymbols;

public class FormulaInputForm extends Region
{
    private static final Logger logger = LoggerFactory.getLogger( FormulaInputForm.class );

    private SimpleStringProperty formula;
    private StringConverter< String > stringConverter;
    private SimpleStringProperty decimalSeparator;
    TextField textField;
    TextFieldSelectionAndFocusManager textFieldManager;
    BooleanProperty isValid;
    Tooltip errorMessage;

    public FormulaInputForm()
    {
        setPickOnBounds( false );
        hideUnusedNodes();
        initTextField();
        initButtons();
        initFeedbackNodes();
        initLabels();
    }

    void initTextField()
    {
        Label equalsZero = new Label( "\u2009=\u20090" );
        equalsZero.getStyleClass().setAll( "formulaFont" );
        equalsZero.setPadding( new Insets( 0, 5, 0, 0 ) );
        equalsZero.effectProperty().bind( Bindings.when( equalsZero.disabledProperty() ).then( FXUtils.getEffectForDisabledNodes() ).otherwise( ( Effect ) null ) );

        Node formulaBox = Main.< Node >fxmlLookup( "#Formula_Box" );
        formulaBox.effectProperty().bind( Bindings.when( this.disabledProperty() ).then( FXUtils.getEffectForDisabledNodes() ).otherwise( ( Effect ) null ) );

        formula = new SimpleStringProperty("");

        decimalSeparator = new SimpleStringProperty();
        decimalSeparator.bind(Bindings.createStringBinding(
            ()->String.valueOf(DecimalFormatSymbols.getInstance(L.localeProperty().get()).getDecimalSeparator()),
            L.localeProperty()
        ));

        stringConverter = new StringConverter<String>() {
            @Override
            public String toString(String s) {
                StringBuffer sb = new StringBuffer();
                for( int i = 0; i < s.length(); ++i )
                    sb.append(formulaToTextFieldCharacter(s.charAt(i)));
                return sb.toString();
            }

            @Override
            public String fromString(String s) {
                StringBuffer sb = new StringBuffer();
                for( int i = 0; i < s.length(); ++i )
                    sb.append(textFieldToFormulaCharacter(s.charAt(i)));
                return sb.toString();
            }
        };

        textField = new TextField();
        textField.getStyleClass().setAll( "formulaFont", "noDecoration" );
        textField.addEventFilter(KeyEvent.KEY_TYPED , e -> {
            if(e.getCharacter() != null && e.getCharacter().length() >= 1) {
                char in = e.getCharacter().charAt(0);
                char out = formulaToTextFieldCharacter(textFieldToFormulaCharacter(in));
                logger.debug("via KEY: {} -> {}",in,out);
                if(in!=out) {
                    e.consume();
                    insertText(String.valueOf(out));
                }
            }
        });
        textField.textProperty().bindBidirectional(formula, stringConverter );
        L.localeProperty().addListener( (o,ov,nv) -> {
            int caretPosition = textField.getCaretPosition();
            String text = textField.getText();
            for(int i = 0; i < text.length(); ++i)
            {
                char in = text.charAt(i);
                char out = formulaToTextFieldCharacter(textFieldToFormulaCharacter(in));
                logger.debug("via LOCALE: {} -> {}",in,out);
                if( in != out ) {
                    logger.debug("via LOCALE: {} -> {}",in,out);
                    textField.replaceText(i, i + 1, String.valueOf(out));
                }
            }
            textField.positionCaret(caretPosition);
        });

        textField.paddingProperty().bind(
            Bindings.createObjectBinding(
                () -> { return new Insets( 0, equalsZero.getWidth(), 0, 10 ); },
                equalsZero.widthProperty()
            )
        );
        textField.effectProperty().bind( Bindings.when( textField.disabledProperty() ).then( FXUtils.getEffectForDisabledNodes() ).otherwise( ( Effect ) null ) );

        BoundingBox textFieldBB;
        {
            Node equationFXMLNode = Main.< Node >fxmlLookup( "#Equation" );
            Node equalsZeroFXMLNode = Main.< Node >fxmlLookup( "#Equals_Zero" );

            Bounds equationBounds = equationFXMLNode.getBoundsInLocal();
            Bounds equalsZeroBounds = equalsZeroFXMLNode.getBoundsInLocal();

            textFieldBB = new BoundingBox(
                equationBounds.getMinX(),
                equationBounds.getMinY(),
                equalsZeroBounds.getMaxX() - equationBounds.getMinX(),
                equationBounds.getHeight()
            );
        }

        FXUtils.resizeTo( textField, textFieldBB );
        equalsZero.setMinHeight( textFieldBB.getHeight() );

        textFieldManager = new TextFieldSelectionAndFocusManager( textField );

        StackPane stackPane = new StackPane( textField, equalsZero );
        stackPane.setAlignment( Pos.BASELINE_RIGHT );
        FXUtils.relocateTo( stackPane, textFieldBB );

        this.getChildren().add( stackPane );
    }

    char formulaToTextFieldCharacter( char c )
    {
        switch(c) {
            case '*':
                return '\u00b7'; // '·'
            case '.':
                return decimalSeparator.get().charAt(0);
        }
        return c;
    }

    char textFieldToFormulaCharacter( char c )
    {
        switch(c) {
            case '\u00b7': // '·'
                return '*';
            case ',':
                return '.';
        }
        return c;
    }

    void initButtons()
    {
        String characters = "xyzabcd0123456789";
        characters.chars().forEach( c -> {
            this.getChildren().add(
                createButton(
                    "Button",
                    "" + ( char ) c,
                    ( event ) -> insertText( "" + ( char ) c )
                )
            );
        } );

        this.getChildren().add( createButton( "Button", "Plus", ( event ) -> insertText( "+" ) ) );
        this.getChildren().add( createButton( "Button", "Minus", ( event ) -> insertText( "-" ) ) );
        this.getChildren().add( createButton( "Button", "Times", ( event ) -> insertText( "*" ) ) );
        this.getChildren().add( createButton( "Button", "Comma", ( event ) -> insertText( decimalSeparator.get() ) ) );
        this.getChildren().add( createButton( "Button", "Bracket_open", ( event ) -> insertText( "(" ) ) );
        this.getChildren().add( createButton( "Button", "Bracket_close", ( event ) -> insertText( ")" ) ) );
        this.getChildren().add( createButton( "Button", "Exp_2", ( event ) -> insertText( "^2" ) ) );
        this.getChildren().add( createButton( "Button", "Exp_3", ( event ) -> insertText( "^3" ) ) );
        this.getChildren().add( createButton( "Button", "Exp_n", ( event ) -> insertText( "^" ) ) );

        this.getChildren().add( createButton( "Button", "Cursor_Left", ( event ) -> textField.backward() ) );
        this.getChildren().add( createButton( "Button", "Cursor_Right", ( event ) -> textField.forward() ) );

        this.getChildren().add( createButton( "Button", "Complete_Delete", ( event ) -> textField.clear() ) );
        this.getChildren().add( createButton( "Button", "Delete", ( event ) -> { if( textField.getSelection().getLength() == 0 ) textField.deletePreviousChar(); else textField.deleteText( textField.getSelection() ); } ) );
    }

    Button createButton( String prefix, String suffix, EventHandler< ActionEvent > handler )
    {
        Node defaultState = Main.< Node >fxmlLookup( "#" + prefix + "_" + suffix );
        Node hoverState = Main.< Node >fxmlLookup( "#" + prefix + "_Over_" + suffix );
        Node armedState = Main.< Node >fxmlLookup( "#" + prefix + "_Pressed_" + suffix );

        SceneNodeButton result = new SceneNodeButton( defaultState, hoverState, armedState );
        result.setOnAction( handler );

        return result;
    }

    void initFeedbackNodes()
    {
        // this can't be used as a button because it misses the "Hover" and "Pressed" states
        Node correct = Main.< Node >fxmlLookup( "#Button_Correct" );
        correct.visibleProperty().bind( isValidProperty() );
        correct.effectProperty().bind( Bindings.when( this.disabledProperty() ).then( FXUtils.getEffectForDisabledNodes() ).otherwise( ( Effect ) null ) );

        // this can be used as a button
        Button wrong = createButton( "Button", "Wrong",  event -> {} );
        wrong.visibleProperty().bind( isValidProperty().not() );
        this.getChildren().add( wrong );

        // show error message in tooltip over the "Wrong" button
        errorMessage = new Tooltip();
        errorMessage.setAutoHide( true );
        wrong.setOnAction( e -> {
            if(Preferences.Kiosk.showParserErrorProperty().get()) {
                Point2D p = wrong.localToScene(0.0, 0.0);
                Scene scene = wrong.getScene();
                Window window = scene.getWindow();
                errorMessage.show(window,
                    p.getX() + scene.getX() + window.getX(),
                    p.getY() + scene.getY() + window.getY()
                );
            }
        } );
    }

    void initLabels()
    {
        Label variables = new Label();
        variables.getStyleClass().add( "keypad-label" );
        variables.textProperty().bind( lb( "variables" ) );
        variables.effectProperty().bind( Bindings.when( variables.disabledProperty() ).then( FXUtils.getEffectForDisabledNodes() ).otherwise( (Effect) null ) );
        FXUtils.relocateTo( variables, FXUtils.setVisible( Main.fxmlLookup( "#Text_Keyboard_XYZ" ), false ) );

        Label arithmeticOperations = new Label();
        arithmeticOperations.getStyleClass().add( "keypad-label" );
        arithmeticOperations.textProperty().bind( lb( "arithmeticOperations" ) );
        arithmeticOperations.effectProperty().bind( Bindings.when( arithmeticOperations.disabledProperty() ).then( FXUtils.getEffectForDisabledNodes() ).otherwise( (Effect) null ) );
        FXUtils.relocateTo( arithmeticOperations, FXUtils.setVisible( Main.fxmlLookup( "#Text_Keyboard_Operations" ), false ) );

        Label parameters = new Label();
        parameters.getStyleClass().add( "keypad-label" );
        parameters.textProperty().bind( lb( "parameters" ) );
        parameters.effectProperty().bind( Bindings.when( parameters.disabledProperty() ).then( FXUtils.getEffectForDisabledNodes() ).otherwise( (Effect) null ) );
        FXUtils.relocateTo( parameters, FXUtils.setVisible( Main.fxmlLookup( "#Text_Keyboard_Parameters" ), false ) );

        this.getChildren().addAll( variables, arithmeticOperations, parameters );
    }

    void hideUnusedNodes()
    {
        String[] ids = { "#Button_Over_draw", "#Button_Pressed_draw", "#Layer_31" };
        for( String id : ids )
            Main.fxmlLookup( id ).setVisible( false );
    }

    void insertText( String text )
    {
        textField.insertText( textField.getCaretPosition(), stringConverter.toString(text) );
    }

    @Override
    public String getUserAgentStylesheet()
    {
        return FormulaInputForm.class.getResource( "/de/mfo/surfer/css/style.css" ).toExternalForm();
    }

    public String getFormula()
    {
        return formula.get();
    }

    public void setFormula( String value )
    {
        formula.set( value );
    }

    public StringProperty formulaProperty()
    {
        return formula;
    }

    public boolean getIsValid()
    {
        return isValidProperty().get();
    }

    public void setIsValid( boolean value )
    {
        isValidProperty().set( value );
    }

    public BooleanProperty isValidProperty()
    {
        if( isValid == null )
            isValid = new SimpleBooleanProperty( true );
        return isValid;
    }

    public String getErrorMessage()
    {
        return errorMessage.getText();
    }

    public void setErrorMessage( String value )
    {
        errorMessage.setText( value );
    }

    public StringProperty errorMessageProperty()
    {
        return errorMessage.textProperty();
    }
}
