package de.mfo.surfer.control;

import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.mfo.surfer.util.L;

public class SceneNodeSliderPanel extends Region
{
    private static final Logger logger = LoggerFactory.getLogger( SceneNodeSliderPanel.class );

    SimpleMapProperty< String, Double > parameters;

    SceneNodeSliderWithNameAndValue slider0;
    SceneNodeSliderWithNameAndValue slider1;
    SceneNodeSliderWithNameAndValue slider2;
    SceneNodeSliderWithNameAndValue slider3;
    SceneNodeSliderWithNameAndValue sliderZoom;

    public SceneNodeSliderPanel()
    {
        setPickOnBounds( false );

        parameters = new SimpleMapProperty< String, Double >( FXCollections.< String, Double >observableHashMap() );

        slider0 = new SceneNodeSliderWithNameAndValue( "Slider_A", "a", 1.0 );
        slider1 = new SceneNodeSliderWithNameAndValue( "Slider_B", "b", 1.0 );
        slider2 = new SceneNodeSliderWithNameAndValue( "Slider_C", "c", 1.0 );
        slider3 = new SceneNodeSliderWithNameAndValue( "Slider_D", "d", 1.0 );
        sliderZoom = new SceneNodeSliderWithNameAndValue(
            "Slider_Zoom",
            "zoom",
            1.0,
            v -> String.format( L.getLocale(), "%.3gx", Math.pow( 10.0, v ) )
        );
        sliderZoom.getSlider().setMin( -2.0 );
        sliderZoom.getSlider().setMax( 2.0 );

        bindSliderToParameterBidirectional( slider0, "a" );
        bindSliderToParameterBidirectional( slider1, "b" );
        bindSliderToParameterBidirectional( slider2, "c" );
        bindSliderToParameterBidirectional( slider3, "d" );
        bindSliderToParameterBidirectional( sliderZoom, "scale_factor" );

        getChildren().addAll( slider0, slider1, slider2, slider3, sliderZoom );
    }

    private void bindSliderToParameterBidirectional( SceneNodeSliderWithNameAndValue s, String p )
    {
        parameters.addListener( new MapChangeListener< String, Double >()
            {
                @Override
                public void onChanged( Change<? extends String,? extends Double> change )
                {
                    if( change.wasAdded() && change.getKey().equals( p ) )
                        s.getSlider().setValue( change.getValueAdded() );
                }
            }
        );
        s.getSlider().valueProperty().addListener( ( p0, p1, newValue ) -> parameters.put( p, newValue.doubleValue() ) );
    }

    public ObservableMap< String, Double > getParameters()
    {
        return parameters.getValue();
    }

    public void setParameters( ObservableMap< String, Double > value )
    {
        parameters.setValue( value );
    }

    public MapProperty< String, Double > parametersProperty()
    {
        return parameters;
    }
}
