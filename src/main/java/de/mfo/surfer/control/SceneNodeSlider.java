package de.mfo.surfer.control;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SceneNodeSlider extends Slider
{
    private static final Logger logger = LoggerFactory.getLogger( SceneNodeSlider.class );

    protected DoubleBinding lerpValue;

    public SceneNodeSlider( Node trackNode, Node thumbNode, Node plusNode, Node minusNode )
    {
        this( trackNode, thumbNode, plusNode, minusNode, true );
    }

    public SceneNodeSlider( Node trackNode, Node thumbNode, Node plusNode, Node minusNode, boolean disableNodes )
    {
        super();
        setPickOnBounds( false );
        setOrientation( Orientation.VERTICAL );
        setMin( 0.0 );
        setMax( 1.0 );
        setBlockIncrement( 0.005 );
        lerpValue = Bindings.createDoubleBinding(
            () -> ( getValue() - getMin() ) / ( getMax() - getMin() ),
            minProperty(),
            maxProperty(),
            valueProperty()
        );

        if( disableNodes )
        {
            trackNode.setDisable( true );
            thumbNode.setDisable( true );
        }

        Bounds trackBB = trackNode.getBoundsInParent();
        Bounds thumbBB = thumbNode.getBoundsInParent();
        BoundingBox sliderBB = new BoundingBox(
            trackBB.getMinX(),
            trackBB.getMinY() - 14,
            trackBB.getWidth(),
            trackBB.getHeight() + 28
        );

        thumbNode.translateYProperty().bind(
            Bindings.createDoubleBinding(
                () -> -( -trackBB.getHeight() + 14 + lerpValue.getValue() * ( trackBB.getHeight() - 28 ) ),
                lerpValue
            )
        );

        relocate( sliderBB.getMinX(), sliderBB.getMinY() );
        setMinWidth( sliderBB.getWidth() );
        setMinHeight( sliderBB.getHeight() );
        setOpacity( 0.0 );

        // increment on click
        plusNode.setOnMouseClicked( e -> increment() );

        // increment repeatedly if pressed down longer
        Timeline plusTimer = new Timeline( new KeyFrame(
            Duration.millis( 50 ),
            ae -> increment()
        ));
        plusTimer.setCycleCount( Animation.INDEFINITE );

        Timeline plusInitTimer = new Timeline( new KeyFrame(
            Duration.millis( 500 ),
            ae -> plusTimer.play()
        ) );

        plusNode.setOnMousePressed( e -> plusInitTimer.play() );
        plusNode.setOnMouseReleased( e -> { plusInitTimer.stop(); plusTimer.stop(); } );
        plusNode.setOnMouseExited( e -> { plusInitTimer.stop(); plusTimer.stop(); } );

        // decrement on click
        minusNode.setOnMouseClicked( e -> decrement() );

        // decrement repeatedly if pressed down longer
        Timeline minusTimer = new Timeline( new KeyFrame(
            Duration.millis( 50 ),
            ae -> decrement()
        ));
        minusTimer.setCycleCount( Animation.INDEFINITE );

        Timeline minusInitTimer = new Timeline( new KeyFrame(
            Duration.millis( 500 ),
            ae -> minusTimer.play()
        ) );

        minusNode.setOnMousePressed( e -> minusInitTimer.play() );
        minusNode.setOnMouseReleased( e -> { minusInitTimer.stop(); minusTimer.stop(); } );
        minusNode.setOnMouseExited( e -> { minusInitTimer.stop(); minusTimer.stop(); } );
    }

    @Override
    public String getUserAgentStylesheet()
    {
        return SceneNodeButton.class.getResource( "../css/style.css" ).toExternalForm();
    }
}
