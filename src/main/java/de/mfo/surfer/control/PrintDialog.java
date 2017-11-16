package de.mfo.surfer.control;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.mfo.surfer.util.Preferences;
import de.mfo.surfer.util.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.print.*;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Window;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.util.Scanner;

public class PrintDialog extends Dialog< ButtonType >
{
    private static final Logger logger = LoggerFactory.getLogger( PrintDialog.class );

    static final boolean debugWebView = false;

    static final String resourceName = "/de/mfo/surfer/printing/index.html";

    // in order to debug the SVG rendering, you need to run a HTTP server that serves index.html just like this:
    static final String debugURL = "http://[::1]:8888/src/main/resources/de/mfo/surfer/printing/index.html";

    WebView webView;
    PrinterJob printerJob;

    public class JavaBridge {

        SnapshotParameters snapshotParameters;

        public void svgCallback( String svg, double svgWidth, double svgHeight )
        {
            if( debugWebView ) {
                logger.debug("{}x{}: {}", svg, svgWidth, svgHeight);
                return;
            }

            WebEngine webEngine = webView.getEngine();
            Pane webViewWrapper = new Pane(webView);

            // add the WebView to the scene, but completely clip it to make it invisible
            // (it still gets rendered so we can take a snapshot later on)
            webViewWrapper.setClip(new Rectangle());
            getDialogPane().getChildren().add(webViewWrapper);

            // do this once SVG loading finished
            webEngine.getLoadWorker().stateProperty().addListener( new ChangeListener< Worker.State > () {
                @Override
                public void changed( ObservableValue<? extends Worker.State> observable, Worker.State oldState, Worker.State newState ) {
                    if (newState == Worker.State.SUCCEEDED) {
                        // remove this listener
                        webEngine.getLoadWorker().stateProperty().removeListener(this);

                        //
                        Window window = getDialogPane().getScene().getWindow();
                        Rectangle2D vb = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight()).get(0).getVisualBounds();
                        double webViewTargetSize = Math.min( vb.getWidth(), vb.getHeight() ) / 2.0;
                        double webViewTargetWidth, webViewTargetHeight;
                        if( vb.getHeight() < vb.getWidth() )
                        {
                            // landscape mode
                            webViewTargetHeight = webViewTargetSize;
                            webViewTargetWidth = ( webViewTargetHeight * svgWidth ) / svgHeight;
                        }
                        else
                        {
                            // portrait mode
                            webViewTargetWidth = webViewTargetSize;
                            webViewTargetHeight = ( webViewTargetWidth * svgHeight ) / svgWidth;
                        }

                        // adjust SVG size
                        fitWebViewSvgTo( webViewTargetWidth, webViewTargetHeight );

                        // round up to next integer values
                        webViewTargetWidth = Math.ceil( webViewTargetWidth );
                        webViewTargetHeight = Math.ceil( webViewTargetHeight );

                        // resize webView according to target size
                        webView.setMinWidth( webViewTargetWidth );
                        webView.setMinHeight( webViewTargetHeight );
                        webView.setMaxWidth( webViewTargetWidth );
                        webView.setMaxHeight( webViewTargetHeight );

                        // prepare taking a snapshot of the WebView (convert SVG to raster graphics)
                        snapshotParameters = new SnapshotParameters();
                        snapshotParameters.setViewport(new Rectangle2D(0, 0, webViewTargetWidth, webViewTargetHeight));

                        // wait for the WebView to finish rendering of the SVG and call back to take the snapshot
                        JSObject svgWindow = (JSObject) webEngine.executeScript("window");
                        svgWindow.setMember("console", Console.getConsole());
                        svgWindow.setMember("javaBridge", JavaBridge.this);
                        webEngine.executeScript("window.requestAnimationFrame( function() { javaBridge.takeSnapshot(); } );");
                    }
                }
            } );

            webEngine.loadContent( svg, "image/svg+xml" );
        }

        public void takeSnapshot()
        {
            // convert SVG to raster graphics by taking snapshot of the WebView
            webView.snapshot( snapshotResult -> {
                    // display rendering of SVG image
                    Image image = snapshotResult.getImage();
                    ImageView imageView = new ImageView( image );
                    BorderPane bp = new BorderPane( imageView );
                    bp.setMaxSize( image.getWidth(), image.getHeight() );
                    bp.setStyle( "-fx-border-color: gray; -fx-border-style: solid; -fx-border-width: 1px;" );
                    getDialogPane().setContent( new BorderPane( bp ));

                    // enable the dialog buttons again
                    getDialogPane().getButtonTypes().forEach( bt -> getDialogPane().lookupButton( bt ).setDisable( false ) );

                    // resize and center dialog
                    getDialogPane().getScene().getWindow().sizeToScene();
                    getDialogPane().getScene().getWindow().centerOnScreen();

                    return (Void) null;
                },
                snapshotParameters,
                null
            );
        }
    }

    public static class Console
    {
        private static final Logger consoleLogger = LoggerFactory.getLogger( Console.class );
        private static final Console console = new Console();

        public void log( Object o )
        {
            consoleLogger.debug( "{}", o );
        }

        private Console() {}

        public static Console getConsole() {
            return console;
        }
    }

    public PrintDialog( Window owner, PrinterJob printJob, String formula, String image )
    {
        super();
        initOwner( owner );

        this.printerJob = printJob;
        setHeaderText( null );

        ButtonType pageSetupDialogButton = new ButtonType( "Page Setup" );
        ButtonType printDialogButton = new ButtonType( "Print Dialog" );
        getDialogPane().getButtonTypes().addAll(
            ButtonType.OK,
            ButtonType.CANCEL,
            pageSetupDialogButton,
            printDialogButton
        );
        getDialogPane().getButtonTypes().forEach( bt -> getDialogPane().lookupButton( bt ).setDisable( true ) );


        getDialogPane().lookupButton( pageSetupDialogButton ).addEventFilter(
            ActionEvent.ACTION,
            e -> { printJob.showPageSetupDialog( null ); e.consume(); }
        );
        getDialogPane().lookupButton( printDialogButton ).addEventFilter(
            ActionEvent.ACTION,
            e -> { printJob.showPrintDialog( null ); e.consume(); }
        );

        setContentText( "Processing ..." );
        initProcessing( formula, image ); // TODO: delayed loading to avoid blocking the UI

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK)
                print();
            return dialogButton;
        });
    }

    void initProcessing( String formula, String image ) {
        webView = new WebView();

        WebEngine webEngine = webView.getEngine();
        if( debugWebView )
            webEngine.setUserAgent( "SURFER debug" );
        else
            webEngine.setUserAgent( "SURFER" );

        webEngine.getLoadWorker().stateProperty().addListener( new ChangeListener< Worker.State > () {
            @Override
            public void changed( ObservableValue<? extends Worker.State> observable, Worker.State oldState, Worker.State newState ) {
                if (newState == Worker.State.SUCCEEDED) {
                    // create the SVG via JavaScript
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("console", Console.getConsole() );
                    window.setMember("javaBridge", new JavaBridge());

                    File externalSVGTemplate = Preferences.General.printTemplateFileProperty().get();
                    if( externalSVGTemplate != null )
                        Utils.wrapInRte( () -> {
                            window.call( "setTemplateSVG", new String(Files.readAllBytes(externalSVGTemplate.toPath())));
                            return null;
                        } );

                    JSObject callback = (JSObject) webEngine.executeScript("(function( svgElem  ) { javaBridge.svgCallback( svgSourceWithXlinkHref( svgElem ), svgElem.getAttribute( 'width' ), svgElem.getAttribute( 'height' ) ); })");
                    window.call("createSVG", image, toLaTeX(formula + "=0"), callback);

                    // remove this listener
                    webEngine.getLoadWorker().stateProperty().removeListener(this);
                }
            }
        } );

        webEngine.setOnError(e -> logger.debug( "{}", e ) );

        if( debugWebView ) {
            setResizable( true );
            getDialogPane().setContent( webView );
            webEngine.load( debugURL );
            logger.debug( webEngine.getLocation() );
        }
        else
        {
            // pass HTML content into the webEngine instead of the path to index.html because otherwise it won't resolve file:// URLs
            String html = new Scanner(PrintDialog.class.getResourceAsStream( resourceName ), "UTF-8").useDelimiter("\\A").next();
            webEngine.loadContent(html);
        }
    }

    void print() {
        double dpi;
        {
            Window window = this.getDialogPane().getScene().getWindow();

            Screen screen = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight()).get(0);
            dpi = screen.getDpi();
        }

        JobSettings jobSettings = printerJob.getJobSettings();

        Paper paper = jobSettings.getPageLayout().getPaper();
        double targetWidth = paper.getWidth() * ( dpi / 72.0 );
        double targetHeight = paper.getHeight() * ( dpi / 72.0 );

        Printer printer = printerJob.getPrinter();
        PageLayout pageLayout = printer.createPageLayout( paper, PageOrientation.PORTRAIT, 0, 0, 0, 0 );

        jobSettings.setPageLayout( pageLayout );
        jobSettings.setPageRanges( new PageRange( 1, 1 ) );

        fitWebViewSvgTo( targetWidth, targetHeight );

        webView.getEngine().print( printerJob );

        // TODO: deal with print errors
    }

    void fitWebViewSvgTo( double targetWidth, double targetHeight )
    {
        JSObject svgElem = (JSObject) webView.getEngine().executeScript("window.document.querySelector(\"svg\")");
        double svgWidth = Double.parseDouble( (String) svgElem.call( "getAttribute", "width" ) );
        double svgHeight = Double.parseDouble( (String) svgElem.call( "getAttribute", "height" ) );

        double scale = Math.min( targetWidth / svgWidth, targetHeight / svgHeight );

        JSObject svgElemStyle = (JSObject) svgElem.getMember( "style" );
        svgElemStyle.setMember( "transformOrigin", "top left" );
        svgElemStyle.setMember( "transform", "scale(" + scale + "," + scale + ")" );
    }

    public static String toLaTeX( String formula )
    {
        // this isn't a very efficient implementation, but it happens only once per
        // per print job, so it should be OK

        if( debugWebView )
            logger.debug( formula );

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

        // turn \sqrt[...] into \sqrt{...}
        formula = iterativeReplaceAll(formula,"sqrt\\[([^\\[\\]]*)]","sqrt\\{$1\\}");

        // turn [...] into \left(...\right)
        formula = iterativeReplaceAll(formula,"\\[([^\\[\\]]*)]","\\\\left($1\\\\right)");

        if( debugWebView )
            logger.debug( formula );

        return formula;
    }

    private static String iterativeReplaceAll(String text, String regex, String replacement) {
        boolean match;
        do {
            String newText = text;

            newText = newText.replaceAll(regex,replacement);

            // inefficient way to check if an actual replacement occurred
            match = !newText.equals(text);

            text = newText;
        }
        while( match );

        return text;
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
