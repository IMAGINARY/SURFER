package de.mfo.surfer.control;

import de.mfo.surfer.BuildConfig;
import de.mfo.surfer.util.L;
import de.mfo.surfer.util.Utils;
import de.mfo.surfer.util.WebConsole;
import javafx.concurrent.Worker.State;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

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
                logger.debug(fieldName);
                return "" + BuildConfig.class.getField( fieldName ).get( null );
            }
            catch( Exception e )
            {
                return "<" + fieldName + ">";
            }
        }

        // TODO: exchange image
        public List<String> getGalleryAuthors() {

            List<String> lines = new LinkedList<>();
            try {
                InputStream is = Credits.class.getResourceAsStream("/de/mfo/surfer/gallery/AUTHORS");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }

                String[] linesArray = lines.toArray(new String[lines.size()]);
                for( int i = 0; i < linesArray.length; ++i ) {
                    linesArray[i]=linesArray[i].replaceAll("\\s*\\(.*\\)\\s*","");
                    if(linesArray[i].matches("^\\s*-+")) {
                        if(i > 0)
                            linesArray[i-1] = null;
                        linesArray[i] = null;
                    } else if(linesArray[i].matches("^\\s*$")||linesArray[i].length()>80||linesArray[i].matches("^\\s*$")) {
                        linesArray[i] = null;
                    }
                }

                lines = Arrays.stream(linesArray).filter(s->s!=null).collect(Collectors.toList());
                lines = lines
                    .stream()
                    .distinct()
                    .sorted(Comparator.comparing(s -> Arrays.stream(s.split(" ")).reduce((first, second) -> second).orElse(null)))
                    .collect(Collectors.toList());
            } catch( IOException ioe )
            {
                Utils.wrapInRte( () -> { throw ioe; } );
            }

            return lines;
        }
    }

    private Localizer localizer;
    private BuildConfigWrapper buildConfigWrapper;

    public class Console {
        public void log(Object o) {logger.debug("{}",o);}
    }

    public Credits()
    {
        WebView webView = new WebView();
        webView.setMinWidth( 1920.0 );
        webView.setMinHeight( 1080.0 );
        webView.setDisable( true );
        webView.setMouseTransparent( true );

        WebEngine webEngine = webView.getEngine();
        localizer = new Localizer();
        buildConfigWrapper = new BuildConfigWrapper();

        webEngine.getLoadWorker().stateProperty().addListener(
            ( observable, oldValue, newValue ) -> {
                if( newValue == State.SUCCEEDED )
                {
                    JSObject window = ( JSObject ) webEngine.executeScript( "window" );
                    window.setMember( "console", WebConsole.get( Credits.class ));
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
