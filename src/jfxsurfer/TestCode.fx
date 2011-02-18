/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfxsurfer;

import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.ext.swing.SwingUtils;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollView;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.lang.System;
import java.util.Locale;
import com.sun.pdfview.*;
import de.mfo.jsurfer.gui.*;


/**
 * @author stussak
 */

// place your code here

public function loadBufferedImage() : BufferedImage
{
    var bImg:BufferedImage = null;

try {
    var filename:String = "/home/stussak/Desktop/gallery_1.4.pdf";

    var raf:RandomAccessFile = new RandomAccessFile(new File (filename), "r");
    var fc:FileChannel = raf.getChannel ();
    var buf:ByteBuffer = fc.map (FileChannel.MapMode.READ_ONLY, 0, fc.size ());
    var pdfFile:PDFFile = new PDFFile (buf);
    var pdfPage:PDFPage = pdfFile.getPage(1);
    var r2d:Rectangle2D = pdfPage.getBBox();
    System.out.println( r2d );
    var img:Image = pdfPage.getImage( 3*r2d.getWidth(), 3*r2d.getHeight(), r2d, null, true, true);
    bImg = new BufferedImage(3*r2d.getWidth(), 3*r2d.getHeight(), BufferedImage.TYPE_INT_RGB);
    bImg.getGraphics().drawImage( img, 0, 0, null);


    //System.out.println(filename);
    //img = ImageIO.read(new File(filename));
} catch (e:IOException) {
    System.out.println( e );
}
    return bImg;

}

public function testLocales() : String
{
    Locale.setDefault(new Locale("en", "US"));
    return ##"many_singularites";
}

public function testGallery() : Group
{
    var g:de.mfo.jsurfer.gui.Gallery = new de.mfo.jsurfer.gui.Gallery( 0 );

    return
    Group {
        content: [
            ScrollView {
                layoutInfo: LayoutInfo {
                    width: 500
                    height: 500
                }
                node: VBox {
                    content: [
                        Text { content: "gallery_num={g.getNumber()}" },
                        Text { content: "gallery_key={g.getKey()}" },
                        Text { content: "gallery_name={g.getName()}" },
                        ImageView { image: SwingUtils.toFXImage( g.getIcon() ) },
                        ImageView { image: SwingUtils.toFXImage( g.getDescription() ) },
                        Text { content: "{g.getEntries()[ 0 ].getName()}" }
                        ImageView { image: SwingUtils.toFXImage( g.getEntries()[ 0 ].getIcon() ) }
                        ImageView { image: SwingUtils.toFXImage( g.getEntries()[ 0 ].getDescription() ) }
                    ]
                }
            }
        ]
    };
}