package de.mfo.surfer.control;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javafx.concurrent.Worker;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.print.PrinterJob;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.function.Consumer;

public class PrintDialog extends Dialog< Consumer< PrinterJob > >
{
    private static final Logger logger = LoggerFactory.getLogger( PrintDialog.class );

    public PrintDialog( String formula, String image )
    {
        super();
        setHeaderText( null );

        getDialogPane().getButtonTypes().addAll( ButtonType.APPLY, ButtonType.CANCEL );

        WebView webView = new WebView();

        WebEngine webEngine = webView.getEngine();
        webEngine.setUserAgent("SURFER");

        webEngine.getLoadWorker().stateProperty().addListener(
            ( ov, oldState, newState) -> {
                    if( newState == Worker.State.SUCCEEDED )
                    {
                        // create the SVG via JavaScript
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        JSObject callback = (JSObject) webEngine.executeScript("(function( svgSource ) { console.log( svgSource ); })");
                        window.call("createSVG",image, formula, callback );
                    }
                }
            );
        webEngine.setOnError(e -> logger.debug( "{}", e ) );

        // pass HTML content into the webEngine instead of the path to index.html because otherwise it won't resolve file:// URLs
        String html = new Scanner(PrintDialog.class.getResourceAsStream( "/de/mfo/surfer/printing/index.html" ), "UTF-8").useDelimiter("\\A").next();
        webEngine.loadContent( html );

        getDialogPane().setContent( webView );

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.APPLY) {
                return job -> webView.getEngine().print(job);
            }
            return null;
        });
    }

    public static String toLaTeX( String formula )
    {
        // TODO: turn SURFER formula into LaTeX formula
        return formula;
    }

    public static String encodeURIComponent( String s )
    {
        try
        {
            return s == null ? null : URLEncoder.encode(s, "UTF-8")
                .replaceAll("\\+", "%20")
                .replaceAll("%21", "!")
                .replaceAll("%27", "'")
                .replaceAll("%28", "(")
                .replaceAll("%29", ")")
                .replaceAll("%7E", "~");
        }
        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            return s;
        }
    }
}
