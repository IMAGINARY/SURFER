/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 *
 * @author Panda
 */

import com.bric.swing.ColorPicker;
import java.awt.*;
import javax.swing.*;

public class ColorChooserBasic extends ColorPicker {
  ColorChooserBasic(int a, int b)//:ColorPicker(false)
    {
      super(false);
      setPreferredSize( new Dimension( a, b ) );
        for( Component c : getComponents() )
            if( c instanceof JComponent )
                ( ( JComponent ) c ).setOpaque( false );
    }
}
