package de.mfo.surfer.control;

import de.mfo.surfer.util.L;
import java.util.Map;
import java.util.TreeSet;
import javafx.beans.binding.Bindings;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static de.mfo.surfer.util.L.getLocale;

import javafx.beans.value.ChangeListener;

public class SceneNodeSliderPanel extends Region
{
    private static final Logger logger = LoggerFactory.getLogger( SceneNodeSliderPanel.class );

    SimpleMapProperty< String, Double > parameters;

    SceneNodeSliderWithNameAndValue[] sliderABCD;
    MapChangeListener< String, Double >[] mapListernersABCD;
    ChangeListener< Number >[] sliderListenersABCD;
    MapChangeListener< String, Double > dummyMapListerner;
    ChangeListener< Number > dummySliderListener;

    SceneNodeSliderWithNameAndValue sliderZoom;

    public SceneNodeSliderPanel()
    {
        setPickOnBounds( false );

        parameters = new SimpleMapProperty< String, Double >( FXCollections.< String, Double >observableHashMap() );

        parameters.addListener( new MapChangeListener< String, Double >()
            {
                @Override
                public void onChanged( Change<? extends String,? extends Double> change ) {
                    if( ( change.wasAdded() || change.wasRemoved() ) && !( change.wasAdded() && change.wasRemoved() ) )
                        rebindSlidersToParametersBidirectional();
                }
            }
        );

        dummyMapListerner = new MapChangeListener< String, Double >()
            {
                @Override
                public void onChanged( Change<? extends String,? extends Double> change ) {}
            };
        dummySliderListener = ( p0, p1, p2 ) -> {};

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
        sliderZoom.getSlider().setMin( -2.0 );
        sliderZoom.getSlider().setMax( 2.0 );
        sliderZoom.getNameLabel().textProperty().bind( L.lb( "zoom" ) );

        mapListernersABCD = new MapChangeListener[ 4 ];
        sliderListenersABCD = new ChangeListener[ 4 ];
        for( int i = 0; i < 4; i++ )
        {
            mapListernersABCD[ i ] = dummyMapListerner;
            sliderListenersABCD[ i ] = dummySliderListener;
        }

        rebindSlidersToParametersBidirectional();

        bindSliderToParameterBidirectional( sliderZoom, "scale_factor" );

        getChildren().addAll( sliderABCD );
        getChildren().add( sliderZoom );
    }

    private void rebindSlidersToParametersBidirectional()
    {
        // unbind
        for( int i = 0; i < 4; i++ )
        {
            parameters.removeListener( mapListernersABCD[ i ] );
            sliderABCD[ i ].getSlider().valueProperty().asObject().removeListener( sliderListenersABCD[ i ] );
        }

        // retrieve new parameter names
        TreeSet< String > occuringABCDParams_tmp = new TreeSet< String >();
        parameters.forEach( ( k, v ) -> { if( k.equals( "a" ) || k.equals( "b" ) || k.equals( "c" ) || k.equals( "d" ) ) occuringABCDParams_tmp.add( k ); } );

        // bind unassigned parameters to dummy listeners for consistency
        for( int i = 0; i < 4 - occuringABCDParams_tmp.size(); i++ )
        {
            mapListernersABCD[ i ] = dummyMapListerner;
            sliderListenersABCD[ i ] = dummySliderListener;
            parameters.addListener( dummyMapListerner );
            sliderABCD[ i ].getSlider().valueProperty().addListener( dummySliderListener );
            sliderABCD[ i ].setVisible( false );
        }

        // bind occuring parameters
        String[] occuringABCDParams = occuringABCDParams_tmp.toArray( new String[ occuringABCDParams_tmp.size() ] );
        for( int paramIndex = 0; paramIndex < occuringABCDParams.length; paramIndex++ )
        {
            int sliderIndex = 4 - occuringABCDParams.length + paramIndex;
            SceneNodeSliderWithNameAndValue s = sliderABCD[ sliderIndex ];
            String p = occuringABCDParams[ paramIndex ];
            logger.debug( "{}", p );
            mapListernersABCD[ sliderIndex ] = new MapChangeListener< String, Double >()
                {
                    @Override
                    public void onChanged( Change<? extends String,? extends Double> change )
                    {
                        if( change.wasAdded() && change.getKey().equals( p ) )
                            s.getSlider().setValue( change.getValueAdded() );
                    }
                };
            sliderListenersABCD[ sliderIndex ] = ( p0, p1, newValue ) -> parameters.put( p, newValue.doubleValue() );

            parameters.addListener( mapListernersABCD[ sliderIndex ] );
            s.getSlider().valueProperty().addListener( sliderListenersABCD[ sliderIndex ] );
            s.setVisible( true );
            s.getNameLabel().setText( p );
        }
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
