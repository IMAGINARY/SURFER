/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author Panda
 */


import javafx.scene.CustomNode;

public class FXGalleryMini extends CustomNode
{
    public var x: Number;
    public var y: Number;
    public var width: Number;
    public var height: Number;

    public override function create(): javafx.scene.Node
    {
        /*def sw:SwingComponent=SwingComponent.wrap(getRenderer());
        sw.layoutInfo=LayoutInfo{
            minWidth: bind height,
            width: bind width
            maxWidth: bind width
            minHeight: bind height
            height: bind height
            maxHeight: bind height
         };*/

        return javafx.scene.Group {
                    translateX: bind x translateY: bind y;
                    content:
                    [
                        javafx.scene.shape.Rectangle
                        {
                            x: 0  y: 0
                            width: bind width  height: bind height
                            fill: javafx.scene.paint.Color.rgb(100, 200, 0)
                        }
                        //SwingComponent.wrap(getRenderer())
                    ]
                }
    }

}
