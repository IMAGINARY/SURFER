package de.mfo.surfer.control;

import de.mfo.surfer.Main;
import de.mfo.surfer.util.L;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;

public class FormulaInputForm extends Region
{
    TextField textField;
    BooleanProperty isValid;

    public FormulaInputForm()
    {
        applyCss();
        initTextField();
        initButtons();
        initFeedbackNodes();
        initLabels();
    }

    void initTextField()
    {
        Label equalsZero = new Label( " = 0" );
        equalsZero.getStyleClass().setAll( "formulaFont" );
        equalsZero.setPadding( new Insets( 0, 5, 0, 0 ) );

        textField = new TextField();
        textField.getStyleClass().setAll( "formulaFont", "noDecoration" );
        textField.focusedProperty().addListener(
            // always grab input focus
            ( observable, newValue, oldValue ) -> textField.requestFocus()
        );
        textField.paddingProperty().bind(
            Bindings.createObjectBinding(
                () -> { return new Insets( 0, equalsZero.getWidth(), 0, 10 ); },
                equalsZero.widthProperty()
            )
        );

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

        textField.setMinWidth( textFieldBB.getWidth() );
        textField.setMinHeight( textFieldBB.getHeight() );
        equalsZero.setMinHeight( textFieldBB.getHeight() );

        StackPane stackPane = new StackPane( textField, equalsZero );
        stackPane.setAlignment( Pos.BASELINE_RIGHT );
        stackPane.relocate( textFieldBB.getMinX(), textFieldBB.getMinY() );

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
        Main.< Node >fxmlLookup( "#Button_Correct" ).visibleProperty().bind( isValidProperty() );
        Main.< Node >fxmlLookup( "#Button_Wrong" ).visibleProperty().bind( isValidProperty().not() );
    }

    void initLabels()
    {
        Node variablesPlaceholder = Main.< Node >fxmlLookup( "#Text_Keyboard_XYZ" );
        variablesPlaceholder.setVisible( false );
        Bounds variablesBB = variablesPlaceholder.getBoundsInParent();
        Label variables = new Label();
        variables.textProperty().bind( L.localize( "variables" ) );
        variables.relocate( variablesBB.getMinX(), variablesBB.getMinY() );
        variables.setMinWidth( variablesBB.getWidth() );
        variables.setMinHeight( variablesBB.getHeight() );

        Node arithmeticOperationsPlaceholder = Main.< Node >fxmlLookup( "#Text_Keyboard_Operations" );
        arithmeticOperationsPlaceholder.setVisible( false );
        Bounds arithmeticOperationsBB = arithmeticOperationsPlaceholder.getBoundsInParent();
        Label arithmeticOperations = new Label();
        arithmeticOperations.textProperty().bind( L.localize( "arithmeticOperations" ) );
        arithmeticOperations.relocate( arithmeticOperationsBB.getMinX(), arithmeticOperationsBB.getMinY() );
        arithmeticOperations.setMinWidth( arithmeticOperationsBB.getWidth() );
        arithmeticOperations.setMinHeight( arithmeticOperationsBB.getHeight() );

        Node parametersPlaceholder = Main.< Node >fxmlLookup( "#Text_Keyboard_Parameters" );
        parametersPlaceholder.setVisible( false );
        Bounds parametersBB = parametersPlaceholder.getBoundsInParent();
        Label parameters = new Label();
        parameters.textProperty().bind( L.localize( "parameters" ) );
        parameters.relocate( parametersBB.getMinX(), parametersBB.getMinY() );
        parameters.setMinWidth( parametersBB.getWidth() );
        parameters.setMinHeight( parametersBB.getHeight() );

        this.getChildren().addAll( variables, arithmeticOperations, parameters );
    }

    void insertText( String text )
    {
        textField.insertText( textField.getCaretPosition(), text );
    }

    TextField getTextField()
    {
        return textField;
    }

    @Override
    public String getUserAgentStylesheet()
    {
        return FormulaInputForm.class.getResource( "../css/style.css" ).toExternalForm();
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
}
