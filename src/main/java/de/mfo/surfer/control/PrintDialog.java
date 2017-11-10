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

    public class JavaBridge {
        public void svgCallback( String svg )
        {
            // TODO: further process SVG image
            //logger.debug( svg );
        }
    }

    public PrintDialog( String formula, String image )
    {
        super();
        setHeaderText( null );
        setResizable( true );

        getDialogPane().getButtonTypes().addAll( ButtonType.APPLY, ButtonType.CANCEL );

        WebView webView = new WebView();
        webView.setMinWidth( 600.0 );
        webView.setMinHeight( 900.0 );

        WebEngine webEngine = webView.getEngine();
        webEngine.setUserAgent("SURFER");

        webEngine.getLoadWorker().stateProperty().addListener(
            ( ov, oldState, newState) -> {
                    if( newState == Worker.State.SUCCEEDED )
                    {
                        // create the SVG via JavaScript
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember( "javaBridge", new JavaBridge() );
                        JSObject callback = (JSObject) webEngine.executeScript("(function( svgElem  ) { javaBridge.svgCallback( svgSourceWithXlinkHref( svgElem ) ); })");
                        window.call("createSVG",image, toLaTeX(formula+"=0"), callback );
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
        // this isn't a very efficient implementation, but it happens only once per
        // per print job, so it should be OK

        // get rid of whitespace
        formula = formula.replaceAll(" ","");

        // proper formatting of powers
        formula = formula.replaceAll("\\^(\\d*)","^{$1}");

        // unary double operators
        // TODO: convert all possible operators (see de.mfo.jsurf.algebra.DoubleUnaryOperation.Op)
        formula = formula.replaceAll("sin","\\\\sin");
        formula = formula.replaceAll("cos","\\\\cos");
        formula = formula.replaceAll("tan","\\\\tan");
        formula = formula.replaceAll("asin","\\\\sin^{-1}");
        formula = formula.replaceAll("acos","\\\\cos^{-1}");
        formula = formula.replaceAll("atan","\\\\tan^{-1}");
        formula = formula.replaceAll("exp","\\\\exp");
        formula = formula.replaceAll("log","\\\\log");
        formula = formula.replaceAll("sqrt","\\\\sqrt");

        // binary double operators
        // TODO: convert all possible operators (see de.mfo.jsurf.algebra.DoubleBinaryOperation.Op)
        formula = formula.replaceAll("atan2","\\tan^{-1}");

        // use a centered dot for multiplications
        formula = formula.replaceAll("\\*","\\\\cdot{}");

        // turn all () into [] for later iterative processing of matching [ and ]
        formula = formula.replaceAll("\\(","[");
        formula = formula.replaceAll("\\)","]");

        // iteratively process all innermost [] pairs
        boolean match;
        do {
            String newFormula = formula;

            // proper formatting of sqrt
            newFormula = newFormula.replaceAll("sqrt\\[([^\\[\\]]*)]","sqrt\\{$1\\}");
            // proper formatting of parentheses
            newFormula = newFormula.replaceAll("\\[([^\\[\\]]*)]","\\\\left($1\\\\right)");

            // inefficient way to check if an actual replacement occurred
            match = !newFormula.equals(formula);

            formula = newFormula;
        }
        while( match );

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
