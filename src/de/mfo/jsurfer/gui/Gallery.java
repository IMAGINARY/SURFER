/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.*;
import java.io.*;
import java.nio.*;
import com.sun.pdfview.*;

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
    URL descriptionURL;
    BufferedImage description;
    GalleryItem[] gallery_items;
    
    public Gallery( int number )
            throws IOException
    {
        locale = Locale.getDefault();
        rb = ResourceBundle.getBundle( "de/mfo/jsurfer/gui/gallery/Gallery", locale );
        
        Enumeration< String > keys = rb.getKeys();
        for(; keys.hasMoreElements(); ) 
            System.out.println( keys.nextElement() );
        
        this.number = number;
        this.key = rb.getString( "gallery_" + number + "_key" );
        this.name = rb.getString( key );
        this.iconURL = getResource( "gallery/" + rb.getString( "gallery_" + number + "_icon" ) + "_icon.png" );
        this.descriptionURL = getResourceFromLocalizedName( "gallery/" + key + "_description", ".pdf" );
        
        String[] content_keys = getContentKeys();
        LinkedList< GalleryItem > l = new LinkedList< GalleryItem >();
        for( int i = 0; i < content_keys.length; i++ )
            l.add( new GalleryItem( content_keys[ i ] ) );
        this.gallery_items = l.toArray( new GalleryItem[ 0 ] );
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
    public BufferedImage getDescription( int width, int height )
    {
        if( description == null || description.getWidth() != width || description.getHeight() == height )
            description = renderPDF( descriptionURL, width, height );
        return description;
    }
    
    public GalleryItem[] getEntries()
    {
        return this.gallery_items;
    }

    String[] getContentKeys()
    {
        String content_string = rb.getString( "gallery_" + number + "_content" );
        String[] content_keys = content_string.split( "," );
        for( int i = 0; i < content_keys.length; i++ )
            content_keys[ i ] = content_keys[ i ].trim();
        return content_keys;
    }

    URL getResource( String res )
    {
        URL url = getClass().getResource( res );
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
            System.err.println( "resource \"" + url_language_country + "\" and fallback \"" + url_language + "\" not found" );
        return url;
    }

    BufferedImage renderPDF( URL url, int width, int height )
    {
        BufferedImage bImg = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        try
        {
            URLConnection connection = url.openConnection();
            // Since you get a URLConnection, use it to get the InputStream
            InputStream is = connection.getInputStream();
            // Now that the InputStream is open, get the content length
            int contentLength = connection.getContentLength();

            // create temporary buffer
            ByteArrayOutputStream tmpOut = new ByteArrayOutputStream( contentLength == -1 ? 16384 : contentLength );

            // fill temporary buffer
            byte[] buf = new byte[512];
            int len;
            while( ( len = is.read(buf) ) != -1 )
                tmpOut.write(buf, 0, len);
        
            // close buffers
            is.close();
            tmpOut.close();

            // create ByteBuffer, which we need for the PDFRenderer
            ByteBuffer byte_buf = ByteBuffer.wrap( tmpOut.toByteArray() );

            // render PDF into image
            PDFFile pdfFile = new PDFFile( byte_buf );
            System.err.println( pdfFile.getVersionString() );
            PDFPage pdfPage = pdfFile.getPage( 0 );
            java.awt.geom.Rectangle2D r2d = pdfPage.getBBox();
            Image img = pdfPage.getImage( width, height, r2d, null, true, true );
            bImg.getGraphics().drawImage( img, 0, 0, null );
        }
        catch( Exception e )
        {
            System.err.println( e );
        }
        return bImg;
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
        private URL descriptionURL;
        private BufferedImage description;
        private URL jsurf_file_url;

        GalleryItem( String key )
                throws IOException
        {
            this.key = key;
            this.name = rb.getString( key );
            this.iconURL = getResource( "gallery/" + key + "_icon.png" );
            this.descriptionURL = getResourceFromLocalizedName( "gallery/" + key + "_description", ".pdf" );
            this.jsurf_file_url = getResource( "gallery/" + key + ".jsurf" );
        }

        public String getKey() { return key; }
        public String getName() { return name; }
        public BufferedImage getIcon()
        {
            if( icon == null )
                icon = loadImage( iconURL );
            return icon;
        }
        public BufferedImage getDescription( int width, int height )
        {
            if( description == null || description.getWidth() != width || description.getHeight() == height )
                description = renderPDF( descriptionURL, width, height );
            return description;
        }
        public URL getJSurfURL() { return jsurf_file_url; }
    }
}
