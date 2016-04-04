package de.mfo.surfer.control;

import de.mfo.surfer.Main;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Region;
import javafx.scene.Node;

public class SceneNodeSliderWithNameAndValue extends Region
{
    SceneNodeSlider slider;
    Label nameLabel;
    Label valueLabel;

    public SceneNodeSliderWithNameAndValue( String groupName, String name, double value )
    {
        slider = new SceneNodeSlider(
            Main.< Node >fxmlLookup( "#" + groupName + "_Shaft" ),
            Main.< Node >fxmlLookup( "#" + groupName + "_Knob" ),
            Main.< Node >fxmlLookup( "#" + groupName + "_Button_Plus" ),
            Main.< Node >fxmlLookup( "#" + groupName + "_Button_Minus" )
        );

        nameLabel = new Label( name );
        valueLabel = new Label( Double.toString( value ) );
        valueLabel.textProperty().bind( Bindings.createStringBinding(
            () -> String.format( "%.3f" , slider.getValue() ),
            slider.valueProperty()
        ) );

        setPickOnBounds( false );

        Node namePlaceholderNode = Main.< Node >fxmlLookup( "#" + groupName + "_Name" );
        Node valuePlaceholderNode = Main.< Node >fxmlLookup( "#" + groupName + "_Value" );

        namePlaceholderNode.setVisible( false );
        valuePlaceholderNode.setVisible( false );

        Bounds nameBB = namePlaceholderNode.getBoundsInParent();
        Bounds valueBB = valuePlaceholderNode.getBoundsInParent();

        nameLabel.setMinWidth( nameBB.getWidth() );
        nameLabel.setMinHeight( nameBB.getHeight() );
        nameLabel.relocate( nameBB.getMinX(), nameBB.getMinY() );
        valueLabel.setMinWidth( valueBB.getWidth() );
        valueLabel.setMinHeight( valueBB.getHeight() );
        valueLabel.relocate( valueBB.getMinX(), valueBB.getMinY() );

        this.getChildren().addAll( slider, nameLabel, valueLabel );
    }

    public Slider getSlider()
    {
        return slider;
    }

    public Label getNameLabel()
    {
        return nameLabel;
    }

    public Label getValueLabel()
    {
        return valueLabel;
    }

    @Override
    public String getUserAgentStylesheet()
    {
        return SceneNodeSliderWithNameAndValue.class.getResource( "../css/style.css" ).toExternalForm();
    }
}
