package de.mfo.surfer.control;

import de.mfo.surfer.util.Preferences;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.LinkedList;
import java.util.function.Function;

import de.mfo.surfer.util.Utils;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.StringConverter;

import static de.mfo.surfer.util.L.lb;

//// FIXME: check if ressources are leaked due to bindings
public class PreferenceDialog extends Dialog< ButtonType >
{
    interface Resetter
    {
        void reset();
    }

    LinkedList< Resetter > resetters;

    public PreferenceDialog()
    {
        super();
        titleProperty().bind( lb( "preferences" ) );
        setHeaderText( null );

        resetters = new LinkedList< Resetter >();

        ButtonType saveToConfigFile = new ButtonType( "Save To Config File" );
        getDialogPane().getButtonTypes().addAll( saveToConfigFile, ButtonType.FINISH, ButtonType.CANCEL );

        ( ( Button ) getDialogPane().lookupButton( ButtonType.CANCEL ) ).setOnAction( e -> reset() );
        ( ( Button ) getDialogPane().lookupButton( saveToConfigFile ) ).setDisable(true);

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
        else if( IntegerProperty.class.isAssignableFrom( p.getClass() ) )
            return createIntegerEditor( ( IntegerProperty ) p );
        else if( ObjectProperty.class.isAssignableFrom( p.getClass() ) )
        {
            Class<?> c = Utils.wrapInRte( () -> (Class<?>) m.getDeclaringClass().getMethod(m.getName().replaceAll("Property$", "Type")).invoke( null));
            if( File.class.isAssignableFrom( c ) ) {
                StringConverter<File> stringConverter = new StringConverter<File>() {
                    @Override
                    public String toString(File file) {
                        return file == null ? "" : file.toString();
                    }

                    @Override
                    public File fromString(String string) {
                        return new File( string );
                    }
                };
                return createFileChooser((ObjectProperty<File>) p, stringConverter, f -> f );
            }
            if( URL.class.isAssignableFrom( c ) ) {
                StringConverter<URL> stringConverter = new StringConverter<URL>() {
                    @Override
                    public String toString(URL file) {
                        return file == null ? "" : file.toString();
                    }

                    @Override
                    public URL fromString(String string) { return Utils.wrapInRte( () -> new URL( string ) ); }
                };
                return createFileChooser((ObjectProperty<URL>) p, stringConverter, f -> Utils.wrapInRte( () -> f.toURI().toURL() ) );
            }
            if( Duration.class.isAssignableFrom( c ) ) {
                return createDurationEditor( ( SimpleObjectProperty<Duration> ) p );
            }
            else if( Enum.class.isAssignableFrom( c ) )
                return createEnumChooser((ObjectProperty<Enum>) p, ( Class<Enum>) c);
            else
                throw new ClassCastException( "Unsupported property type: " + p.getClass().getName() );
        }
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

        Spinner< Double > spinner = new Spinner< Double >( Double.MIN_VALUE, Double.MAX_VALUE, originalValue, 0.1 );
        spinner.setEditable( true );
        spinner.getValueFactory().valueProperty().bindBidirectional( dp.asObject() );
        resetters.add( () -> dp.set( originalValue ) );

        return spinner;
    }

    private Node createIntegerEditor( IntegerProperty ip )
    {
        final int originalValue = ip.get();

        Spinner< Integer > spinner = new Spinner< Integer >( Integer.MIN_VALUE, Integer.MAX_VALUE, originalValue, 1 );
        spinner.setEditable( true );
        spinner.getValueFactory().valueProperty().bindBidirectional( ip.asObject() );
        resetters.add( () -> ip.set( originalValue ) );

        return spinner;
    }

    private <T> Node createFileChooser(ObjectProperty<T> fp, StringConverter<T> stringConverter, Function<File,T> fromFileConverter )
    {
        final T originalValue = fp.get();

        TextField textField = new TextField();
        textField.setEditable( false );
        textField.textProperty().bindBidirectional(fp, stringConverter);
        Button button = new Button( "\u2026" );
        button.setOnAction( e -> {
            File newFile = new FileChooser().showOpenDialog( null );
            if( newFile != null )
                fp.set( fromFileConverter.apply( newFile ) );
        } );
        resetters.add( () -> fp.set( originalValue ) );

        return new HBox( textField, button );
    }

    private Node createEnumChooser( ObjectProperty<Enum> ep, Class< Enum > clazz )
    {
        final Enum originalValue = ep.get();

        final ComboBox<Enum> cb = new ComboBox<>();
        cb.getItems().addAll( clazz.getEnumConstants() );
        cb.getSelectionModel().select( ep.get() );
        ep.bind( cb.getSelectionModel().selectedItemProperty());
        ep.addListener( (o,ov,nv) -> cb.getSelectionModel().select(nv));
        resetters.add( () -> cb.getSelectionModel().select( originalValue ) );

        return cb;
    }

    private Node createDurationEditor( SimpleObjectProperty<Duration> dp ) {
        final Duration originalValue = dp.get();

        final TextField tf = new TextField();
        ChangeListener<Duration> cl = (o,ov,nv) -> tf.setText(dp.get().toString().replaceAll(" ", ""));
        dp.addListener( cl );
        cl.changed( null, null, dp.get());
        tf.setOnAction( event -> {
                try {
                    dp.set(Duration.valueOf(tf.getText()));
                    tf.setStyle("");
                } catch (Exception e) {
                    tf.setStyle("-fx-text-fill: red;");
                }
            }
        );
        resetters.add( () -> dp.set( originalValue ) );

        return tf;
    }

    public void reset()
    {
        resetters.forEach( r -> r.reset() );
    }
}
