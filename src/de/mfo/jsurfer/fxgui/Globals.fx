/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.fxgui;

import de.mfo.jsurfer.gui.FontProvider;
/**
 * @author stussak
 */

public function getJavaFont( s:Float ):java.awt.Font
{
    return FontProvider.getFont().deriveFont( s );
}
public function getJavaFXFont( s:Float ):javafx.scene.text.Font
{
    return javafx.scene.text.Font { name:FontProvider.getName() size:s };
}

public class Globals {}