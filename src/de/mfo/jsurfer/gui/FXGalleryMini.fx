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
    public-init var language: java.util.Locale;
    public var x: Number;
    public var y: Number;
    public var width: Number;
    public var height: Number;
    public var gallerys:de.mfo.jsurfer.gui.Gallery[];
    public var gallery:Integer;
    public var surface:Integer;
    public var press:function(s:Integer):Void;


    var galleryNodes:javafx.scene.layout.Tile[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(language)-1])javafx.scene.layout.Tile{};;
    public override function create(): javafx.scene.Node
    {
        //galleryNodes=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(language)-1])javafx.scene.layout.Tile{};
        
        for (g in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(language)-1])
        {
            galleryNodes[g]=javafx.scene.layout.Tile
            {
                columns: 3
                hgap: 20
                vgap: 30
                padding: bind javafx.geometry.Insets{top: 5 left: (width-((width-3*5-2*20)/3*0.9*3+15))/2}
                nodeVPos: javafx.geometry.VPos.BOTTOM
                nodeHPos: javafx.geometry.HPos.LEFT
                layoutInfo:javafx.scene.layout.LayoutInfo
                {
                    width: bind width  
                    height: bind height
                }

                content: bind
                [
                    for (i in [0..(gallerys[g].getEntries().length-1)])
                    javafx.scene.Group
                    {
                        content:
                        [
                            javafx.scene.layout.VBox
                            {
                                onMousePressed :bind function(e: javafx.scene.input.MouseEvent): Void {press(i);}
                                content:
                                [
                                    javafx.scene.image.ImageView
                                    {
                                        image: javafx.ext.swing.SwingUtils.toFXImage( gallerys[g].getEntries()[ i ].getIcon() )
                                        fitWidth:bind (width-3*5-2*20)/3*0.7
                                        preserveRatio: true
                                    }
                                    javafx.scene.text.Text
                                    {
                                        id: "FXGalleryChooser{language }{gallery} {i}"
                                        font: bind javafx.scene.text.Font.font
                                        (
                                            "Arial",
                                             javafx.scene.text.FontWeight.REGULAR,
                                             ((width-3*5-2*20)/3*0.9)*0.4*0.25
                                        )
                                        content: "{gallerys[g].getEntries()[ i ].getName()}"
                                        //content: "FXGalleryChooser|{language.getDisplayCountry() }|{gallery} {i}({java.util.Locale.GERMAN.getDisplayCountry()})({java.util.Locale.GERMAN})"
                                        //textAlignment:javafx.scene.text.TextAlignment.CENTER
                                        translateX: bind ((width-3*5-2*20)/3*0.7-scene.lookup("FXGalleryChooser{language}{gallery} {i}").boundsInLocal.width)/2
                                        //translateY: bind (height-4*5)/5*0.2
                                        //textOrigin: javafx.scene.text.TextOrigin.BASELINE

                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        }
        
        return javafx.scene.Group
        {
            translateX: bind x translateY: bind y;
            content:
            [
                javafx.scene.layout.Stack
                {
                    content:bind
                    [
                        /*javafx.scene.shape.Rectangle
                        {
                            x: 0  y: 0
                            width: bind width  height: bind height
                            fill: javafx.scene.paint.Color.rgb(0, 100, 200)
                        }*/
                        galleryNodes [gallery]
                    ]
                }
            ]
        }
    }

}
