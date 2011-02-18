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
import javax.swing.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import java.io.File;
import java.io.InputStream;
import javafx.fxd.Duplicator;
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
    //var zoomShaft:Node;
    //var zoomThumb:Node;
    var DragStartY:Number;
    var ParA:Number=0.5 on replace
    {

        if (ParA<0){ParA=0;}
        else if (ParA>1){ParA=1;}
        else
        {
        def min = fxdButtons.getNode("ParA_Shaft").layoutBounds.minY;
	def max = fxdButtons.getNode("ParA_Shaft").layoutBounds.maxY-fxdButtons.getNode("ParA_Thumb").layoutBounds.height;
        //def wert=
        fxdButtons.getNode("ParA_Thumb").translateY = (ParA)*(max-min)+min;
        System.out.println("PAraA:{ParA}  {fxdButtons.getNode("ParA_Thumb").translateX}  {(ParA)*(max-min)+min} {min} {max}");
        }
        surferPanel.a=ParA;
        //setPar();
    };
    var ParB:Number=0.5 on replace
    {

        if (ParB<0){ParB=0;}
        else if (ParB>1){ParB=1;}
        else
        {
        def min = fxdButtons.getNode("ParB_Shaft").layoutBounds.minY;
	def max = fxdButtons.getNode("ParB_Shaft").layoutBounds.maxY-fxdButtons.getNode("ParB_Thumb").layoutBounds.height;
        //def wert=
        fxdButtons.getNode("ParB_Thumb").translateY = (ParB+0.5)*(max-min);
       // System.out.println("PAraB:{ParB}  {fxdButtons.getNode("ParB_Thumb").translateX}  {(ParB)*(max-min)+min} {min} {max}");
        }
        surferPanel.b=ParB;
       // setPar();
    };
    var ParC:Number=0.5 on replace
    {

        if (ParC<0){ParC=0;}
        else if (ParC>1){ParC=1;}
        else
        {
        def min = fxdButtons.getNode("ParC_Shaft").layoutBounds.minY;
	def max = fxdButtons.getNode("ParC_Shaft").layoutBounds.maxY-fxdButtons.getNode("ParC_Thumb").layoutBounds.height;
        //def wert=
        fxdButtons.getNode("ParC_Thumb").translateY = (ParB+0.5)*(max-min)+min;
       // System.out.println("PAraB:{ParB}  {fxdButtons.getNode("ParB_Thumb").translateX}  {(ParB)*(max-min)+min} {min} {max}");
        }
        surferPanel.c=ParC;

    };
    var ParD:Number=0.5 on replace
    {

        if (ParD<0){ParD=0;}
        else if (ParD>1){ParD=1;}
        else
        {
        def min = fxdButtons.getNode("ParD_Shaft").layoutBounds.minY;
	def max = fxdButtons.getNode("ParD_Shaft").layoutBounds.maxY-fxdButtons.getNode("ParD_Thumb").layoutBounds.height;
        //def wert=
        fxdButtons.getNode("ParD_Thumb").translateY = (ParD)*(max-min)+min;
        //System.out.println("PAraB:{ParB}  {fxdButtons.getNode("ParB_Thumb").translateX}  {(ParB)*(max-min)+min} {min} {max}");
        }
        surferPanel.d=ParD;
        //setPar();
    };
    var ParAUse:Boolean = bind surferPanel.usedA on replace
    {
        //fxdButtons.getNode("ParA").visible=ParAUse;
        setPar();
    }
    var ParBUse:Boolean = bind surferPanel.usedB on replace
    {
        setPar();
        //fxdButtons.getNode("ParB").visible=ParBUse;
    }

    var ParCUse:Boolean = bind surferPanel.usedC on replace
    {
        setPar();
        //fxdButtons.getNode("ParB").visible=ParBUse;
    }
    var ParDUse:Boolean = bind surferPanel.usedD on replace
    {
        setPar();
        //fxdButtons.getNode("ParB").visible=ParBUse;
    }
    var sliderWidth:Number=0;
    var zoomScale:Number=bind surferPanel.scale on replace
    {
        def min = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.minY;
	//def max = fxdButtons.getNode("Zoom_Shaft").layoutBounds.maxX-fxdButtons.getNode("Zoom_Shaft").layoutBounds.width;
        def max = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_Zoom_Knob").layoutBounds.height*0.6;
        fxdButtons.getNode("Slider_Zoom_Knob").translateY = (zoomScale)*(max-min);
        System.out.println("zoomScale:{(zoomScale)*(max-min)} {zoomScale} {min} {max}");
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
                url:    "{__DIR__}surfer_touchscreen_1920_x_1080.fxz"
		backgroundLoading: false;
	}

        function getScale(n:Number, w:Number):Number
        {
            var tmp:Number = w/fxdButtons.layoutBounds.maxY;
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
                        width:bind R.height*getScale(height,width),
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
        //zoomShaft = fxdButtons.getNode("Zoom_Shaft");
	//zoomThumb = fxdButtons.getNode("Zoom_Thumb");
        //var  bound: Bounds;// =fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").boundsInLocal;
	System.out.println("StartHöhe:{fxdButtons.getNode("Slider_Zoom_Knob").translateY}");
        sliderWidth= fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").layoutBounds.width;
        fxdButtons.getNode("Slider_Zoom_Knob").onMousePressed = function (ev:MouseEvent){sliderMouseDown(ev, "Slider_Zoom")};
	fxdButtons.getNode("Slider_Zoom_Knob").onMouseDragged = function (ev:MouseEvent)
        {
            var x:Number=sliderMouseDrag(ev, "Slider_Zoom");
            def min = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.minY;
            def max = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_Zoom_Knob").layoutBounds.height*0.8;
            surferPanel.setScale((x-min)/(max-min));
        };
        def m:MouseEvent=MouseEvent{};
        sliderMouseDrag(m,"Slider_Zoom");
        fxdButtons.getNode("Slider_Zoom_Button_Minus").onMousePressed=function(e: MouseEvent): Void {surferPanel.setScale(surferPanel.scale-0.025); };
        fxdButtons.getNode("Slider_Zoom_Button_Plus").onMousePressed=function(e: MouseEvent): Void {surferPanel.setScale(surferPanel.scale+0.025); };

        /*def min = zoomShaft.layoutBounds.minX;
	def max = zoomShaft.layoutBounds.maxX-zoomThumb.layoutBounds.width;
        zoomThumb.translateX = ((zoomScale)*(max-min)+min);*/
    }
    function setParameter()
    {

        //var ParAX:Number;
        //var ParBX:Number;
        fxdButtons.getNode("ParA_Thumb").onMousePressed = function (ev:MouseEvent){sliderMouseDown(ev, "ParA")};
	fxdButtons.getNode("ParA_Thumb").onMouseDragged = function (ev:MouseEvent)
        {
            var x:Number=sliderMouseDrag(ev, "ParA");
            def min = fxdButtons.getNode("ParA_Shaft").layoutBounds.minX;
            def max = fxdButtons.getNode("ParA_Shaft").layoutBounds.maxX-fxdButtons.getNode("ParA_Thumb").layoutBounds.width;
            ParA=((x-min)/(max-min));
        };

        def m:MouseEvent=MouseEvent{};
        sliderMouseDrag(m,"ParA");
        fxdButtons.getNode("ParA_Minus").onMousePressed=function(e: MouseEvent): Void {ParA-=0.025;};
        fxdButtons.getNode("ParA_Plus").onMousePressed=function(e: MouseEvent): Void {ParA+=0.025;};
        fxdButtons.getNode("ParA").visible=surferPanel.usedA;

        fxdButtons.getNode("ParB_Thumb").onMousePressed = function (ev:MouseEvent){sliderMouseDown(ev, "ParB")};
	fxdButtons.getNode("ParB_Thumb").onMouseDragged = function (ev:MouseEvent)
        {
            var x:Number=sliderMouseDrag(ev, "ParB");
            def min = fxdButtons.getNode("ParB_Shaft").layoutBounds.minX;
            def max = fxdButtons.getNode("ParB_Shaft").layoutBounds.maxX-fxdButtons.getNode("ParB_Thumb").layoutBounds.width;
            ParB=((x-min)/(max-min));
        };
        def m2:MouseEvent=MouseEvent{};
        sliderMouseDrag(m2,"ParB");
        fxdButtons.getNode("ParB_Minus").onMousePressed=function(e: MouseEvent): Void {ParB-=0.025;};
        fxdButtons.getNode("ParB_Plus").onMousePressed=function(e: MouseEvent): Void {ParB+=0.025;};
        fxdButtons.getNode("ParB").visible=surferPanel.usedB;
        ParA=surferPanel.a;
        ParB=surferPanel.b;
    }

    function setTextField()
    {
        var T:Bounds=fxdButtons.getNode("Equation").boundsInParent;
        //fxdButtons.getNode("Equation").visible=false;
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
        //var f:Font=test.getFont();
        //Nimbus Sans L Regular Surfer.ttf
        //var f:Font=java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT , new File("{__DIR__}Nimbus Sans L Regular Surfer.ttf"));
        //System.out.println("__DIR__:{__DIR__}");
        var input:InputStream = getClass().getResourceAsStream("/de/mfo/jsurfer/gui/Nimbus Sans L Regular Surfer.ttf");
        var f:Font=java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT , input);
        f=f.deriveFont(T.minY*getScale(height,width)*0.08);
        test.setFont(f);
        test.setBorder( BorderFactory.createEmptyBorder() );
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

	for (s in["Cursor_Left","Cursor_Right","Delete","Complete_Delete","a","b","c","d","x","y","z","Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close","0","1","2","3","4","5","6","7","8","9","Comma","Wrong","Correct","Help"])
	{
           fxdButtons.getNode("Button_Over_{s}").visible=false;
           fxdButtons.getNode("Button_Pressed_{s}").visible=false;
           fxdButtons.getNode("Button_{s}").onMouseEntered =function(e: MouseEvent): Void
           {
               if (e.primaryButtonDown) {Press(s);}
               else {MouseOver(s);}

               inside=true;
               for (t in["Cursor_Left","Cursor_Right","Delete","Complete_Delete","a","b","c","d","x","y","z","Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close","0","1","2","3","4","5","6","7","8","9","Comma","Wrong","Correct","Help"])
                  if (t!=s)Standard(t);
           };
           fxdButtons.getNode("Button_{s}").onMouseExited  =function(e: MouseEvent): Void {Standard(s); inside=false;System.out.println("Exit{s}");};
           //fxdButtons.getNode("Button_{s}").onMouseReleased=function(e: MouseEvent): Void {Release(s); setChar(s);};
           fxdButtons.getNode("Button_{s}").onMousePressed=function(e: MouseEvent): Void {Press(s); setChar(s);};
           fxdButtons.getNode("Button_{s}").onMouseClicked=function(e: MouseEvent): Void
           {
               if (e.button== MouseButton.PRIMARY)
               {

                   if(e.primaryButtonDown){Press(s);}
                   else {Release(s); }
               }
           };

           fxdButtons.getNode("Button_Over_{s}").onMouseEntered=function(e: MouseEvent): Void
           {
               if (e.primaryButtonDown) {Press(s);}
               else {MouseOver(s);}
               inside=true;
               for (t in["Cursor_Left","Cursor_Right","Delete","Complete_Delete","a","b","c","d","x","y","z","Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close","0","1","2","3","4","5","6","7","8","9","Comma","Wrong","Correct","Help"])
                  if (t!=s)Standard(t);
           };
           fxdButtons.getNode("Button_Over_{s}").onMouseExited=function(e: MouseEvent): Void {Standard(s); inside=false;System.out.println("Exit{s}");};
           //fxdButtons.getNode("Button_Over_{s}").onMouseReleased=function(e: MouseEvent): Void {Release(s); setChar(s);};
           fxdButtons.getNode("Button_Over_{s}").onMousePressed=function(e: MouseEvent): Void {Press(s);setChar(s); };
           fxdButtons.getNode("Button_Over_{s}").onMouseClicked=function(e: MouseEvent): Void
           {
               if (e.button== MouseButton.PRIMARY)
               {

                   if(e.primaryButtonDown){Press(s);}
                   else {Release(s);/* setChar(s);*/}
               }
           };
           fxdButtons.getNode("Button_Pressed_{s}").onMouseEntered=function(e: MouseEvent): Void
           {
               if (e.primaryButtonDown) {Press(s);}
               else {MouseOver(s);}
               inside=true;
               for (t in["Cursor_Left","Cursor_Right","Delete","Complete_Delete","a","b","c","d","x","y","z","Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close","0","1","2","3","4","5","6","7","8","9","Comma","Wrong","Correct","Help"])
                  if (t!=s)Standard(t);
           };
           fxdButtons.getNode("Button_Pressed_{s}").onMouseExited=function(e: MouseEvent): Void {Standard(s); inside=false;System.out.println("Exit{s}");};
           //fxdButtons.getNode("Button_Pressed_{s}").onMouseReleased=function(e: MouseEvent): Void {Release(s); setChar(s);};
           fxdButtons.getNode("Button_Pressed_{s}").onMousePressed=function(e: MouseEvent): Void {Press(s); setChar(s);};
           fxdButtons.getNode("Button_Pressed_{s}").onMouseClicked=function(e: MouseEvent): Void
           {
               if (e.button== MouseButton.PRIMARY)
               {

                   if(e.primaryButtonDown){Press(s);}
                   else {Release(s); /*setChar(s);*/}
               }
           };
	}

    }




	var inside : Boolean=false;
        var pressedButton : String="";
        var copynode:Node=javafx.fxd.Duplicator.duplicate(fxdButtons.getNode("Button_a"));
        //copynode.translateX=50;

	public override function create(): Node
	{
            setZoom();
            setButtons();
            setTextField();

            copynode.translateY=100;
            copynode.translateX=100;
            copynode.scaleX=3;
            test.getDocument().addDocumentListener( DocumentListener
            {
                override function changedUpdate( e )
                {
                    surferPanel.surfaceExpressionChanged(test.getText());
                }
                override function insertUpdate( e )
                {
                    surferPanel.surfaceExpressionChanged(test.getText());
                }
                override function removeUpdate( e )
                {
                    surferPanel.surfaceExpressionChanged(test.getText());
                }
            } );
            setColoChooser();
            setRenderPanel();
            setParameter();
            //fxdButtons.opacity=0.5;
		return Group
		{




                        content:
                        [
                            Group
                            {
                            transforms: bind Transform.scale(getScale(height,width),getScale(height,width));

                            content:
                            [
                                    fxdButtons,


                            ]
                            },
                            frontColor,
                            backColor,
                            surferPanel,

                            SurfaceExpression,
                            copynode

                            //javafx.fxd.Duplicator.duplicate(fxdButtons.getNode("Button_a"))
                            /*Group
                                    {
                                        content:[fxdButtons.getNode("Button_a")]
                                        translateX:0
                                        translateY:0
                                    }*/

                        ]
		}
	}
	public function forward()
	{
                pos=test.getCaretPosition();
                expression=test.getText();
		if (pos<expression.length())pos++;
                //test.setText(expression);
                test.setCaretPosition(pos);
                System.out.println("zoomScale:{copynode.localToScene(0,0)}");

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
        public function CompleteDelete()
        {
            pos=0;
            test.setText("");
            test.setCaretPosition(pos);
        }

        public function insertStringInTextfield(c:String)
        {
            pos=test.getCaretPosition();
                expression=test.getText();
		expression="{expression.substring(0,pos)}{c}{expression.substring(pos)}";
		pos+=c.length();
               test.setText(expression);
                test.setCaretPosition(pos);
        }

	public function setChar(c:String )
	{
           //there is no switch case in javafx
           //["Cursor_Left","Cursor_Right","Delete","Complete_Delete","a","b","c","d","x","y","z",
           // "Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close",
           // "0","1","2","3","4","5","6","7","8","9","Comma","Wrong","Correct","Help"]
           if (c=="a" or c=="b" or c=="c" or c=="d" or c=="1" or c=="2" or c=="3" or
               c=="4" or c=="5" or c=="6" or c=="7" or c=="8" or c=="9" or c=="0"or
               c=="x"or c=="y"or c=="z")
           {
               insertStringInTextfield(c);
           }
           else if (c=="Cursor_Left"){back();}
           else if (c=="Cursor_Right"){forward();}
           else if (c=="Delete"){backspace();}
           else if (c=="Complete_Delete"){CompleteDelete();}
           else if (c=="Plus"){insertStringInTextfield("+");}
           else if (c=="Minus"){insertStringInTextfield("-");}
           else if (c=="Times"){insertStringInTextfield("*");}
           else if (c=="Exp_n"){insertStringInTextfield("^");}
           else if (c=="Exp_2"){insertStringInTextfield("^2");}
           else if (c=="Exp_3"){insertStringInTextfield("^3");}
           else if (c=="Bracket_open"){insertStringInTextfield("(");}
           else if (c=="Bracket_close"){insertStringInTextfield(")");}
           else if (c=="Comma"){insertStringInTextfield(".");}
           else if (c=="Correct"){/*Nothing Happens*/}
           else if (c=="Wrong"){/*ToDo NahrichtenFensten ausgeben*/}
           else if (c=="Help"){/*ToDo Help*/}

            /*if (c!="Back" and c!="Forward" and c!="Backspace")
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
            }*/


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
        function sliderMouseDown(ev:MouseEvent, s:String) : Void
	{	//zoomDragStartX = zoomThumb.layoutX;
                DragStartY = fxdButtons.getNode("{s}_Knob").translateY+fxdButtons.getNode("{s}_Shaft").layoutBounds.minY;
	}
	function sliderMouseDrag(ev:MouseEvent, s:String) : Number
	{	var x:Number = DragStartY + /*getScale(height,width)*/ev.dragY/getScale(height,width);
                if (getScale(height,width)==0.0)x=DragStartY;
		def min = fxdButtons.getNode("{s}_Shaft").layoutBounds.minY;
		def max = fxdButtons.getNode("{s}_Shaft").layoutBounds.maxY-fxdButtons.getNode("{s}_Knob").layoutBounds.height;
		//def min = fxdButtons.getNode("{s}_Shaft").localToScene(fxdButtons.getNode("{s}_Shaft").layoutBounds).minX;
                //def max = fxdButtons.getNode("{s}_Shaft").localToScene(fxdButtons.getNode("{s}_Shaft").layoutBounds).maxX-fxdButtons.getNode("{s}_Shaft").localToScene(fxdButtons.getNode("{s}_Thumb").layoutBounds).width;
                /*if(x < min)
		{	x = min;
		}
		else if(x > max)
		{	x = max;
		}*/
                //zoomScale = x/(max-min);
                //surferPanel.setScale((x-min)/(max-min));
                return x;
                //surferPanel.scale=5.0;
		//zoomThumb.translateX = x;
                //System.out.println("Drag:{min},{max},{x},{zoomDragStartX},{(x-min)/(max-min)}");
	}
        /*function zoomMouseDown(ev:MouseEvent) : Void
	{	//zoomDragStartX = zoomThumb.layoutX;
                zoomDragStartX = fxdButtons.getNode("Zoom_Thumb").translateX;
	}
	function zoomMouseDrag(ev:MouseEvent) : Void
	{	var x:Number = zoomDragStartX + ev.dragX/getScale(height,width);
                if (getScale(height,width)==0.0)x=zoomDragStartX;
		def min = fxdButtons.getNode("Zoom_Shaft").layoutBounds.minX;
		def max = fxdButtons.getNode("Zoom_Shaft").layoutBounds.maxX-fxdButtons.getNode("Zoom_Thumb").layoutBounds.width;
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
	}*/
        function setPar():Void
        {
            System.out.println("setPar: ");
            var listIn:String[]=[];
            var listOut:String[]=[];
            if (surferPanel.usedD){insert "D" into listIn;}else{insert "D" into listOut;}
            if (surferPanel.usedC){insert "C" into listIn;}else{insert "C" into listOut;}
            if (surferPanel.usedB){insert "B" into listIn;}else{insert "B" into listOut;}
            if (surferPanel.usedA){insert "A" into listIn;}else{insert "A" into listOut;}
            for (e in listOut)
            {
                fxdButtons.getNode("Slider_{e}").visible=false;

            }

            def i=listIn.size();
            if (i==0)
            {
                fxdButtons.getNode("Slider_Zoom_No_Parameter_Background").visible=true;
                fxdButtons.getNode("Slider_Zoom_Plus_Parameter_Background").visible=false;
                fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").visible=false;
                fxdButtons.getNode("Slider_Plus_Parameter_End").visible=false;
                fxdButtons.getNode("Slider_A").visible=false;
                fxdButtons.getNode("Slider_B").visible=false;
                fxdButtons.getNode("Slider_C").visible=false;
                fxdButtons.getNode("Slider_D").visible=false;
            }
            else
            {
                fxdButtons.getNode("Slider_Zoom_No_Parameter_Background").visible=false;
                fxdButtons.getNode("Slider_Zoom_Plus_Parameter_Background").visible=true;
                var num:Number=0.0;
                for (e in listIn)
                {
                    fxdButtons.getNode("Slider_{e}").visible=true;
                    fxdButtons.getNode("Slider_{e}").translateX=-num*sliderWidth;
                    num+=1.0;
                    System.out.println("setPar: Slider_{e} {fxdButtons.getNode("Slider_{e}").translateX} {-num*sliderWidth} ");
                }
                fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").visible=true;
                fxdButtons.getNode("Slider_Plus_Parameter_End").visible=true;
                fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").scaleX=num;
                fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").translateX=-((num-1.0)*sliderWidth)/2;
                fxdButtons.getNode("Slider_Plus_Parameter_End").translateX=-(num-1.0)*sliderWidth;
                //sliderWidth
            }


        }


    function load():Void
    {
       var fc:JFileChooser  = new JFileChooser();
       fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
       fc.setAcceptAllFileFilterUsed( false );
       var jsurfFilter:JSurfFilter  = new JSurfFilter();
       fc.addChoosableFileFilter( jsurfFilter );

       var returnVal:Integer = fc.showOpenDialog( surferPanel.renderer );
       if( returnVal == JFileChooser.APPROVE_OPTION )
       {
          var f:java.io.File  = fc.getSelectedFile();
          f = jsurfFilter.ensureExtension( f );
          try
          {
             surferPanel.renderer.loadFromFile( f.toURL());
             surferPanel.renderer.repaintImage();
             
             //renderer.getAlgebraicSurfaceRenderer().setParameterValue("a", a);
             surferPanel.a=surferPanel.renderer.getAlgebraicSurfaceRenderer().getParameterValue("a");
             System.out.println("loaded Par a:{surferPanel.renderer.getAlgebraicSurfaceRenderer().getParameterValue("a")}");
             surferPanel.b=surferPanel.renderer.getAlgebraicSurfaceRenderer().getParameterValue("b");
             test.setText(surferPanel.renderer.getAlgebraicSurfaceRenderer().getSurfaceFamilyString());
           }
           catch(e: java.lang.Exception  )
           {
             var  message:String= "Could not save to file \" {f.getName()  } \".";
             if( e.getMessage() != null )
             message = "{message}\n\nMessage: {e.getMessage()}";
             JOptionPane.showMessageDialog( null, message, "Error", JOptionPane.OK_OPTION );
           }
        }
      }

      function save():Void
      {
        var fc:JFileChooser  = new JFileChooser();
        fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
        fc.setAcceptAllFileFilterUsed( false );
        var jsurfFilter:JSurfFilter  = new JSurfFilter();
        fc.addChoosableFileFilter( jsurfFilter );

        var returnVal:Integer = fc.showSaveDialog( surferPanel.renderer );
        if( returnVal == JFileChooser.APPROVE_OPTION )
        {
            var f:java.io.File  = fc.getSelectedFile();
            f = jsurfFilter.ensureExtension( f );
            try
            {
                //file.toURL()
                surferPanel.renderer.saveToFile( f.toURL());
                surferPanel.renderer.repaintImage();
                //renderer.saveToPNG( f, 1024, 1024 );
            }
            catch(e: java.lang.Exception  )
            {
                var  message:String= "Could not save to file \" {f.getName()  } \".";
                if( e.getMessage() != null )
                    message = "{message}\n\nMessage: {e.getMessage()}";
                JOptionPane.showMessageDialog( null, message, "Error", JOptionPane.OK_OPTION );
            }
        }
      }


}

