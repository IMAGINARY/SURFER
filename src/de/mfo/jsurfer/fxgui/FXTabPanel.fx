/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.fxgui;

/**
 * @author Panda
 */

import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Stack;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.ShapeSubtract;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextOrigin;




public class FXTabPanel extends CustomNode {
    
    //public var tabHeight:Number;
  var rsize=1.0;//0 or 1 to blend the rectangle
  public var tabWidth:Number;
  public var tabX:Number;
  public-init var index:Integer;
  public-init var title:String;
  public var parentTabPane:FXTabPane;
  public var content:Node[] = [];
  // The clickable tab area
  def tabRect =  bind Rectangle {
        x:  bind tabX, y: bind parentTabPane.heightRect-parentTabPane.tabHeight-7
        arcHeight:7
        arcWidth:7
        width:  bind tabWidth, height:  bind parentTabPane.tabHeight+7
        opacity:bind 1-rsize
        stroke: Color.web("70706E");
    //stroke: Color.web("FF0000");
    //opacity:0.5
    strokeWidth:1
        fill: bind  LinearGradient {
            startX: 0.0, startY: 0.0, endX: 1.0, endY: 1.0
            proportional: true
            stops: [
                Stop { offset: 0.0 color: Color.WHITE }
                Stop { offset: 1.0 color: Color.rgb(192-rsize*64,192-rsize*64,192-rsize*64)}
            ]
    } // fill
  }

  def tabRect2 =  bind Rectangle {
        x:  tabX, y:  parentTabPane.heightRect-parentTabPane.tabHeight
        arcHeight:7
        arcWidth:7
        width:  tabWidth, height:  parentTabPane.tabHeight
        opacity:1//(1-rsize)*1
        stroke: Color.web("70706E");
        fill: bind  LinearGradient {
            startX: 0.0, startY: 0.0, endX: 1.0, endY: 1.0
            proportional: true
            stops: [
                Stop { offset: 0.0 color: Color.DARKRED }
                Stop { offset: 1.0 color: Color.RED}
            ]
    }
  }

  // tab text
  def tabText = Text {
    content: title
    textOrigin: TextOrigin.TOP
    font: Font.font("Verdana", FontWeight.BOLD, 10);
    x: bind 5+tabX
    y: bind (parentTabPane.heightRect-parentTabPane.tabHeight)
  }



  // merge or blend the bottom of tab with the main content region
  /*def blendBottomOfTab = bind Rectangle {
    x:  tabX, y:  parentTabPane.heightRect-parentTabPane.tabHeight-10
    width:  tabWidth, height:  parentTabPane.tabHeight
  }*/

  // content area background area
  def tabPanelContentArea = bind Rectangle {
    x: 0, y: 0
    arcHeight:15
    arcWidth:15
    opacity:1.0-rsize
    width:   parentTabPane.widthRect, height:  (parentTabPane.heightRect-parentTabPane.tabHeight);

  }
  def tabPanelContentArea2 = bind Rectangle {
    x: 0, y: 0
    arcHeight:15
    arcWidth:15
    opacity:0
    width:   parentTabPane.widthRect, height:  (parentTabPane.heightRect-parentTabPane.tabHeight);
  }

  var tabArea =   ShapeSubtract {
    a: bind [  tabRect,
          //blendBottomOfTab,
          tabPanelContentArea
    ] // union tab and content area

    fill: bind  LinearGradient {
            startX: 0.0, startY: 0.0, endX: 1.0, endY: 1.0
            proportional: true
            stops: [
                Stop { offset: 0.0 color: Color.WHITE }
                Stop { offset: 1.0 color: Color.rgb(192-rsize*64,192-rsize*64,192-rsize*64)}
            ]
    } // fill
    stroke: Color.web("70706E");
    //stroke: Color.web("FF0000");
    //opacity:0.5
    strokeWidth:1
    onMousePressed: function( e: MouseEvent ):Void
    {
        println("Mouse {index}");
        if (e.y>parentTabPane.height-parentTabPane.tabHeight)
        {
            println("In {index}");
            var tabs:Node[] = parentTabPane.content;
            for (tab in tabs)
            {
                var tabPanel:FXTabPanel = tab as FXTabPanel;
                if (index == tabPanel.index)
                {
                    println("A tabPane.width: {parentTabPane.width}");
                    delete tab from parentTabPane.content;
              //
                    insert tab into parentTabPane.content;
                    println("B tabPane.width: {parentTabPane.width}");
                    break;
                } // found so make it the top of stack [add to end]
            }
            for (tab in tabs) 
            {
                var tabPanel:FXTabPanel = tab as FXTabPanel;
                if (index == tabPanel.index) 
                {
                    tabPanel.rsize=1;
                    for (n:Node in tabPanel.content)
                    {
                        n.visible=true;
                        
                    }

                    
                } // found so make it the top of stack [add to end]
                else
                {
                    tabPanel.rsize=0;
                    for (n:Node in tabPanel.content)
                    {
                        n.visible=false;
                        
                    }
                }
                
            }

          } // loop through all tab panels
        }
        //translateX: bind (1-rsize)*((-parentTabPane.width-10)*0.0+-tabWidth*1)*0
        //translateY: bind (1-rsize)*(parentTabPane.height-parentTabPane.tabHeight)
       /* onMouseEntered: function(e: MouseEvent): Void {
          println("Entered {title}");
        }
        onMouseExited: function(e: MouseEvent): Void {
          println("Exited {title}");
        }*/
    //effect: DropShadow {offsetX: 0 offsetY: 0.5 spread: 0.0, radius:0.0}
    opacity:bind rsize
  }

 //public override function create(): Node {
  public override function create(): Node {
      //println("Grau:{Color.GRAY}");
    return Group {
      content:  [ tabArea, tabRect, content, tabText/*,tabPanelContentArea2,tabRect2*/]
    }
  }
}

public class FXTabPane extends Stack
{
   public var heightRect:Number;
   public var widthRect:Number;
   public var tabHeight:Number;
}
