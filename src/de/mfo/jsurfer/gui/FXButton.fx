/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

import javafx.scene.CustomNode;
import javafx.scene.Node;
/**
 * @author Panda
 */

public class FXButton extends CustomNode{
    public var normal:Node;
    public var over:Node;
    public var press:Node;
    public var f:function():Void;
    function setNormal()
    {
        normal.visible=true;
        over.visible=false;
        press.visible=false;
    }
    function setOver()
    {
        over.visible=true;
        normal.visible=false;
        press.visible=false;
    }
    function setPress()
    {
        press.visible=true;
        normal.visible=false;
        over.visible=false;
        
    }
    
}
