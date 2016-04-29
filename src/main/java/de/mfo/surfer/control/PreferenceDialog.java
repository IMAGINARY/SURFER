package de.mfo.surfer.control;

import de.mfo.surfer.util.Preferences;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import static de.mfo.surfer.util.L.lb;

public class PreferenceDialog extends Dialog< ButtonType >
{
    interface Resetter
    {
        public void reset();
    }

    LinkedList< Resetter > resetters;

    public PreferenceDialog()
    {
        super();
        titleProperty().bind( lb( "preferences" ) );
        setHeaderText( null );

        resetters = new LinkedList< Resetter >();

        getDialogPane().getButtonTypes().addAll( ButtonType.FINISH, ButtonType.CANCEL );

        ( ( Button ) getDialogPane().lookupButton( ButtonType.CANCEL ) ).setOnAction( e -> reset() );

        getDialogPane().setContent( createTabPane() );
    }

    private TabPane createTabPane()
    {
        TabPane tabPane = new TabPane();
        for( Class<?> cls : Preferences.class.getDeclaredClasses() )
            if( Modifier.isStatic( cls.getModifiers() ) )
                tabPane.getTabs().add( createTab( cls ) );
        return tabPane;
    }

    private Tab createTab( Class< ? > cls )
    {
        String prefix = Preferences.class.getSimpleName() + "." + cls.getSimpleName();

        GridPane gridPane = new GridPane();
        gridPane.setHgap( 10 );
        gridPane.setVgap( 10 );
        gridPane.setPadding( new Insets( 10, 0, 0, 0 ) );

        int row = 0;
        for( Method m : cls.getDeclaredMethods() )
        {
            if( Modifier.isStatic( m.getModifiers() ) && m.getName().endsWith( "Property" ) )
            {
                String propertyName = m.getName().substring( 0, m.getName().length() - "Property".length() );
                String key = Preferences.class.getSimpleName() + "." + cls.getSimpleName() + "." + propertyName;

                Node editor = createEditor( m );
                GridPane.setConstraints( editor, 1, row, 1, 1, HPos.LEFT, VPos.BASELINE );

                Label label = new Label();
                label.textProperty().bind( lb( key ) );
                label.setLabelFor( editor );
                GridPane.setConstraints( label, 0, row, 1, 1, HPos.RIGHT, VPos.BASELINE );

                gridPane.getChildren().addAll( label, editor );

                ++row;
            }
        }

        Tab tab = new Tab();
        tab.setClosable( false );
        tab.textProperty().bind( lb( prefix ) );
        tab.setContent( gridPane );

        return tab;
    }

    private Node createEditor( Method m )
    {
        Property p;
        try
        {
            p = ( Property ) m.invoke( null );
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }

        if( BooleanProperty.class.isAssignableFrom( p.getClass() ) )
            return createBooleanEditor( ( BooleanProperty ) p );
        else if( DoubleProperty.class.isAssignableFrom( p.getClass() ) )
            return createDoubleEditor( ( DoubleProperty ) p );
        else
            throw new ClassCastException( "Unsupported property type: " + p.getClass().getName() );
    }

    private Node createBooleanEditor( BooleanProperty bp )
    {
        final boolean originalValue = bp.get();

        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().bindBidirectional( bp );
        resetters.add( () -> bp.set( originalValue ) );

        return checkBox;
    }

    private Node createDoubleEditor( DoubleProperty dp )
    {
        final double originalValue = dp.get();

        Spinner< Double > spinner = new Spinner( Double.MIN_VALUE, Double.MAX_VALUE, originalValue, 0.1 );
        spinner.setEditable( true );
        spinner.getValueFactory().valueProperty().bindBidirectional( dp.asObject() );
        resetters.add( () -> dp.set( originalValue ) );

        return spinner;
    }

    public void reset()
    {
        resetters.forEach( r -> r.reset() );
    }
}
