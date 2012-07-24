/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author stussak
 */
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.effect.*;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;

public class MessageBox extends javafx.scene.CustomNode
{
    def f40=Globals.getJavaFXFont( 40 );
    public-init var message : String;

    public override function create(): javafx.scene.Node
    {
        visible=false;
        return stack;
    }

    public function show( s : Scene, enableOk : Boolean ) : Void {
        button.disable = not enableOk;
        s.lookup( "surferGUIGroup" ).effect = GaussianBlur{};
        var sb = javafx.geometry.BoundingBox { minX: s.x, minY: s.y width: s.width height: s.height };
        var b : javafx.geometry.Bounds = s.lookup( "surferSceneGroup" ).sceneToLocal( sb );

        dummyRectangle.x = b.minX;
        dummyRectangle.y = b.minY;
        dummyRectangle.width = b.width;
        dummyRectangle.height = b.height;
        insert this into (s.lookup( "surferSceneGroup" ) as Group).content;
        visible = true;
    }

    public function hide() : Void {
        scene.lookup( "surferGUIGroup" ).effect = null;
        delete this from scene.content;
        visible = false;
    }

    public function enableOk() : Void {
        button.disable = false;
    }


    public-read def text: javafx.scene.text.Text = javafx.scene.text.Text {
        textAlignment: javafx.scene.text.TextAlignment.LEFT
        content: message
        wrappingWidth: 600
        font: f40
    }

    public-read def button: javafx.scene.control.Button = javafx.scene.control.Button {
        width: 100
        height: 60
        text: "Ok"
        //font: f8
        action: function() { hide(); }
        scaleX: 4.0
        scaleY: 4.0
    }

    public-read def verticalBox: javafx.scene.layout.VBox = javafx.scene.layout.VBox {
        content: [ text, button, ]
        spacing: 100.0
        hpos: javafx.geometry.HPos.CENTER
        vpos: javafx.geometry.VPos.CENTER
        nodeHPos: javafx.geometry.HPos.CENTER
        nodeVPos: javafx.geometry.VPos.CENTER
    }

    public-read def dropShadow: javafx.scene.effect.DropShadow = javafx.scene.effect.DropShadow {
        offsetX: 3
        offsetY: 3
        width: 40
    }

    def __layoutInfo_rectangle: javafx.scene.layout.LayoutInfo = javafx.scene.layout.LayoutInfo {
        hpos: javafx.geometry.HPos.CENTER
        vpos: javafx.geometry.VPos.CENTER
    }

    public-read def rectangle: javafx.scene.shape.Rectangle = javafx.scene.shape.Rectangle {
        layoutInfo: __layoutInfo_rectangle
        effect: dropShadow
        fill: Color.LIGHTGRAY
        stroke: Color.GRAY
        strokeWidth: 2.0
        width: bind stackMBox.width
        height: bind stackMBox.height
        arcWidth: 20.0
        arcHeight: 20.0
    }

    def __layoutInfo_stack: javafx.scene.layout.LayoutInfo = javafx.scene.layout.LayoutInfo {
        minWidth: 200.0
        minHeight: 200.0
        maxWidth: 700.0
        maxHeight: 400.0
        hpos: javafx.geometry.HPos.CENTER
        vpos: javafx.geometry.VPos.CENTER
    }

    def stackMBox: javafx.scene.layout.Stack = javafx.scene.layout.Stack {
        padding: javafx.geometry.Insets { left: 20 right: 20 bottom: 20 top: 20 }
        nodeHPos: javafx.geometry.HPos.CENTER
        nodeVPos: javafx.geometry.VPos.CENTER
        layoutInfo: __layoutInfo_stack
        content: [ rectangle, verticalBox ]
    }

    def dummyRectangle : Rectangle = Rectangle { blocksMouse: true opacity: 0.25 };

    def stack = Stack {
        content: [ dummyRectangle, stackMBox ]
        nodeHPos: javafx.geometry.HPos.CENTER
        nodeVPos: javafx.geometry.VPos.CENTER        
    }
}
