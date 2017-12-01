package de.mfo.surfer.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextFieldSelectionAndFocusManager {

    private static final Logger logger = LoggerFactory.getLogger( TextFieldSelectionAndFocusManager.class );

    private TextField textField;
    private IndexRange oldSelection;
    private int oldCaretPos;
    private String oldText;
    private ChangeListener<Boolean> focusedListener;
    private ChangeListener<Boolean> disabledListener;
    private ChangeListener<IndexRange> formulaSelectionChangeListener;

    public TextFieldSelectionAndFocusManager(TextField textField) {
        this.formulaSelectionChangeListener = this::formulaSelectionChangeListenerFunc;
        this.focusedListener = (o,wasFocused,isFocused) -> {
            if(wasFocused) {
                oldCaretPos = textField.getCaretPosition();
                oldSelection = textField.getSelection();
                oldText = textField.getText();
                logger.debug("saving caret ({}), selction ({}) and text ({})",oldCaretPos,oldSelection,oldText);
                textField.requestFocus();
            }
            textField.selectionProperty().addListener( formulaSelectionChangeListener );
        };
        this.disabledListener = (o,wasDisabled,isDisabled) -> {
            if(wasDisabled)
                textField.requestFocus();
            textField.selectionProperty().addListener( formulaSelectionChangeListener );
        };

        this.textField = textField;
        textField.focusedProperty().addListener(focusedListener);
        textField.disabledProperty().addListener(disabledListener);
    }

    private void formulaSelectionChangeListenerFunc(ObservableValue o, IndexRange ov, IndexRange nv )
    {
        textField.selectionProperty().removeListener(formulaSelectionChangeListener);
        if( oldText != null && oldText.equals( textField.getText() ) )
        {
            logger.debug( "text is the same: {}", oldText );
            if(oldSelection != null && oldSelection.getLength() > 0) {
                logger.debug("restoring caret ({}) and selction ({}) ",oldCaretPos,oldSelection);
                // move the the selected range into the visible area
                textField.positionCaret(oldSelection.getStart());
                textField.positionCaret(oldSelection.getEnd());
                // select the text
                textField.selectRange(
                    oldCaretPos == oldSelection.getStart() ? oldSelection.getEnd() : oldSelection.getStart(),
                    oldCaretPos
                );
            } else {
                logger.debug("restoring only caret ({})", oldCaretPos);
                textField.selectRange(oldCaretPos, oldCaretPos);
            }
        } else {
            logger.debug( "text differs: old: {} new: {}", oldText, textField.getText() );
            logger.debug( "clearing selecting" );
            textField.deselect();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        textField.focusedProperty().removeListener(focusedListener);
        textField.disabledProperty().removeListener(disabledListener);
        textField.selectionProperty().removeListener(formulaSelectionChangeListener);
    }
}
