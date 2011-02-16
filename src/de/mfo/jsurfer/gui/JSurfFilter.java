/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 *
 * @author Panda
 */

public class JSurfFilter extends javax.swing.filechooser.FileFilter
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
        if( !getExtension( f ).equals( "jsurf" ) )
            f = new java.io.File( f.getAbsolutePath() + ".jsurf" );
        return f;
    }

    //Accept all png files.
    public boolean accept( java.io.File f )
    {
        return f.isDirectory() || getExtension( f ).equals( "jsurf" );
    }

    //The description of this filter
    public String getDescription() {
        return "*.jsurf (jsurf SurferFile)";
    }
}




