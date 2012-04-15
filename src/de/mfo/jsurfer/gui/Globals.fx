/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author stussak
 */

def javaFont:java.awt.Font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT , new java.lang.Object().getClass().getResourceAsStream("/de/mfo/jsurfer/gui/Nimbus Sans L Regular Surfer.ttf") );

public function getJavaFont( s:Float ):java.awt.Font
{
    return javaFont.deriveFont( s );
}
public function getJavaFXFont( s:Float ):javafx.scene.text.Font
{
    return javafx.scene.text.Font { name:"Nimbus_Sans_L_Regular_Surfer" size:s };
}

public class Globals {}
