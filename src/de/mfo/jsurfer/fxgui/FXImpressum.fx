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

    public override function create(): javafx.scene.Node
    {
        return javafx.scene.image.ImageView
        {
           image : bind javafx.scene.image.Image {
            url: "{__DIR__}Impressum_{language.getLanguage()}.png"}
            fitWidth:bind width
            fitHeight:bind height
            preserveRatio: true
        };
    }
}
