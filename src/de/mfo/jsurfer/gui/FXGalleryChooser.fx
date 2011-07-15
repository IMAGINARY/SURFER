/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author Panda
 */

import javafx.scene.CustomNode;

public class FXGalleryChooser extends CustomNode
{
    public-init var language: java.util.Locale;
    //public var x: Number;
    //public var y: Number;
    public var width: Number;
    public var height: Number;
    public var gallerys:de.mfo.jsurfer.gui.Gallery[];
    public var gallery:Integer=0;
    public var setGallery:function(g:Integer):Void;
    def n:Number=de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(language);
    public override function create(): javafx.scene.Node
    {
        return javafx.scene.Group
        {
            /*translateX: bind x*/ translateY: bind height/20;
            content:
            [
                javafx.scene.layout.Stack
                {
                    content:
                    [
                        /*javafx.scene.shape.Rectangle
                        {
                            x: 0  y: 0
                            width: bind width  height: bind height
                            fill: javafx.scene.paint.Color.rgb(200, 0, 0)
                        }*/
                        javafx.scene.layout.VBox
                        {
                            padding: javafx.geometry.Insets { top: 4 right: 4 bottom: 4 left: 4}
                            spacing: bind height/10
                            nodeHPos: javafx.geometry.HPos.CENTER
                            content: for (i in [0..n-1])createButton(i)

                        }
                    ]
                }
            ]
        }
    }
   /* private function getFontWeight(g:Integer):javafx.scene.text.FontWeight
    {
        if (g==gallery)return javafx.scene.text.FontWeight.REGULAR;

    }*/

    function createButton(g:Integer):javafx.scene.Group
    {
        return javafx.scene.Group
        {
            //translateX: bind x+g*(height/5) translateY: bind y*(height/5);
            onMousePressed : function(e: javafx.scene.input.MouseEvent): Void {setGallery(g)}
            content:
            [
                javafx.scene.layout.Stack
                {
                    content:
                    [
                        javafx.scene.shape.Rectangle
                        {
                            x: 0  y: 0
                            width: bind width-10  height: bind (height-4*5)/5
                            //visible:false
                            opacity:0.0
                            //fill: javafx.scene.paint.Color.rgb(0, 200, 0)
                        }/**/

                        javafx.scene.layout.HBox
                        {
                            content:
                            [
                                javafx.scene.image.ImageView
                                {
                                    image: javafx.ext.swing.SwingUtils.toFXImage( gallerys[g].getIcon() )
                                    fitHeight:bind (height-4*5)/5
                                    preserveRatio: true
                                    layoutInfo:javafx.scene.layout.LayoutInfo{hpos:javafx.geometry.HPos.LEFT}

                                }
                                javafx.scene.text.Text
                                {
                                    id: "FXGalleryChooser{g}"
                                    font: bind javafx.scene.text.Font.font
                                    (  
                                        "Arial",
                                         if (g==gallery){javafx.scene.text.FontWeight.BOLD}else{ javafx.scene.text.FontWeight.REGULAR},
                                         ((height-4*5)/5)*0.4*0.4
                                    )
                                    content: "{gallerys[g].getName()}"
                                    //textAlignment:javafx.scene.text.TextAlignment.CENTER
                                    translateX: bind width*0.05
                                    translateY: bind (height-4*5)/5*0.2
                                    /*layoutInfo:javafx.scene.layout.LayoutInfo
                                    {
                                        //hpos:javafx.geometry.HPos.RIGHT
                                        //vpos:javafx.geometry.VPos.TOP
                                    }*/
                                   
                                }
                            ]
                        }

                    ]
                }
            ]
        };
    }



}
