/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.fxgui;

/**
 * @author stussak
 */

public class AlwaysFocusedTextBox extends javafx.scene.control.TextBox
{
    init { FX.deferAction( function() { requestFocus(); } ); }
    var my_focused:Boolean = bind focused on replace { requestFocus(); }
    var my_pressed:Boolean = bind pressed on replace { requestFocus(); }
}
