/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.fxgui;

public class FXImpressum  extends javafx.scene.CustomNode
{
    public var height:Number;
    public var width:Number;
    public var language: java.util.Locale;
    var messages:java.util.ResourceBundle=bind java.util.ResourceBundle.getBundle( "de.mfo.jsurfer.fxgui.MessagesBundle", language );

    public override function create(): javafx.scene.Node
    {
        var path : String = bind "/de/mfo/jsurfer/fxgui/{messages.getString( "imprintFile" )}";
        return javafx.scene.image.ImageView
        {
            image : bind javafx.scene.image.Image {
                url: de.mfo.jsurfer.gui.Gallery.class.getResource( path ).toString();
            }
            fitWidth:bind width
            fitHeight:bind height
            preserveRatio: true
        };
    }
}
