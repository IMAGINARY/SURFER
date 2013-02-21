/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.fxgui;
import java.lang.System;
/**
 * @author Panda
 */

public class LanguageSwitchNode extends javafx.scene.CustomNode
{
    public-init var germanNode: javafx.scene.CustomNode;
    public-init var englishNode: javafx.scene.CustomNode;
    public var language: java.util.Locale on replace
    {
        if (language==java.util.Locale.GERMAN)
        {
            germanNode.visible=true;
            //for(n:javafx.scene.CustomNode in[englishNode])n.visible=false;
            englishNode.visible=false;
            //System.out.println("LanguageSwitchNode:case1");
        }
        else if (language==java.util.Locale.ENGLISH)
        {
            englishNode.visible=true;
            germanNode.visible=false;
            //System.out.println("LanguageSwitchNode:case2");
            //for(n:javafx.scene.CustomNode in[germanNode])n.visible=false;
        }
        else
        {
            englishNode.visible=false;
            germanNode.visible=false;
            //System.out.println("LanguageSwitchNode:default");
            //for(n:javafx.scene.CustomNode in[germanNode,englishNode])n.visible=false;
        }
        //System.out.println("LanguageSwitchNode:{englishNode.visible},{germanNode.visible},{language},{java.util.Locale.GERMAN.equals(java.util.Locale.GERMAN)}");
    };
    public var x: Number;
    public var y: Number;
    
    public override function create(): javafx.scene.Node
    {
        return javafx.scene.Group
        {
            translateX: bind x translateY: bind y;
            content:
            [
                /*javafx.scene.layout.Stack
                {
                    content:
                    [*/
                        germanNode,
                        englishNode
                /*    ]
                }*/
           ]
       }
    }

}
