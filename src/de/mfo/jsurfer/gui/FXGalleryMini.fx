/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author Panda
 */


import javafx.scene.CustomNode;
import javafx.scene.control.Label;

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


    var galleryNodes:javafx.scene.layout.Tile[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(language)-1])javafx.scene.layout.Tile{};
    public override function create(): javafx.scene.Node
    {
        //galleryNodes=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(language)-1])javafx.scene.layout.Tile{};
        
        for (g in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(language)-1])
        {
            galleryNodes[g]=javafx.scene.layout.Tile
            {
                columns: 3
                hgap: 20
                vgap: 20
                autoSizeTiles: false
                tileWidth: bind (( width - 2*20-10 - 1 )/3.0)
                padding: javafx.geometry.Insets{ top: 20 left: 10 }
                nodeVPos: javafx.geometry.VPos.BOTTOM
                nodeHPos: javafx.geometry.HPos.CENTER
                layoutInfo:javafx.scene.layout.LayoutInfo
                {
                    width: bind width  
                    height: bind height
                }

                content:
                [
                    for (i in [0..(gallerys[g].getEntries().length-1)])
                    {
                        Label {
                            layoutInfo: javafx.scene.layout.LayoutInfo
                            {
                                width: bind (( width - 2*20-10 - 1 )/3.0)
                            }
                            style: "-fx-background-color:red;"
                            id: "FXGalleryChooser{language}{gallery} {i}"
                            onMousePressed : function(e: javafx.scene.input.MouseEvent): Void {press(i);}
                            text: "{gallerys[g].getEntries()[ i ].getName()}"
                            font: javafx.scene.text.Font.font( "Arial", javafx.scene.text.FontWeight.REGULAR, 19 )
                            hpos: javafx.geometry.HPos.CENTER
                            vpos: javafx.geometry.VPos.TOP
                            textOverrun: javafx.scene.control.OverrunStyle.CLIP
                            textWrap: false
                            textAlignment: javafx.scene.text.TextAlignment.CENTER
                            graphic: javafx.scene.image.ImageView
                            {
                                image: javafx.ext.swing.SwingUtils.toFXImage( gallerys[g].getEntries()[ i ].getIcon() )
                                fitWidth: 150
                                preserveRatio: true
                            }
                            graphicHPos: javafx.geometry.HPos.CENTER
                            graphicVPos: javafx.geometry.VPos.TOP
                        }
                    }
                ]
            }
        }
        
        return javafx.scene.Group
        {
            translateX: bind x translateY: bind y;
            content: bind [ galleryNodes[ gallery ] ]
        }
    }
}
