/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author stussak
 */

public class AlwaysFocusedTextBox extends javafx.scene.control.TextBox
{
    var my_focused:Boolean = bind focused on replace { if( focused == false ) focused = true else java.lang.System.out.println("aaa" ); }
    var my_pressed:Boolean = bind pressed on replace { requestFocus(); }
}
