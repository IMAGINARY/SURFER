package de.mfo.surfer.control;

import de.mfo.surfer.Main;
import de.mfo.surfer.util.FXUtils;
import de.mfo.surfer.util.L;
import java.util.HashMap;
import java.util.Locale;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

public class LanguageSelector extends ChoiceBox< Locale >
{
    public LanguageSelector()
    {
        FXUtils.resizeRelocateTo( this, Main.fxmlLookup( "#Button_Language" ) );
        //setMouseTransparent( true );

        setConverter( new StringConverter< Locale >()
            {
                HashMap< String, Locale > fromStringMap;

                @Override
                public Locale fromString( String string )
                {
                    if( fromStringMap == null )
                    {
                        fromStringMap = new HashMap<>();
                        L.getAvailableLocales().forEach(
                            l -> fromStringMap.put( toString( l ), l )
                        );
                    }
                    return fromStringMap.get( string );
                }

                @Override
                public String toString( Locale locale )
                {
                    String displayName = locale.getDisplayName( locale );

                    // turn first letter into uppercase
                    displayName = java.util.stream.IntStream.concat(
                        java.util.stream.IntStream.of(displayName.codePointAt(0)).map(Character::toUpperCase),
                        displayName.codePoints().skip(1)
                        ).collect(
                            StringBuilder::new,
                            StringBuilder::appendCodePoint,
                            StringBuilder::append
                        ).toString();

                    return //"(" + locale + ") " +
                        displayName;
                }
            }
        );

        ObservableList<Locale> items = getItems();
        L.getAvailableLocales().forEach( l -> items.add( l ) );
        valueProperty().bindBidirectional( L.localeProperty() );
    }

    @Override
    public String getUserAgentStylesheet()
    {
        return LanguageSelector.class.getResource( "../css/style.css" ).toExternalForm();
    }
}
