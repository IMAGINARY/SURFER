package de.mfo.surfer.control;

import de.mfo.surfer.Main;
import de.mfo.surfer.util.FXUtils;
import static de.mfo.surfer.util.L.lb;

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
import java.util.regex.Pattern;

public class FormulaInputForm extends Region
{
    private static final Logger logger = LoggerFactory.getLogger( FormulaInputForm.class );

    private SimpleStringProperty formula;
    private StringConverter< String > stringConverter;
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

        stringConverter = new StringConverter<String>() {

            Pattern patternToString = Pattern.compile("\\*");
            Pattern patternFromString = Pattern.compile("·");

            @Override
            public String toString(String s) {
                return patternToString.matcher(s).replaceAll("·");
            }

            @Override
            public String fromString(String s) {
                return patternFromString.matcher(s).replaceAll("*");
            }
        };

        textField = new TextField();
        textField.getStyleClass().setAll( "formulaFont", "noDecoration" );
        textField.addEventFilter(KeyEvent.KEY_TYPED , e -> { if( "*".equals( e.getCharacter() ) ) { e.consume(); insertText("*"); } } );
        textField.textProperty().bindBidirectional(formula, stringConverter );
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
        this.getChildren().add( createButton( "Button", "Comma", ( event ) -> insertText( "," ) ) );
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
