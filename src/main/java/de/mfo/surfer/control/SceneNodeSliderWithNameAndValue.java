package de.mfo.surfer.control;

import de.mfo.surfer.Main;
import de.mfo.surfer.util.FXUtils;
import de.mfo.surfer.util.L;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Function;

public class SceneNodeSliderWithNameAndValue extends Region
{
    private static final Logger logger = LoggerFactory.getLogger( SceneNodeSliderWithNameAndValue.class );

    SceneNodeSlider slider;
    Label nameLabel;
    Label valueLabel;
    Function< Double, String > valueFormatter;

    public SceneNodeSliderWithNameAndValue( String groupName, String name, double value )
    {
        this( groupName, name, value, s -> String.format( L.getLocale(), "%.3f" , s ) );
    }

    public SceneNodeSliderWithNameAndValue( String groupName, String name, double value, Function< Double, String > valueFormatter )
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
            () -> valueFormatter.apply( slider.getValue() ),
            slider.valueProperty(), L.localeProperty()
        ) );

        setPickOnBounds( false );

        FXUtils.resizeRelocateTo( nameLabel, FXUtils.setVisible( Main.fxmlLookup( "#" + groupName + "_Name" ), false ) );
        FXUtils.resizeRelocateTo( valueLabel, FXUtils.setVisible( Main.fxmlLookup( "#" + groupName + "_Value" ), false ) );

        nameLabel.setText( name );

        slider.visibleProperty().bind( visibleProperty() );
        nameLabel.visibleProperty().bind( visibleProperty() );
        valueLabel.visibleProperty().bind( visibleProperty() );

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
