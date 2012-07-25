/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
/**
 * @author stussak
 */

public class FXButton extends CustomNode{
    public var normal:Node on replace oldValue { setState() };
    public var hovered:Node on replace oldValue { setState() };
    public var armed:Node on replace oldValue { setState() };
    public var action:function():Void;
    var s : Stack;

    public override function create(): javafx.scene.Node
    {
        s = Stack {
            content: bind [ normal, hovered, armed ]
            onMouseClicked : bind function( me : MouseEvent ) { action() }
            onMouseEntered : function( me : MouseEvent ) { setState() }
            onMouseExited : function( me : MouseEvent ) { setState() }
            onMousePressed : function( me : MouseEvent ) { setState() }
            onMouseReleased : function( me : MouseEvent ) { setState() }
        };
        setState();
        return s;
    }

    function setState()
    {
        if( s.pressed )
        {
            setArmed()
        }
        else
        {
            if( s.hover )
                setHovered()
            else
                setNormal();
        }
    }


    function setNormal()
    {
        normal.visible=true;
        hovered.visible=false;
        armed.visible=false;
    }
    function setHovered()
    {
        hovered.visible=true;
        normal.visible=false;
        armed.visible=false;
    }
    function setArmed()
    {
        armed.visible=true;
        normal.visible=false;
        hovered.visible=false;
    }
    
}
