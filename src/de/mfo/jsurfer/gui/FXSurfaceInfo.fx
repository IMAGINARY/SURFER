/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author Panda
 */
import javafx.scene.CustomNode;

public class FXSurfaceInfo extends CustomNode
{
    public var x: Number;
    public var y: Number;
    public var width: Number;
    public var height: Number;
    public var gallerys:de.mfo.jsurfer.gui.Gallery[];
    public var gallery:Integer;
    public var surface:Integer;

    public override function create(): javafx.scene.Node
    {
        return javafx.scene.Group
        {
            translateX: bind x translateY: bind y;
            content:
            [
                javafx.scene.layout.Stack
                {
                    content:
                    [
                        javafx.scene.shape.Rectangle
                        {
                            x: 0  y: 0
                            width: bind width  height: bind height
                            fill: javafx.scene.paint.Color.rgb(255, 0, 0)
                        }
                        javafx.scene.image.ImageView
                        {
                            image: bind javafx.ext.swing.SwingUtils.toFXImage( gallerys[gallery].getEntries()[ surface ].getDescription() )
                            fitHeight:bind height
                            fitWidth: bind width
                            preserveRatio: true
                            //layoutInfo:javafx.scene.layout.LayoutInfo{hpos:javafx.geometry.HPos.LEFT}

                        }
                    ]
                }
            ]
        }
    }
}
