package de.mfo.surfer.control;

import de.mfo.surfer.util.L;
import de.mfo.surfer.util.Preferences;
import java.util.TreeSet;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static de.mfo.surfer.util.L.getLocale;

public class SceneNodeSliderPanel extends Region
{
    private static final Logger logger = LoggerFactory.getLogger( SceneNodeSliderPanel.class );

    SimpleMapProperty< String, Double > parameters;

    SceneNodeSliderWithNameAndValue[] sliderABCD;
    String[] assignment;

    SceneNodeSliderWithNameAndValue sliderZoom;

    public SceneNodeSliderPanel()
    {
        setPickOnBounds( false );

        assignment = new String[ 4 ];

        parameters = new SimpleMapProperty< String, Double >( FXCollections.< String, Double >observableHashMap() );

        sliderABCD = new SceneNodeSliderWithNameAndValue[]
        {
            new SceneNodeSliderWithNameAndValue( "Slider_A", "a", 1.0 ),
            new SceneNodeSliderWithNameAndValue( "Slider_B", "b", 1.0 ),
            new SceneNodeSliderWithNameAndValue( "Slider_C", "c", 1.0 ),
            new SceneNodeSliderWithNameAndValue( "Slider_D", "d", 1.0 )
        };
        sliderZoom = new SceneNodeSliderWithNameAndValue(
            "Slider_Zoom",
            "zoom",
            1.0,
            v -> String.format( getLocale(), "%.3gx", Math.pow( 10.0, v ) )
        );
        sliderZoom.getSlider().minProperty().bind( Preferences.Limits.minScaleFactorProperty() );
        sliderZoom.getSlider().maxProperty().bind( Preferences.Limits.maxScaleFactorProperty() );
        sliderZoom.getNameLabel().textProperty().bind( L.lb( "zoom" ) );

        initListeners();
        assignParametersToSliders();

        getChildren().addAll( sliderABCD );
        getChildren().add( sliderZoom );
    }

    private void initListeners()
    {
        // listener that recreates the assignment of parameters to sliders
        parameters.addListener( new MapChangeListener< String, Double >()
            {
                @Override
                public void onChanged( Change<? extends String,? extends Double> change ) {
                    if( ( change.wasAdded() || change.wasRemoved() ) && !( change.wasAdded() && change.wasRemoved() ) )
                        assignParametersToSliders();
                }
            }
        );

        // listeners that propagate changes of slider values to the parameter map and vice versa
        parameters.addListener( new MapChangeListener< String, Double >()
            {
                @Override
                public void onChanged( Change<? extends String,? extends Double> change )
                {
                    if( change.wasAdded() )
                    {
                        for( int i = 0; i < 4; i++ )
                        {
                            if( change.getKey().equals( assignment[ i ] ) )
                            {
                                sliderABCD[ i ].getSlider().setValue( change.getValueAdded() );
                                break;
                            }
                        }
                    }
                }
            }
        );

        for( int i = 0; i < 4; i++ )
        {
            final int fi = i;
            sliderABCD[ fi ].getSlider().valueProperty().addListener( ( p0, p1, newValue ) -> parameters.put( assignment[ fi ], newValue.doubleValue() ) );
        }

        // same for the zoom slider
        parameters.addListener( new MapChangeListener< String, Double >()
            {
                @Override
                public void onChanged( Change<? extends String,? extends Double> change )
                {
                    if( change.wasAdded() && change.getKey().equals( "scale_factor" ) )
                        sliderZoom.getSlider().setValue( change.getValueAdded() );
                }
            }
        );
        sliderZoom.getSlider().valueProperty().addListener( ( p0, p1, newValue ) -> parameters.put( "scale_factor", newValue.doubleValue() ) );
    }

    private void assignParametersToSliders()
    {
        // unassign sliders
        for( int i = 0; i < 4; i++ )
        {
            sliderABCD[ i ].setVisible( false );
            assignment[ i ] = "";
        }

        // retrieve new parameter names
        TreeSet< String > occuringABCDParams_tmp = new TreeSet< String >();
        parameters.forEach( ( k, v ) -> { if( k.equals( "a" ) || k.equals( "b" ) || k.equals( "c" ) || k.equals( "d" ) ) occuringABCDParams_tmp.add( k ); } );

        // assign occuring parameters to sliders
        String[] occuringABCDParams = occuringABCDParams_tmp.toArray( new String[ occuringABCDParams_tmp.size() ] );
        for( int paramIndex = 0; paramIndex < occuringABCDParams.length; paramIndex++ )
        {
            int sliderIndex = 4 - occuringABCDParams.length + paramIndex;
            assignment[ sliderIndex ] = occuringABCDParams[ paramIndex ];
            sliderABCD[ sliderIndex ].getNameLabel().setText( assignment[ sliderIndex ] );
            sliderABCD[ sliderIndex ].setVisible( true );
        }
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
