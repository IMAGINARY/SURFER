package de.mfo.surfer.control;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.print.PrinterJob;
import java.util.function.Consumer;

public class PrintDialog extends Dialog< Consumer< PrinterJob > >
{
    public PrintDialog( String formula, String image )
    {
        super();
        setHeaderText( null );

        getDialogPane().getButtonTypes().addAll( ButtonType.APPLY, ButtonType.CANCEL );

        String url = PrintDialog.class.getResource("/de/mfo/surfer/printing/index.html").toExternalForm();
        url = url + "?formula=" + encodeURIComponent( toLaTeX( formula ) ) + "&image=" + encodeURIComponent( image );
        WebView webView = new WebView();

        WebEngine webEngine = webView.getEngine();

        webEngine.load( url );

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
