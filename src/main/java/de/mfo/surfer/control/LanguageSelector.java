package de.mfo.surfer.control;

import de.mfo.surfer.Main;
import de.mfo.surfer.util.FXUtils;
import de.mfo.surfer.util.L;
import java.util.HashMap;
import java.util.Locale;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

// TODO: should scale with scene size
public class LanguageSelector extends ChoiceBox< Locale >
{
    public LanguageSelector()
    {
        FXUtils.resizeRelocateTo( this, Main.fxmlLookup( "#Button_Language" ) );

        // TODO: Maybe implementing a ChoiceBox skin (JavaFX 9?) fixes the strange and unreliable behavior that is caused by scaling the popup via -fx-font-size
        styleProperty().bind(Bindings.createStringBinding( () -> ( "-fx-font-size: " + localToSceneTransformProperty().get().getMxx() * 24.0 ) + "px;", localToSceneTransformProperty() ) );

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
        return LanguageSelector.class.getResource( "/de/mfo/surfer/css/style.css" ).toExternalForm();
    }
}
