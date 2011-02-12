package de.mfo.jsurfer.gui;

import de.mfo.jsurfer.gui.FXSurferPanel;
import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import javafx.fxd.FXDNode;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Bounds;
import javax.vecmath.*;
import java.lang.System;
//import javafx.ext.swing.SwingTextField;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.layout.HBox;
//import javafx.scene.text.Font;
import javafx.ext.swing.*;
import javax.swing.JTextField;
import java.awt.Font;
import javafx.scene.input.MouseButton;

/**
 * @author Panda
 */

public class AlgebraicExpressionButtonPanel extends CustomNode {

    var SurfaceExpression:HBox= new HBox();
    var surferPanel:FXSurferPanel;
    //var surfaceExpressionField:SwingTextField=new SwingTextField();
    var test:JTextField=new JTextField();
    var frontColor: ColorChooser;
    var backColor: ColorChooser;
    var zoomShaft:Node;
    var zoomThumb:Node;
    var zoomDragStartX:Number;
    var zoomScale:Number=bind surferPanel.scale on replace
    {
        def min = zoomShaft.layoutBounds.minX;
	def max = zoomShaft.layoutBounds.maxX-zoomThumb.layoutBounds.width;
        zoomThumb.translateX = ((zoomScale)*(max-min)+min);
        //System.out.println("zoomScale:{(zoomScale)*(max-min)+min}");
    };
    
        public var scale:Number;
    	public var x: Number;

	public var y: Number;

	public var width: Number on replace
        {
            setTextField();

        };
        public var height: Number on replace
        {
            setTextField();
        };

	public var expression:String;

	public var pos:Number;

	def fxdButtons:FXDNode = FXDNode
	{
                url:    "{__DIR__}Panel_4_3.fxd"
		backgroundLoading: false;
	}

        function getScale(n:Number, w:Number):Number
        {
            var tmp:Number = w/fxdButtons.layoutBounds.maxX;
            if (tmp*(fxdButtons.layoutBounds.maxY)>n)
            {
                tmp=n/fxdButtons.layoutBounds.maxY;
            }
            return tmp;
        }
    function setColoChooser()
    {
        var F:Bounds=fxdButtons.getNode("FrontColor").layoutBounds;
        frontColor=ColorChooser
                   {
                        color: new Color3f( 0.70588, 0.22745, 0.14117 ),
                        width:bind F.width*getScale(height,width),
                        height:bind F.height*getScale(height,width),
                        x:bind (fxdButtons.getNode("FrontColor").translateX+F.minX)*getScale(height,width),
                        y:bind (fxdButtons.getNode("FrontColor").translateY+F.minY)*getScale(height,width)
                    };
        var B:Bounds=fxdButtons.getNode("BackColor").layoutBounds;
        backColor=ColorChooser
                   {
                        color: new Color3f( 1.0, 0.8, 0.4 ),
                        width:bind B.width*getScale(height,width),
                        height:bind B.height*getScale(height,width),
                        x:bind (fxdButtons.getNode("BackColor").translateX+B.minX)*getScale(height,width),
                        y:bind (fxdButtons.getNode("BackColor").translateY+B.minY)*getScale(height,width)
                    };
        fxdButtons.getNode("BackColor").visible=false;
        fxdButtons.getNode("FrontColor").visible=false;

    }
    function setRenderPanel()
    {
        var R:Bounds=fxdButtons.getNode("Surfer").layoutBounds;
        surferPanel=FXSurferPanel
                   {
                        width:bind R.width*getScale(height,width),
                        height:bind R.height*getScale(height,width),
                        x:bind (fxdButtons.getNode("Surfer").translateX+R.minX)*getScale(height,width),
                        y:bind (fxdButtons.getNode("Surfer").translateY+R.minY)*getScale(height,width),
                        frontColor: bind frontColor.color
                        backColor: bind backColor.color
                        //scale: bind zoomScale
                    };
        surferPanel.surfaceExpressionChanged("x^2+y^2+z^2+2*x*y*z-1");

        fxdButtons.getNode("Surfer").visible=false;
    }
    function setZoom()
    {
        zoomShaft = fxdButtons.getNode("Zoom_Shaft");
	zoomThumb = fxdButtons.getNode("Zoom_Thumb");
	zoomThumb.onMousePressed = zoomMouseDown;
	zoomThumb.onMouseDragged = zoomMouseDrag;
        def m:MouseEvent=MouseEvent{};
        zoomMouseDrag(m);
        fxdButtons.getNode("Zoom_Minus").onMousePressed=function(e: MouseEvent): Void {surferPanel.setScale(surferPanel.scale-0.025); };
        fxdButtons.getNode("Zoom_Plus").onMousePressed=function(e: MouseEvent): Void {surferPanel.setScale(surferPanel.scale+0.025); };
        /*def min = zoomShaft.layoutBounds.minX;
	def max = zoomShaft.layoutBounds.maxX-zoomThumb.layoutBounds.width;
        zoomThumb.translateX = ((zoomScale)*(max-min)+min);*/
    }
    function setTextField()
    {
        var T:Bounds=fxdButtons.getNode("Text").boundsInParent;
        System.out.println("TxtOrG:{T.minX},{T.minY},{T.maxX},{T.maxY},{T.width},{T.height}");

        def sw:SwingComponent=SwingComponent.wrap(test);
        sw.layoutInfo=LayoutInfo
        {
            minWidth: T.width*getScale(height,width)
            width: T.width*getScale(height,width)
            maxWidth: T.width*getScale(height,width)
            minHeight: T.height*getScale(height,width)
            height: T.height*getScale(height,width)
            maxHeight: T.height*getScale(height,width)
         };

        SurfaceExpression.content=
        [
            sw

        ];
        //surfaceExpressionField.font=Font{size:T.minY*getScale(height,width)*0.08};
        var f:Font=test.getFont();
        f=f.deriveFont(T.minY*getScale(height,width)*0.08);
        test.setFont(f);
        SurfaceExpression.layoutX=T.minX*getScale(height,width);
        SurfaceExpression.layoutY=T.minY*getScale(height,width);
    }
    function setButtons()
    {
        /*fxdButtons.getNode("Button_Over_Back").onMousePressed=function(e: MouseEvent): Void {Press("Back"); back();};
	fxdButtons.getNode("Button_Over_Forward").onMousePressed=function(e: MouseEvent): Void {Press("Forward"); forward();};
	fxdButtons.getNode("Button_Over_Backspace").onMousePressed=function(e: MouseEvent): Void {Press("Backspace"); backspace();};

	for (s in["a","b","x","y","z","+","-","*","^","(",")","0","1","2","3","4","5","6","7","8","9","."])
	{
            fxdButtons.getNode("Button_{s}").onMousePressed=function(e: MouseEvent): Void {Press(s); };
            fxdButtons.getNode("Button_Over_{s}").onMousePressed=function(e: MouseEvent): Void {Press(s); };
            fxdButtons.getNode("Button_Pressed_{s}").onMousePressed=function(e: MouseEvent): Void {Press(s); };
	}*/

	for (s in["Back","Forward","Backspace","a","b","x","y","z","+","-","*","^","(",")","0","1","2","3","4","5","6","7","8","9","."])
	{
           fxdButtons.getNode("Button_Over_{s}").visible=false;
           fxdButtons.getNode("Button_Pressed_{s}").visible=false;
           fxdButtons.getNode("Button_{s}").onMouseEntered =function(e: MouseEvent): Void
           {
               if (e.primaryButtonDown) {Press(s);}
               else {MouseOver(s);}
 
               inside=true;
               for (t in["Back","Forward","Backspace","a","b","x","y","z","+","-","*","^","(",")","0","1","2","3","4","5","6","7","8","9","."])
                  if (t!=s)Standard(t);
           };
           fxdButtons.getNode("Button_{s}").onMouseExited  =function(e: MouseEvent): Void {Standard(s); inside=false;System.out.println("Exit{s}");};
           //fxdButtons.getNode("Button_{s}").onMouseReleased=function(e: MouseEvent): Void {Release(s); setChar(s);};
           fxdButtons.getNode("Button_{s}").onMousePressed=function(e: MouseEvent): Void {Press(s); };
           fxdButtons.getNode("Button_{s}").onMouseClicked=function(e: MouseEvent): Void
           {
               if (e.button== MouseButton.PRIMARY)
               {
                   
                   if(e.primaryButtonDown){Press(s);}
                   else {Release(s); setChar(s);}
               }
           };

           fxdButtons.getNode("Button_Over_{s}").onMouseEntered=function(e: MouseEvent): Void
           {
               if (e.primaryButtonDown) {Press(s);}
               else {MouseOver(s);}
               inside=true;
               for (t in["Back","Forward","Backspace","a","b","x","y","z","+","-","*","^","(",")","0","1","2","3","4","5","6","7","8","9","."])
                  if (t!=s)Standard(t);
           };
           fxdButtons.getNode("Button_Over_{s}").onMouseExited=function(e: MouseEvent): Void {Standard(s); inside=false;System.out.println("Exit{s}");};
           //fxdButtons.getNode("Button_Over_{s}").onMouseReleased=function(e: MouseEvent): Void {Release(s); setChar(s);};
           fxdButtons.getNode("Button_Over_{s}").onMousePressed=function(e: MouseEvent): Void {Press(s); };
           fxdButtons.getNode("Button_Over_{s}").onMouseClicked=function(e: MouseEvent): Void
           {
               if (e.button== MouseButton.PRIMARY)
               {

                   if(e.primaryButtonDown){Press(s);}
                   else {Release(s); setChar(s);}
               }
           };
           fxdButtons.getNode("Button_Pressed_{s}").onMouseEntered=function(e: MouseEvent): Void
           {
               if (e.primaryButtonDown) {Press(s);}
               else {MouseOver(s);}
               inside=true;
               for (t in["Back","Forward","Backspace","a","b","x","y","z","+","-","*","^","(",")","0","1","2","3","4","5","6","7","8","9","."])
                  if (t!=s)Standard(t);
           };
           fxdButtons.getNode("Button_Pressed_{s}").onMouseExited=function(e: MouseEvent): Void {Standard(s); inside=false;System.out.println("Exit{s}");};
           //fxdButtons.getNode("Button_Pressed_{s}").onMouseReleased=function(e: MouseEvent): Void {Release(s); setChar(s);};
           fxdButtons.getNode("Button_Pressed_{s}").onMousePressed=function(e: MouseEvent): Void {Press(s); };
           fxdButtons.getNode("Button_Pressed_{s}").onMouseClicked=function(e: MouseEvent): Void
           {
               if (e.button== MouseButton.PRIMARY)
               {

                   if(e.primaryButtonDown){Press(s);}
                   else {Release(s); setChar(s);}
               }
           };
	}

    }




	var inside : Boolean=false;
        var pressedButton : String="";

	public override function create(): Node
	{
            setZoom();
            setButtons();
            setTextField();
            setColoChooser();
            setRenderPanel();
 
		return Group
		{

                        
                        
                        
                        content: 
                        [
                            Group
                            {
                            transforms: bind Transform.scale(getScale(height,width),getScale(height,width));

                            content:
                            [
                                    fxdButtons
                            ]
                            },
                            frontColor,
                            backColor,
                            surferPanel,

                            SurfaceExpression
                            

                        ]
		}
	}
	public function forward()
	{
                pos=test.getCaretPosition();
                //expression=test.getText();
		if (pos<expression.length())pos++;               
                //test.setText(expression);
                test.setCaretPosition(pos);
	}

	public function back()
	{
                pos=test.getCaretPosition();
		if (pos>0)pos--;
                test.setCaretPosition(pos);
	}

	public function backspace()
	{
                pos=test.getCaretPosition();
                expression=test.getText();
		if (pos>0)
		{
			expression="{expression.substring(0,pos-1)}{expression.substring(pos)}";
			pos--;
		}
                test.setText(expression);
                test.setCaretPosition(pos);
	}

	public function setChar(c:String )
	{
            if (c!="Back" and c!="Forward" and c!="Backspace")
            {
                pos=test.getCaretPosition();
                expression=test.getText();
		expression="{expression.substring(0,pos)}{c}{expression.substring(pos)}";
		pos++;
               test.setText(expression);
                test.setCaretPosition(pos);
            }
            else
            {
                if(c=="Back")back();
                if(c=="Forward")forward();
                if(c=="Backspace")backspace();
            }


	}
	function Press(s: String)
	{
		fxdButtons.getNode("Button_{s}").visible=false;
		fxdButtons.getNode("Button_Over_{s}").visible=false;
		fxdButtons.getNode("Button_Pressed_{s}").visible=true;
                pressedButton=s;
	}
	function Release(s: String)
	{
		if(inside) {MouseOver(s);}
		else {Standard(s);}
                pressedButton="";
	}

	function MouseOver(s: String)
	{
            if (s!=pressedButton)
            {
		fxdButtons.getNode("Button_{s}").visible=false;
		fxdButtons.getNode("Button_Over_{s}").visible=true;
		fxdButtons.getNode("Button_Pressed_{s}").visible=false;/**/
            }
	}

	function Standard(s: String)
	{
		fxdButtons.getNode("Button_{s}").visible=true;
		fxdButtons.getNode("Button_Over_{s}").visible=false;
		fxdButtons.getNode("Button_Pressed_{s}").visible=false;
                if (pressedButton==s){pressedButton="";System.out.println("Standard {s}");}
	}
        function zoomMouseDown(ev:MouseEvent) : Void
	{	//zoomDragStartX = zoomThumb.layoutX;
                zoomDragStartX = zoomThumb.translateX;
	}
	function zoomMouseDrag(ev:MouseEvent) : Void
	{	var x:Number = zoomDragStartX + /*getScale(height,width)*/ev.dragX/getScale(height,width);
                if (getScale(height,width)==0.0)x=zoomDragStartX;
		def min = zoomShaft.layoutBounds.minX;
		def max = zoomShaft.layoutBounds.maxX-zoomThumb.layoutBounds.width;
		if(x < min)
		{	x = min;
		}
		else if(x > max)
		{	x = max;
		}
                //zoomScale = x/(max-min);
                surferPanel.setScale((x-min)/(max-min));
                //surferPanel.scale=5.0;
		//zoomThumb.translateX = x;
                //System.out.println("Drag:{min},{max},{x},{zoomDragStartX},{(x-min)/(max-min)}");
	}
}
