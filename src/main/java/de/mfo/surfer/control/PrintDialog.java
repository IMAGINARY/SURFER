package de.mfo.surfer.control;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.mfo.jsurf.algebra.*;
import de.mfo.jsurf.parser.AlgebraicExpressionParser;
import de.mfo.surfer.util.L;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public PrintDialog(Window owner, PrinterJob printJob, String formula, Map< String, Double > parameters, String image )
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
        initProcessing( formula, parameters, image ); // TODO: delayed loading to avoid blocking the UI

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK)
                print();
            return dialogButton;
        });
    }

    void initProcessing( String formula, Map< String, Double > parameters, String image ) {
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
                    window.call("createSVG", image, toMathJax(formula,parameters), callback);

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

    public static String toMathJax( String formula, Map< String, Double > parameters )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "\\begin{equation}\n" );

        Utils.wrapInRte( () -> AlgebraicExpressionParser.parse( formula ).accept( new ToLaTeXVisitor(), sb ) );

        // add equation sign and avoid line breaks around it
        sb.append( "\\mmlToken{mo}[linebreak=\"nobreak\"]{=}0" );

        if( parameters.size() > 1 )
        {

            DecimalFormat df = new DecimalFormat("#.###", new DecimalFormatSymbols(L.getLocale()));
            Function<Map.Entry<String,Double>,String> paramFormatter = param -> {
                Double dv = param.getValue();
                String dvString =  df.format( dv );
                char equalOrApprox;
                try {
                    equalOrApprox = dv.equals( df.parse( dvString ).doubleValue() ) ? '=' : '\u2248';
                } catch( Exception e ) {
                    equalOrApprox = '\u2248';
                }
                return param.getKey()
                    + "\\mmlToken{mo}[linebreak=\"nobreak\"]{"
                    + equalOrApprox
                    + "}"
                    + dvString.replaceFirst( ",", "{,}" );
            };

            sb.append( "\\\\[1em]\n" );
            sb.append( parameters
                .entrySet()
                .stream()
                .filter( e -> e.getKey() != "scale_factor" )
                .sorted( ( e1, e2 ) -> e1.getKey().compareTo( e2.getKey() ) )
                .map( paramFormatter )
                .collect(Collectors.joining( ", " ) )
            );
        }

        sb.append( "\n\\end{equation}" );

        return sb.toString();
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

class ToLaTeXVisitor extends AbstractVisitor< StringBuilder, StringBuilder >
{
    public static final String LPAR = "\\left(";
    public static final String RPAR = "\\right)";

    @Override
    public StringBuilder visit(PolynomialAddition pa, StringBuilder sb) {
        lp( pa, sb );
        pa.getFirstOperand().accept( this, sb );
        sb.append( '-' );
        pa.getSecondOperand().accept( this, sb );
        return rp( pa, sb );
    }

    @Override
    public StringBuilder visit(PolynomialSubtraction ps, StringBuilder sb) {
        lp( ps, sb );
        ps.getFirstOperand().accept( this, sb );
        sb.append( '-' );
        ps.getSecondOperand().accept( this, sb );
        return rp( ps, sb );
    }

    @Override
    public StringBuilder visit(PolynomialMultiplication pm, StringBuilder sb) {
        lp( pm, sb );
        pm.getFirstOperand().accept( this, sb );
        sb.append( "\\cdot{}" );
        pm.getSecondOperand().accept( this, sb );
        return rp( pm, sb );
    }

    @Override
    public StringBuilder visit(PolynomialPower pp, StringBuilder sb) {
        lp( pp, sb );
        sb.append( '{' );
        pp.getBase().accept( this, sb );
        sb.append( "}^{" ).append( pp.getExponent() ).append( '}' );
        return rp( pp, sb );
    }

    @Override
    public StringBuilder visit(PolynomialNegation pn, StringBuilder sb) {
        lp( pn, sb );
        sb.append( '-' );
        pn.getOperand().accept( this, sb );
        return rp( pn, sb );
    }

    @Override
    public StringBuilder visit(PolynomialDoubleDivision pdd, StringBuilder sb) {
        lp( pdd, sb );
        sb.append( "\\frac{" );
        pdd.getDividend().accept( this, sb );
        sb.append( "}{" );
        pdd.getDivisor().accept( this, sb );
        sb.append( '}' );
        return rp( pdd, sb );
    }

    @Override
    public StringBuilder visit(PolynomialVariable pv, StringBuilder sb) {
        lp( pv, sb );
        sb.append( pv.getVariable().toString() );
        return rp( pv, sb );
    }

    @FunctionalInterface
    private interface ComposeBinary {
        void accept( String prefix, String infix, String suffix, boolean removeOuterParentheses);
    }

    @Override
    public StringBuilder visit(DoubleBinaryOperation dbop, StringBuilder sb) {
        lp( dbop, sb );

        ComposeBinary composeBinary = ( prefix, infix, suffix, removeOuterParentheses ) -> {
            sb.append( prefix );
            if( removeOuterParentheses )
                sb.append( removeOuterParentheses( dbop.getFirstOperand().accept(this, new StringBuilder() ) ) );
            else
                dbop.getFirstOperand().accept(this, sb);
            sb.append( infix );
            if( removeOuterParentheses )
                sb.append( removeOuterParentheses( dbop.getSecondOperand().accept(this, new StringBuilder() ) ) );
            else
                dbop.getSecondOperand().accept(this, sb);

            sb.append( suffix );
        };

        // TODO
        switch( dbop.getOperator() )
        {
            case add:
                composeBinary.accept( "", "+", "", false );
                break;
            case sub:
                composeBinary.accept( "", "-", "", false );
                break;
            case mult:
                composeBinary.accept( "", "\\cdot{}", "", false );
                break;
            case div:
                composeBinary.accept( "\\frac{", "}{", "}", false );
                break;
            case pow:
                composeBinary.accept( "{", "}^{", "}", false );
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return rp( dbop, sb );
    }

    @FunctionalInterface
    private interface ComposeUnary {
        void accept( String prefix, String suffix, boolean removeOuterParentheses);
    }


    @Override
    public StringBuilder visit(DoubleUnaryOperation duop, StringBuilder sb) {
        lp( duop, sb );

        ComposeUnary composeUnary = ( prefix, suffix, removeOuterParentheses ) -> {
            sb.append( prefix );
            if( removeOuterParentheses )
                sb.append( removeOuterParentheses( duop.getOperand().accept(this, new StringBuilder() ) ) );
            else
                duop.getOperand().accept(this, sb);
            sb.append( suffix );
        };

        switch( duop.getOperator() )
        {
            case neg:
                composeUnary.accept( "-", "", false );
                break;
            case sin:
                composeUnary.accept( "\\sin", "", false );
                break;
            case cos:
                composeUnary.accept( "\\cos", "", false );
                break;
            case tan:
                composeUnary.accept( "\\tan", "", false );
                break;
            case asin:
                composeUnary.accept( "\\sin^{-1}", "", false );
                break;
            case acos:
                composeUnary.accept( "\\cos^{-1}", "", false );
                break;
            case atan:
                composeUnary.accept( "\\tan^{-1}", "", false );
                break;
            case exp:
                composeUnary.accept( "\\operatorname{e}^{", "}", true );
                break;
            case log:
                composeUnary.accept( "\\ln", "", false );
                break;
            case sqrt:
                composeUnary.accept( "\\sqrt{", "}", true );
                break;
            case ceil:
                composeUnary.accept( "\\lceil", "\\rceil", true );
                break;
            case floor:
                composeUnary.accept( "\\lfloor", "\\rfloor", true );
                break;
            case abs:
                composeUnary.accept( "\\left|", "\\right|", true );
                break;
            case sign:
                composeUnary.accept( "\\operatorname{sgn}", "", false );
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return rp( duop, sb );
    }

    @Override
    public StringBuilder visit(DoubleValue dv, StringBuilder sb) {
        lp( dv, sb );
        sb.append( dv.toString() );
        return rp( dv, sb );
    }

    @Override
    public StringBuilder visit(DoubleVariable dv, StringBuilder sb) {
        lp( dv, sb );
        sb.append( dv.getName() );
        return rp( dv, sb );
    }

    private static StringBuilder lp( PolynomialOperation pop, StringBuilder sb ) {
        return pop.hasParentheses() ? sb.append( LPAR ) : sb;
    }
    private static StringBuilder rp( PolynomialOperation pop, StringBuilder sb ) {
        return pop.hasParentheses() ? sb.append( RPAR ) : sb;
    }

    private static StringBuilder removeOuterParentheses( StringBuilder sb ) {
        if( sb.length() >= LPAR.length() && sb.substring( 0, LPAR.length() ).equals( LPAR ) )
            sb.delete( 0, LPAR.length() );
        if( sb.length() >= RPAR.length() && sb.substring( sb.length() - RPAR.length(), sb.length() ).equals( RPAR ) )
            sb.delete( sb.length() - RPAR.length(), sb.length() );
        return sb;
    }
}
