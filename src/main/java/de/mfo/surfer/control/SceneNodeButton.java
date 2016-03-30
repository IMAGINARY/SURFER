package de.mfo.surfer.control;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;

public class SceneNodeButton extends Button
{
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

        defaultState.visibleProperty().bind( this.hoverProperty().or( this.armedProperty() ).not() );
        hoverState.visibleProperty().bind( this.hoverProperty().and( this.armedProperty().not() ) );
        armedState.visibleProperty().bind( this.armedProperty() );

        javafx.geometry.Bounds bounds = defaultState.getBoundsInLocal();

        Node placeholder = new Rectangle( bounds.getWidth(), bounds.getHeight() );
        this.setGraphic( placeholder );
        this.setContentDisplay( ContentDisplay.GRAPHIC_ONLY );

        relocate( bounds.getMinX(), bounds.getMinY() );
    }

    @Override
    public String getUserAgentStylesheet()
    {
        return SceneNodeButton.class.getResource( "../css/style.css" ).toExternalForm();
    }
}
