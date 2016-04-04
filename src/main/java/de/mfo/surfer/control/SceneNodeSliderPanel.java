package de.mfo.surfer.control;

import javafx.scene.layout.Region;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SceneNodeSliderPanel extends Region
{
    SimpleDoubleProperty a;
    SimpleDoubleProperty b;
    SimpleDoubleProperty c;
    SimpleDoubleProperty d;
    SimpleDoubleProperty zoom;

    SceneNodeSliderWithNameAndValue slider1;
    SceneNodeSliderWithNameAndValue slider2;
    SceneNodeSliderWithNameAndValue slider3;
    SceneNodeSliderWithNameAndValue slider4;
    SceneNodeSliderWithNameAndValue sliderZoom;

    public SceneNodeSliderPanel()
    {
        SceneNodeSliderWithNameAndValue slider0 = new SceneNodeSliderWithNameAndValue( "Slider_A", "a", 1.0 );
        SceneNodeSliderWithNameAndValue slider1 = new SceneNodeSliderWithNameAndValue( "Slider_B", "b", 1.0 );
        SceneNodeSliderWithNameAndValue slider2 = new SceneNodeSliderWithNameAndValue( "Slider_C", "c", 1.0 );
        SceneNodeSliderWithNameAndValue slider3 = new SceneNodeSliderWithNameAndValue( "Slider_D", "d", 1.0 );
        SceneNodeSliderWithNameAndValue sliderZoom = new SceneNodeSliderWithNameAndValue( "Slider_Zoom", "zoom", 1.0 );

        a = new SimpleDoubleProperty();
        b = new SimpleDoubleProperty();
        c = new SimpleDoubleProperty();
        d = new SimpleDoubleProperty();
        zoom = new SimpleDoubleProperty();

        a.bindBidirectional( slider0.getSlider().valueProperty() );
        b.bindBidirectional( slider1.getSlider().valueProperty() );
        c.bindBidirectional( slider2.getSlider().valueProperty() );
        d.bindBidirectional( slider3.getSlider().valueProperty() );
        zoom.bindBidirectional( sliderZoom.getSlider().valueProperty() );

        getChildren().addAll( slider0, slider1, slider2, slider3, sliderZoom );
    }

    // a
    public double getA()
    {
        return a.getValue();
    }

    public void setA( double value )
    {
        a.setValue( value );
    }

    public DoubleProperty aProperty()
    {
        return a;
    }

    // b
    public double getB()
    {
        return b.getValue();
    }

    public void setB( double value )
    {
        b.setValue( value );
    }

    public DoubleProperty bProperty()
    {
        return b;
    }

    // c
    public double getC()
    {
        return c.getValue();
    }

    public void setC( double value )
    {
        c.setValue( value );
    }

    public DoubleProperty cProperty()
    {
        return c;
    }

    // d
    public double getD()
    {
        return d.getValue();
    }

    public void setD( double value )
    {
        d.setValue( value );
    }

    public DoubleProperty dProperty()
    {
        return d;
    }

    // zoom
    public double getZoom()
    {
        return zoom.getValue();
    }

    public void setZoom( double value )
    {
        zoom.setValue( value );
    }

    public DoubleProperty zoomProperty()
    {
        return zoom;
    }
}
