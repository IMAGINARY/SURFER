package de.mfo.surfer.control;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import static de.mfo.surfer.util.L.lb;

public class PreferenceDialog extends Dialog< ButtonType >
{
    protected SimpleBooleanProperty diableApplyButton;

    public PreferenceDialog()
    {
        super();
        titleProperty().bind( lb( "preferences" ) );
        setHeaderText( null );

        getDialogPane().getButtonTypes().addAll( ButtonType.APPLY, ButtonType.CANCEL );

        // TODO: bind this property to comparisons of new and old preference values
        diableApplyButton = new SimpleBooleanProperty( true );

        // Enable/Disable apply button depending on whether the preferences actually changed.
        getDialogPane().lookupButton( ButtonType.APPLY ).disableProperty().bind( diableApplyButton );

        getDialogPane().setContent( new Label( "TODO" ) );
    }
}
