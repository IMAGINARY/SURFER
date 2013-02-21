/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.fxgui;

import javax.swing.*;
/**
 * @author Panda
 */

public class FXLabel extends javafx.scene.CustomNode
{   
    def jLable:JLabel=new JLabel();
    public var string:String on replace
    {
        jLable.setText(string);
    };
    public-init var Bound:javafx.scene.Node;
    public-init var getScale:function (n:Number, w:Number):Number;
    public var sceneWidth:Number;
    public var sceneHeight:Number;
    public-init var faktor:Number;
    public override function create(): javafx.scene.Group
    {
        var T2:javafx.geometry.Bounds=Bound.boundsInParent;
        def textField0:JLabel=new JLabel("=0");

        def sw2=JavaFXExtWrapper.wrap(textField0);
        sw2.layoutInfo=javafx.scene.layout.LayoutInfo
        {
            minWidth: T2.width*getScale(sceneHeight,sceneWidth)
            minHeight: T2.height*getScale(sceneHeight,sceneWidth)
            height: T2.height*getScale(sceneHeight,sceneWidth)
            maxHeight: T2.height*getScale(sceneHeight,sceneWidth)
         };

        def hBox:javafx.scene.layout.HBox= new javafx.scene.layout.HBox();
        hBox.content=[sw2];
        var f=Globals.getJavaFont(T2.minY*getScale(sceneHeight,sceneWidth)*0.08);
        textField0.setBorder(BorderFactory.createEmptyBorder());
        hBox.layoutX=T2.minX*getScale(sceneHeight,sceneWidth);
        hBox.layoutY=T2.minY*getScale(sceneHeight,sceneWidth);

        return javafx.scene.Group { content: [ hBox ] };
    }
}
