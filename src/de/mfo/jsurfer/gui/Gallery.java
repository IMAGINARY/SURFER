/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

import java.util.*;
//import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.*;
import java.io.*;
import java.nio.*;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.w3c.dom.*;


/**
 *
 * @author stussak
 */
public class Gallery {

    ResourceBundle rb;
    Locale locale;

    int number;
    String key;
    String name;
    URL iconURL;
    BufferedImage icon;
    PdfDecoder pdfDecoder;
    BufferedImage description;
    GalleryItem[] gallery_items;

    public static int getNumberOfGalleries() throws IOException
    {
        return getNumberOfGalleries(Locale.getDefault());
    }
    public static int getNumberOfGalleries(Locale locale)
            throws IOException
    {
        ResourceBundle rb = ResourceBundle.getBundle( "de/mfo/jsurfer/gallery/Gallery", locale );
        return Integer.parseInt( rb.getString( "number_of_galleries" ) );
    }
    public Gallery(int number) throws IOException
    {
        this( number, Locale.getDefault() );
    }
    public Gallery( int number, Locale locale  )
            throws IOException
    {
        this.locale = locale;
        rb = ResourceBundle.getBundle( "de/mfo/jsurfer/gallery/Gallery", locale );

        this.number = number;
        this.key = rb.getString( "gallery_" + number + "_key" ).trim();

        pdfDecoder = new PdfDecoder();
        String pdfURL = getResourceFromLocalizedName( "/de/mfo/jsurfer/gallery/" + key, ".pdf" ).toString();
        try
        {
            System.err.print( "loading " + pdfURL);
            pdfDecoder.openPdfFileFromURL( pdfURL, true );            
        }
        catch( PdfException pdfe )
        {
            System.err.print( " ... failed!" );
            pdfe.printStackTrace();
        }
        finally { System.err.println(); }

        List< OutlineEntry > outlineEntries = readGalleryOutline();

        OutlineEntry introEntry = outlineEntries.remove(0);
        this.name = introEntry.name.trim();
        this.iconURL = getResource( "/de/mfo/jsurfer/gallery/" + introEntry.filename_prefix + "_icon.png" );

        LinkedList< GalleryItem > l = new LinkedList< GalleryItem >();
        for( OutlineEntry entry : outlineEntries )
            l.add( new GalleryItem( entry.filename_prefix, entry.name, entry.pageNum ) );
        this.gallery_items = l.toArray( new GalleryItem[ 0 ] );
    }

    private class OutlineEntry
    {
        public String name;
        public String filename_prefix;
        public int pageNum;

        public OutlineEntry( String name, String filename_prefix, int pageNum )
        {
            this.name = name;
            this.filename_prefix = filename_prefix;
            this.pageNum = pageNum;
        }
    }

    private List< OutlineEntry > readGalleryOutline()
    {
        LinkedList< OutlineEntry > entries = new LinkedList< OutlineEntry >();  
        Node rootNode = pdfDecoder.getOutlineAsXML().getFirstChild();

        NodeList children = rootNode.getChildNodes();
        for( int i = 0; i < children.getLength(); i++ )
        {
            Element currentElement = (Element) children.item(i);

            entries.add( new OutlineEntry(
                currentElement.getAttribute("title"),
                ( (Element) currentElement.getFirstChild() ).getAttribute( "title" ),
                Integer.parseInt( currentElement.getAttribute("page") )
                ) );
        }
        return entries;
    }

    public int getNumber() { return number; }
    public String getKey() { return key; }
    public String getName() { return name; }
    public BufferedImage getIcon()
    {
        if( icon == null )
            icon = loadImage( iconURL );
        return icon;
    }
    public URL getIconURL() { return iconURL; }
    public BufferedImage getDescription( int width, int height )
    {
        if( width == 0 )
            width = 1;
        if( height == 0 )
            height = 1;
        if( description == null || description.getWidth() != width || description.getHeight() != height )
            description = renderPDFPage( 1, width, height );
        return description;
    }

    public GalleryItem[] getEntries()
    {
        return this.gallery_items;
    }

    URL getResource( String res )
    {
        URL url = this.getClass().getResource( res );
        if( url == null )
            System.err.println( "resource \"" + res + "\" not found" );
        return url;
    }

    URL getResourceFromLocalizedName( String name, String extension )
    {
        // prepare localized urls
        String url_language_country = name + "_" + locale.getLanguage() + "_" + locale.getCountry() + extension;
        String url_language = name + "_" + locale.getLanguage() + extension;

        // try to retrieve fully localized url
        URL url = getClass().getResource( url_language_country );
        if( url == null )
            // fallback to language-only localization
            url = getClass().getResource( url_language );

        if( url == null )
            if( !locale.getCountry().equals( "" ) )
                System.err.println( "resource \"" + url_language_country + "\" and fallback \"" + url_language + "\" not found" );
            else
                System.err.println( "resource \"" + url_language + "\" not found" );
        return url;
    }

    BufferedImage renderPDFPage( int pageNum, int width, int height )
    {
        try
        {
            // ToDo: scale appropriately 
            return pdfDecoder.getPageAsImage( pageNum );
        }
        catch( Exception e )
        {
            System.err.println( e );
        }
        return new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
    }

    BufferedImage loadImage( URL img_url )
    {
        BufferedImage img = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
        try
        {
            img = ImageIO.read( img_url );
        }
        catch( Exception e )
        {
            System.err.println( "could not load " + img_url );
        }
        return img;
    }

    public class GalleryItem
    {
        private String key;
        private String name;
        private URL iconURL;
        private BufferedImage icon;
        private int descriptionPageNumber;
        private BufferedImage description;
        private URL jsurf_file_url;

        GalleryItem( String key, String label, int pageNum )
                throws IOException
        {
            this.key = key;
            this.name = label.trim();
            this.iconURL = getResource( "/de/mfo/jsurfer/gallery/" + key + "_icon.png" );
            this.descriptionPageNumber = pageNum;
            this.jsurf_file_url = getResource( "/de/mfo/jsurfer/gallery/" + key + ".jsurf" );
        }

        public String getKey() { return key; }
        public String getName() { return name; }
        public BufferedImage getIcon()
        {
            if( icon == null )
                icon = loadImage( iconURL );
            return icon;
        }
        public URL getIconURL() { return iconURL; }

        public BufferedImage getDescription( int width, int height )
        {
            if( width == 0 )
                width = 1;
            if( height == 0 )
                height = 1;
            if( description == null || description.getWidth() != width || description.getHeight() != height )
                    description = Gallery.this.renderPDFPage( descriptionPageNumber, width, height );
                return description;
        }
        public URL getJSurfURL() { return jsurf_file_url; }
    }
}
