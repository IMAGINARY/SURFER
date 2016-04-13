package de.mfo.surfer.control;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ExceptionDialog extends Alert
{
    SimpleObjectProperty< Throwable > throwable;

    public ExceptionDialog( Alert.AlertType alertType, Throwable throwable )
    {
        super( alertType );
        initExpandableContent( throwable );
    }

    public ExceptionDialog( Alert.AlertType alertType, String contentText, Throwable throwable, ButtonType... buttons )
    {
        super( alertType, contentText, buttons );
        initExpandableContent( throwable );
    }

    public ExceptionDialog( Alert.AlertType alertType, String headerText, String contentText, Throwable throwable, ButtonType... buttons )
    {
        this( alertType, contentText, throwable, buttons );
        setHeaderText( headerText );
    }

    public ExceptionDialog( Alert.AlertType alertType, String titleText, String headerText, String contentText, Throwable throwable, ButtonType... buttons )
    {
        this( alertType, headerText, contentText, throwable, buttons );
        setTitle( titleText );
    }

    protected void initExpandableContent( Throwable throwable )
    {
        this.throwable = new SimpleObjectProperty< Throwable >( throwable );

        StringWriter sw = new StringWriter();
        throwable.printStackTrace( new PrintWriter( sw ) );
        String details = sw.toString();
        String[] detailsLines = details.split( "\\n" );
        int maxLineLength = Arrays
            .asList( detailsLines )
            .stream()
            .mapToInt( s -> s.length() )
            .reduce( 0, Integer::max );

        TextArea textArea = new TextArea( details );
        textArea.setEditable( false );
        textArea.setWrapText( true );
        textArea.setPrefColumnCount( ( maxLineLength * 2 ) / 3 );
        textArea.setPrefRowCount( detailsLines.length + 1 );

        HBox.setHgrow( textArea, Priority.ALWAYS );

        getDialogPane().setExpandableContent( new HBox( textArea ) );
    }

    public Throwable getThrowable()
    {
        return throwable.getValue();
    }

    public void setThrowable( Throwable value )
    {
        throwable.setValue( value );
    }

    public ObjectProperty< Throwable > throwableProperty()
    {
        return throwable;
    }
}
