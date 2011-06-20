/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

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
        //var T:Bounds=fxdButtons.getNode("Equation").boundsInParent;
        var T2:javafx.geometry.Bounds=Bound.boundsInParent;
        //var test:JTextField=new JTextField("x^2+y^2+z^2+2*x*y*z-1");
        //fxdButtons.getNode("Equation").visible=false;
        //fxdButtons.getNode("Equals_Zero").visible=false;
        //System.out.println("TxtOrG:{T.minX},{T.minY},{T.maxX},{T.maxY},{T.width},{T.height}");
        //System.out.println("TabField:{tabField.backColor.width},{tabField.backColor.height}");
        def textField0:JLabel=new JLabel("=0");
        //textField0.setEditable(false);

        def sw2:javafx.ext.swing.SwingComponent=javafx.ext.swing.SwingComponent.wrap(textField0);
        sw2.layoutInfo=javafx.scene.layout.LayoutInfo
        {
            minWidth: T2.width*getScale(sceneHeight,sceneWidth)
            //width: T2.width*getScale(height,width)
            //maxWidth: T2.width*getScale(height,width)
            minHeight: T2.height*getScale(sceneHeight,sceneWidth)
            height: T2.height*getScale(sceneHeight,sceneWidth)
            maxHeight: T2.height*getScale(sceneHeight,sceneWidth)
         };
        /*def sw:SwingComponent=SwingComponent.wrap(test);
        sw.layoutInfo=LayoutInfo
        {
            minWidth: T.width*getScale(height,width)
            width: T.width*getScale(height,width)
            maxWidth: T.width*getScale(height,width)
            minHeight: T.height*getScale(height,width)
            height: T.height*getScale(height,width)
            maxHeight: T.height*getScale(height,width)
         };*/
        /*SurfaceExpression.content=
        [
            sw

        ];*/
        def hBox:javafx.scene.layout.HBox= new javafx.scene.layout.HBox();
        hBox.content=[sw2];
        //surfaceExpressionField.font=Font{size:T.minY*getScale(height,width)*0.08};
        //var f:Font=test.getFont();
        //Nimbus Sans L Regular Surfer.ttf
        //var f:Font=java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT , new File("{__DIR__}Nimbus Sans L Regular Surfer.ttf"));
        //System.out.println("__DIR__:{__DIR__}");
        var input:java.io.InputStream = getClass().getResourceAsStream("/de/mfo/jsurfer/gui/Nimbus Sans L Regular Surfer.ttf");
        var f:java.awt.Font=java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT , input);
        f=f.deriveFont(T2.minY*getScale(sceneHeight,sceneWidth)*0.08);
        //test.setFont(f);
        //test.setBorder( BorderFactory.createEmptyBorder() );
        //textField0.setFont(f);
        textField0.setBorder(BorderFactory.createEmptyBorder());
        //SurfaceExpression.layoutX=T.minX*getScale(height,width);
        //SurfaceExpression.layoutY=T.minY*getScale(height,width);
        hBox.layoutX=T2.minX*getScale(sceneHeight,sceneWidth);
        hBox.layoutY=T2.minY*getScale(sceneHeight,sceneWidth);
        /*var T:javafx.geometry.Bounds=Bound.boundsInParent;
        Bound.visible=false;def sw2:javafx.ext.swing.SwingComponent=javafx.ext.swing.SwingComponent.wrap(jLable);
        sw2.layoutInfo=javafx.scene.layout.LayoutInfo
        {
            minWidth: T.width*getScale(sceneHeight,sceneWidth)
            width: T.width*getScale(sceneHeight,sceneWidth)
            maxWidth: T.width*getScale(sceneHeight,sceneWidth)
            minHeight: T.height*getScale(sceneHeight,sceneWidth)
            height: T.height*getScale(sceneHeight,sceneWidth)
            maxHeight: T.height*getScale(sceneHeight,sceneWidth)
         };
        def hBox:javafx.scene.layout.HBox= new javafx.scene.layout.HBox();
        hBox.layoutX=T.minX*getScale(sceneHeight,sceneWidth);
        hBox.layoutY=T.minY*getScale(sceneHeight,sceneWidth);
        hBox.content=[sw2];
        
         var input:java.io.InputStream = getClass().getResourceAsStream("/de/mfo/jsurfer/gui/Nimbus Sans L Regular Surfer.ttf");
        var f:java.awt.Font=java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT , input);
        f=f.deriveFont(T.minY*getScale(sceneHeight,sceneWidth)*faktor);
        jLable.setFont(f);
        jLable.setBorder(BorderFactory.createEmptyBorder());*/
        return javafx.scene.Group
        {
            //translateX: bind x translateY: bind y;
            content:
            [
                hBox
                
            ]
        }
    }
    

}
