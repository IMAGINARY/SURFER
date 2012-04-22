/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 *
 * @author stussak
 */
public class FontProvider {

    static Font font;
    static String name;

    static
    {
        try
        {
            InputStream is = FontProvider.class.getResourceAsStream( "/de/mfo/jsurfer/gui/Nimbus Sans L Regular Surfer.ttf");
            font = Font.createFont( Font.TRUETYPE_FONT, is );
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // Java5 is bundled with JavaFX 1.3, but is does not include the registerFont method.
            // Since we require Java6 during runtime, we can use reflection to solve the probem. This ugly hack is not is not typesafe.
            Method registerFont = ge.getClass().getMethod( "registerFont" , Font.class );
            registerFont.invoke( ge, font );
            name = font.getName();
            // now the font is available as a System font under that name
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    public static java.awt.Font getFont()
    {
        return font;
    }
    public static String getName()
    {
        return name;
    }
}
