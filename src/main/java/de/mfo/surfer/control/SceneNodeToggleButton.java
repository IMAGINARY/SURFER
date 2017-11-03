package de.mfo.surfer.control;

import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ContentDisplay;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SceneNodeToggleButton extends ToggleButton
{
    private static final Logger logger = LoggerFactory.getLogger( SceneNodeToggleButton.class );

    public SceneNodeToggleButton(Node defaultState, Node hoverState, Node armedState )
    {
        this( defaultState, hoverState, armedState, true );
    }

    public SceneNodeToggleButton(Node defaultState, Node hoverState, Node armedState, boolean disableNodes )
    {
        super();

        if( disableNodes )
        {
            defaultState.setDisable( true );
            hoverState.setDisable( true );
            armedState.setDisable( true );
        }

        defaultState.visibleProperty().bind( this.hoverProperty().or( this.armedProperty() ).or( this.selectedProperty() ).not() );
        hoverState.visibleProperty().bind( this.hoverProperty().and( this.armedProperty().or( this.selectedProperty() ).not() ) );
        armedState.visibleProperty().bind( this.armedProperty().or( this.selectedProperty() ) );

        javafx.geometry.Bounds bounds = defaultState.getBoundsInLocal();

        Node placeholder = new Rectangle( bounds.getWidth(), bounds.getHeight() );
        this.setGraphic( placeholder );
        this.setContentDisplay( ContentDisplay.GRAPHIC_ONLY );

        relocate( bounds.getMinX(), bounds.getMinY() );
    }

    @Override
    public String getUserAgentStylesheet()
    {
        return SceneNodeToggleButton.class.getResource( "/de/mfo/surfer/css/style.css" ).toExternalForm();
    }
}
