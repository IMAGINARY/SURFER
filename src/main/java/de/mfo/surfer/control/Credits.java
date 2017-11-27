package de.mfo.surfer.control;

import de.mfo.surfer.BuildConfig;
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

    public class BuildConfigWrapper
    {
        public String getField( String fieldName ) {
            try {
                return (String) BuildConfig.class.getField( fieldName ).get( null );
            }
            catch( Exception e )
            {
                return "<" + fieldName + ">";
            }
        }
    }

    private Localizer localizer;
    private BuildConfigWrapper buildConfigWrapper;

    public Credits()
    {
        WebView webView = new WebView();
        webView.setMinWidth( 1920.0 );
        webView.setMinHeight( 1080.0 );
        webView.setDisable( true );
        webView.setMouseTransparent( true );

        WebEngine webEngine = webView.getEngine();
        webEngine.setUserStyleSheetLocation(
            Credits.class.getResource( "/de/mfo/surfer/css/credits.css" ).toExternalForm()
        );

        localizer = new Localizer();
        buildConfigWrapper = new BuildConfigWrapper();

        webEngine.getLoadWorker().stateProperty().addListener(
            ( observable, oldValue, newValue ) -> {
                if( newValue == State.SUCCEEDED )
                {
                    JSObject window = ( JSObject ) webEngine.executeScript( "window" );
                    window.setMember( "L", localizer );
                    window.setMember( "BC", buildConfigWrapper );
                    window.call("generate" );
                }
            }
        );

        webEngine.load( Credits.class.getResource( "/de/mfo/surfer/credits/credits.html" ).toExternalForm() );

        L.localeProperty().addListener( ( observable, oldValue, newValue ) -> webEngine.reload() );

        getChildren().add( webView );
    }
}
