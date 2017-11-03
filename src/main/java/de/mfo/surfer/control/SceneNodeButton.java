package de.mfo.surfer.control;

import de.mfo.surfer.util.FXUtils;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.effect.Effect;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: clear style sheet in order to have a clean CSS base
public class SceneNodeButton extends Button
{
    private static final Logger logger = LoggerFactory.getLogger( SceneNodeButton.class );

    public SceneNodeButton( Node defaultState, Node hoverState, Node armedState )
    {
        this( defaultState, hoverState, armedState, true );
    }

    public SceneNodeButton( Node defaultState, Node hoverState, Node armedState, boolean disableNodes )
    {
        super();

        if( disableNodes )
        {
            defaultState.setDisable( true );
            hoverState.setDisable( true );
            armedState.setDisable( true );
        }

        defaultState.visibleProperty().bind( this.hoverProperty().or( this.armedProperty() ).not().or( this.disabledProperty() ).and( visibleProperty() ) );
        hoverState.visibleProperty().bind( this.hoverProperty().and( this.armedProperty().or( this.disabledProperty() ).not() ).and( visibleProperty() ) );
        armedState.visibleProperty().bind( this.armedProperty().and( this.disabledProperty().not() ).and( visibleProperty() ) );

        defaultState.effectProperty().bind( Bindings.when( this.disabledProperty() ).then( FXUtils.getEffectForDisabledNodes() ).otherwise( ( Effect ) null ) );

        javafx.geometry.Bounds bounds = defaultState.getBoundsInLocal();

        Node placeholder = new Rectangle( bounds.getWidth(), bounds.getHeight() );
        this.setGraphic( placeholder );
        this.setContentDisplay( ContentDisplay.GRAPHIC_ONLY );

        relocate( bounds.getMinX(), bounds.getMinY() );
    }

    @Override
    public String getUserAgentStylesheet()
    {
        return SceneNodeButton.class.getResource( "/de/mfo/surfer/css/style.css" ).toExternalForm();
    }
}
