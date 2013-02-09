/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author Panda
 */

import javafx.scene.CustomNode;
//import com.bric.swing;
//import com.bric.swing.ColorPicker;

import javax.vecmath.*;
import javafx.scene.Group;
import java.awt.*;
import java.lang.System;
import javax.swing.event.*;
import javafx.scene.Node;
import javafx.scene.layout.LayoutInfo;

public class ColorChooser extends CustomNode
{
    
    public var color: Color3f;
    
    public var width:Number /*on replace
    {
        colorPicker.setPreferredSize( new Dimension(width,height) );
    }*/;
    
    public var height:Number /*on replace
    {
        colorPicker.setPreferredSize( new Dimension(width,height) );
    }*/;
    
    var colorPicker: ColorChooserBasic=new ColorChooserBasic(width,height); 


    public function setColor(color:javax.vecmath.Color3f ):Void
    {
        colorPicker.setRGB(color.x*255, color.y*255, color.z*255);
    }
    

    public var x: Number;
//setRenderSize
    public var y: Number;
    
    public override function create(): Node {
        /*initMaterials();
        initLights();*/
        //var frontColorPicker: ColorPicker=new ColorPicker(false) ;
        /*ColorPicker frontColorPicker;
        frontColorPicker = new ColorPicker( false );*/
        colorPicker.setOpaque( false );
        colorPicker.setForeground( Color.WHITE );
        //colorPicker.setPreferredSize( new Dimension( 150, 150 ) );
        var cb: Color = color.get();
        colorPicker.setRGB( cb.getRed(), cb.getGreen(), cb.getBlue() );
        /*for( c in frontColorPicker.getComponents() )
            if( c instanceof JComponent )
                ( ( JComponent ) c ).setOpaque( false );*/
       colorPicker.getColorPanel().addChangeListener(
       ChangeListener {
            override function stateChanged(e)
                {
                    color= new Color3f(colorPicker.getColor());
                   // System.out.println("color2Picker has changed");
                }
            }
        );
        def sw=JavaFXExtWrapper.wrap(colorPicker);
        sw.layoutInfo=LayoutInfo{
            minWidth: bind height,
            width: bind width
            maxWidth: bind width
            minHeight: bind height
            height: bind height
            maxHeight: bind height
         };
        return Group {
                    translateX: bind x translateY: bind y;
                    content: [

                        sw
                    ]
                }
    }
}
