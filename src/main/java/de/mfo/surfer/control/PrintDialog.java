package de.mfo.surfer.control;

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
        url = url + "?formula=" + urlEncode( toLaTeX( formula ) ) + "&image=" + urlEncode( image );
        WebView webView = new WebView();
        webView.getEngine().load( url );

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

    public static String urlEncode( String s )
    {
        return s;
    }
}
