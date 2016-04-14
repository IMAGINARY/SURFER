package de.mfo.surfer.control;

import de.mfo.surfer.Main;
import de.mfo.surfer.util.L;
import javafx.concurrent.Worker.State;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Credits extends Region
{
    private static final Logger logger = LoggerFactory.getLogger( Credits.class );

    public class Localizer
    {
        public String localize( String key ) { return L.l( key ); }
    }

    public Credits()
    {
        WebView webView = new WebView();
        webView.setMinWidth( 1920.0 );
        webView.setMinHeight( 1080.0 );
        webView.setDisable( true );
        webView.setMouseTransparent( true );

        WebEngine webEngine = webView.getEngine();
        webEngine.setUserStyleSheetLocation(
            Credits.class.getResource( "../css/credits.css" ).toExternalForm()
        );

        webEngine.getLoadWorker().stateProperty().addListener(
            ( observable, oldValue, newValue ) ->
            {
                if( newValue == State.SUCCEEDED )
                {
                    JSObject jsobj = ( JSObject ) webEngine.executeScript( "window" );
                    jsobj.setMember( "L", new Localizer() );
                    webEngine.executeScript( "localize('colors')" );
                }
            }
        );

        webEngine.load( Credits.class.getResource( "../credits/credits.html" ).toExternalForm() );

        L.localeProperty().addListener( ( observable, oldValue, newValue ) -> webEngine.reload() );

        getChildren().add( webView );
    }
}
