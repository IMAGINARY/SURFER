/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

import javafx.ext.swing.*;

/**
 * wrap all needed functions from the javafx.ext packaged since it is
 * deprecated and produces lots of warnings.
*/
public function toFXImage( image: java.awt.image.BufferedImage ): javafx.scene.image.Image
{
    return JavaFXExtWrapper.toFXImage( image );
}

public function wrap( jComponent: javax.swing.JComponent ) : SwingComponent
{
    return JavaFXExtWrapper.wrap( jComponent );
}
