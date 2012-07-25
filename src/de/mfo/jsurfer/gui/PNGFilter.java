/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 *
 * @author Panda
 */

public class PNGFilter extends javax.swing.filechooser.FileFilter
{
    public static String getExtension( java.io.File f )
    {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1)
            ext = s.substring(i+1).toLowerCase();
        return ext;
    }

    public static java.io.File ensureExtension( java.io.File f )
    {
        if( !getExtension( f ).equals( "png" ) )
            f = new java.io.File( f.getAbsolutePath() + ".png" );
        return f;
    }

    //Accept all png files.
    public boolean accept( java.io.File f )
    {
        return f.isDirectory() || getExtension( f ).equals( "png" );
    }

    //The description of this filter
    public String getDescription() {
        return "*.png (Portable Network Graphics)";
    }
}




