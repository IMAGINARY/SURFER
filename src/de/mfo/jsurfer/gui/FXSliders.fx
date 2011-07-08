/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author Panda
 */
//import java.lang.System;
public class FXSliders {
    public var surferPanel:FXSurferPanel;
    public var fxdButtons:javafx.fxd.FXDNode;
    public var getScale:function (n:Number, w:Number):Number;
    public var sceneWidth:Number;
    public var sceneHeight:Number;
    public-init var scene:javafx.scene.Scene;
    var sliderWidth:Number;
    var DragStartY:Number;
    var ParA:Number= bind surferPanel.a on replace
    {
        var value:Number=ParA;if (value<0)value=0;if (value>1)value=1;
        fxdButtons.getNode("Slider_A_Knob").translateY = (1-value)*(max("A")-min("A"));
        textValueA.content="{ValueToString(value)}"
    };
    var ParB:Number=bind surferPanel.b  on replace
    {
        var value:Number=ParB;if (value<0)value=0;if (value>1)value=1;
        fxdButtons.getNode("Slider_B_Knob").translateY = (1-value)*(max("B")-min("B"));
        textValueB.content="{ValueToString(value)}"
    };
    var ParC:Number=bind surferPanel.c  on replace
    {
        var value:Number=ParC;if (value<0)value=0;if (value>1)value=1;
        fxdButtons.getNode("Slider_C_Knob").translateY = (1-value)*(max("C")-min("C"));
        textValueC.content="{ValueToString(value)}"
    };
    var ParD:Number=bind surferPanel.d  on replace
    {
        var value:Number=ParD;if (value<0)value=0;if (value>1)value=1;
        fxdButtons.getNode("Slider_D_Knob").translateY = (1-value)*(max("D")-min("D"));
        textValueD.content="{ValueToString(value)}"
    };
    var ParAUse:Boolean = bind surferPanel.usedA on replace
    {
        setVisibility(true);
    }
    var ParBUse:Boolean = bind surferPanel.usedB on replace
    {
        setVisibility(true);
    }

    var ParCUse:Boolean = bind surferPanel.usedC on replace
    {
        setVisibility(true);
    }
    var ParDUse:Boolean = bind surferPanel.usedD on replace
    {
        setVisibility(true);
    }
    var zoomScale:Number=bind surferPanel.scale on replace
    {
        fxdButtons.getNode("Slider_Zoom_Knob").translateY = (zoomScale)*(max("Zoom")-min("Zoom"));
        //"{ValueToString(zoomScale)}"
        //textValueZoom.content="{(javafx.util.Math.round((javafx.util.Math.pow(10,-zoomScale*4+2))*100) as Double)/100} x"
        textValueZoom.content="{ValueToString(javafx.util.Math.pow(10,-zoomScale*4+2))} x"
    };
    function setZoom()
    {
        sliderWidth=fxdButtons.getNode("Slider_Zoom_Knob").layoutBounds.minX-fxdButtons.getNode("Slider_A_Knob").layoutBounds.minX;
        fxdButtons.getNode("Slider_Zoom_Shaft").onMousePressed = function (ev:javafx.scene.input.MouseEvent)
        {
            surferPanel.renderer.drawCoordinatenSystem(true);
            def n:Number=(ev.y-min("Zoom"))/(max("Zoom")-min("Zoom"));
            surferPanel.setScale(n);
            DragStartY = n*(max("Zoom")-min("Zoom"))+min("Zoom");
        };
        fxdButtons.getNode("Slider_Zoom_Shaft").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=DragStartY + ev.dragY/getScale(sceneHeight,sceneWidth);//sliderMouseDrag(ev, "Slider_Zoom");
            surferPanel.setScale((x-min("Zoom"))/(max("Zoom")-min("Zoom")));
        };
        //surferPanel.renderer.drawCoordinatenSystem(true);
        fxdButtons.getNode("Slider_Zoom_Shaft").onMouseReleased = function (ev:javafx.scene.input.MouseEvent)
        {
            surferPanel.renderer.drawCoordinatenSystem(false);
        }

        fxdButtons.getNode("Slider_Zoom_Button_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.setScale(surferPanel.scale+0.025); };
        fxdButtons.getNode("Slider_Zoom_Button_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.setScale(surferPanel.scale-0.025); };
        surferPanel.setScale(0.66);
    }
    function setParameter()
    {
        fxdButtons.getNode("Slider_A_Shaft").onMousePressed = function (ev:javafx.scene.input.MouseEvent)
        {
            def n:Number=1-(ev.y-min("A"))/(max("A")-min("A"));
            surferPanel.a=n;
            DragStartY = n*(max("A")-min("A"))+min("A");
        };
	fxdButtons.getNode("Slider_A_Shaft").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=DragStartY - ev.dragY/getScale(sceneHeight,sceneWidth);
            surferPanel.a=((x-min("A"))/(max("A")-min("A")));
        };
        def mA:javafx.scene.input.MouseEvent=javafx.scene.input.MouseEvent{}; sliderMouseDrag(mA,"Slider_A");
        fxdButtons.getNode("Slider_A_Button_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.a=javafx.util.Math.round(100.0*surferPanel.a-1)/100.0};
        fxdButtons.getNode("Slider_A_Button_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.a=javafx.util.Math.round(100.0*surferPanel.a+1)/100.0;};

        fxdButtons.getNode("Slider_B_Shaft").onMousePressed = function (ev:javafx.scene.input.MouseEvent)
        {
            def n:Number=1-(ev.y-min("B"))/(max("B")-min("B"));
            surferPanel.b=n;
            DragStartY = n*(max("B")-min("B"))+min("B");
        };
	fxdButtons.getNode("Slider_B_Shaft").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=DragStartY - ev.dragY/getScale(sceneHeight,sceneWidth);
            surferPanel.b=((x-min("B"))/(max("B")-min("B")));
        };
        def mB:javafx.scene.input.MouseEvent=javafx.scene.input.MouseEvent{}; sliderMouseDrag(mB,"Slider_B");
        fxdButtons.getNode("Slider_B_Button_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.b=javafx.util.Math.round(100.0*surferPanel.b-1)/100.0};
        fxdButtons.getNode("Slider_B_Button_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.b=javafx.util.Math.round(100.0*surferPanel.b+1)/100.0};


        fxdButtons.getNode("Slider_C_Shaft").onMousePressed = function (ev:javafx.scene.input.MouseEvent)
        {
            def n:Number=1-(ev.y-min("C"))/(max("C")-min("C"));
            surferPanel.c=n;
            DragStartY = n*(max("C")-min("C"))+min("C");
        };
	fxdButtons.getNode("Slider_C_Shaft").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=DragStartY - ev.dragY/getScale(sceneHeight,sceneWidth);
            surferPanel.c=((x-min("C"))/(max("C")-min("C")));
        };
        def mC:javafx.scene.input.MouseEvent=javafx.scene.input.MouseEvent{}; sliderMouseDrag(mC,"Slider_C");
        fxdButtons.getNode("Slider_C_Button_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.c=javafx.util.Math.round(100.0*surferPanel.c-1)/100.0};
        fxdButtons.getNode("Slider_C_Button_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.c=javafx.util.Math.round(100.0*surferPanel.c+1)/100.0};

        fxdButtons.getNode("Slider_D_Shaft").onMousePressed = function (ev:javafx.scene.input.MouseEvent)
        {
            def n:Number=1-(ev.y-min("D"))/(max("D")-min("D"));
            surferPanel.d=n;
            DragStartY = n*(max("D")-min("D"))+min("D");
        };
	fxdButtons.getNode("Slider_D_Shaft").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=DragStartY - ev.dragY/getScale(sceneHeight,sceneWidth);
            surferPanel.d=((x-min("D"))/(max("D")-min("D")));
        };
        def mD:javafx.scene.input.MouseEvent=javafx.scene.input.MouseEvent{}; sliderMouseDrag(mD,"Slider_D");
        fxdButtons.getNode("Slider_D_Button_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.d=javafx.util.Math.round(100.0*surferPanel.d-1)/100.0};
        fxdButtons.getNode("Slider_D_Button_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.d=javafx.util.Math.round(100.0*surferPanel.d+1)/100.0};
    }

    public function setVisibility(visible:Boolean):Void
    {
        if (visible)
        {
            fxdButtons.getNode("Slider_Zoom").visible=true;
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
                //fxdButtons.getNode("Slider_Zoom_No_Parameter_Background").visible=true;
                fxdButtons.getNode("Slider_Zoom_No_Parameter_Background").visible=false;
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
                //fxdButtons.getNode("Slider_Zoom_Plus_Parameter_Background").visible=true;
                fxdButtons.getNode("Slider_Zoom_Plus_Parameter_Background").visible=false;
                var num:Number=0.0;
                for (e in listIn)
                {
                    fxdButtons.getNode("Slider_{e}").visible=true;
                    fxdButtons.getNode("Slider_{e}").translateX=-num*sliderWidth;
                    num+=1.0;
                }
                //fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").visible=true;
                fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").visible=false;
                fxdButtons.getNode("Slider_Plus_Parameter_End").visible=true;
                fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").scaleX=num;
                fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").translateX=-((num-1.0)*sliderWidth)/2;
                fxdButtons.getNode("Slider_Plus_Parameter_End").translateX=-(num-1.0)*sliderWidth;
                //sliderWidth
            }

        }
        else
        {

            fxdButtons.getNode("Slider_Zoom_No_Parameter_Background").visible=false;
            fxdButtons.getNode("Slider_Zoom_Plus_Parameter_Background").visible=false;
            fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").visible=false;
            fxdButtons.getNode("Slider_Plus_Parameter_End").visible=false;
            fxdButtons.getNode("Slider_A").visible=false;
            fxdButtons.getNode("Slider_B").visible=false;
            fxdButtons.getNode("Slider_C").visible=false;
            fxdButtons.getNode("Slider_D").visible=false;
            fxdButtons.getNode("Slider_Zoom").visible=false;

        }

    }
    function sliderMouseDown(ev:javafx.scene.input.MouseEvent, s:String) : Void
    {
        DragStartY = fxdButtons.getNode("{s}_Knob").translateY+fxdButtons.getNode("{s}_Shaft").layoutBounds.minY;
    }
    function sliderMouseDrag(ev:javafx.scene.input.MouseEvent, s:String) : Number
    {
        return DragStartY + ev.dragY/getScale(sceneHeight,sceneWidth);
    }
    public function setDescription():Void
    {
        //Slider_D_Value Slider_D_Name
        for (s in ["A","B","C","D","Zoom"])
        {
            fxdButtons.getNode("Slider_{s}_Value").visible=false;
            fxdButtons.getNode("Slider_{s}_Name").visible=false;
        }
            //textValue{s};
            textValueA=javafx.scene.text.Text{};
            textValueB=javafx.scene.text.Text{};
            textValueC=javafx.scene.text.Text{};
            textValueD=javafx.scene.text.Text{};
            textValueZoom=javafx.scene.text.Text{};
            textNameA=javafx.scene.text.Text{};
            textNameB=javafx.scene.text.Text{};
            textNameC=javafx.scene.text.Text{};
            textNameD=javafx.scene.text.Text{};
            textNameZoom=javafx.scene.text.Text
            {
                //id:"TextSliderNameZoom"
                font: bind javafx.scene.text.Font.font ("Arial", fxdButtons.getNode("Slider_Zoom_Name").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
                content: "Zoom" textAlignment:javafx.scene.text.TextAlignment.CENTER
                translateX: bind fxdButtons.getNode("Slider_Zoom_Name").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdButtons.getNode("Slider_Zoom_Name").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-textNameZoom.boundsInLocal.width/2
                translateY: bind fxdButtons.getNode("Slider_Zoom_Name").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
                visible: bind fxdButtons.getNode("Slider_Zoom").visible
            };
            textNameA=javafx.scene.text.Text
            {
                //id:"TextSliderNameA"
                font: bind javafx.scene.text.Font.font ("Arial", fxdButtons.getNode("Slider_A_Name").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
                content: "a" textAlignment:javafx.scene.text.TextAlignment.CENTER
                translateX: bind fxdButtons.getNode("Slider_A_Name").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdButtons.getNode("Slider_A_Name").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-textNameA.boundsInLocal.width/2+fxdButtons.getNode("Slider_A").translateX*getScale(sceneHeight,sceneWidth)
                translateY: bind fxdButtons.getNode("Slider_A_Name").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
                visible: bind fxdButtons.getNode("Slider_A").visible
            };
            textNameB=javafx.scene.text.Text
            {
                id:"TextSliderNameB"
                font: bind javafx.scene.text.Font.font ("Arial", fxdButtons.getNode("Slider_B_Name").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
                content: "b" textAlignment:javafx.scene.text.TextAlignment.CENTER
                translateX: bind fxdButtons.getNode("Slider_B_Name").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdButtons.getNode("Slider_B_Name").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-textNameB.boundsInLocal.width/2+fxdButtons.getNode("Slider_B").translateX*getScale(sceneHeight,sceneWidth)
                translateY: bind fxdButtons.getNode("Slider_B_Name").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
                visible: bind fxdButtons.getNode("Slider_B").visible
            };
            textNameC=javafx.scene.text.Text
            {
                id:"TextSliderNameC"
                font: bind javafx.scene.text.Font.font ("Arial", fxdButtons.getNode("Slider_C_Name").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
                content: "c" textAlignment:javafx.scene.text.TextAlignment.CENTER
                translateX: bind fxdButtons.getNode("Slider_C_Name").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdButtons.getNode("Slider_C_Name").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-textNameC.boundsInLocal.width/2+fxdButtons.getNode("Slider_C").translateX*getScale(sceneHeight,sceneWidth)
                translateY: bind fxdButtons.getNode("Slider_C_Name").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
                visible: bind fxdButtons.getNode("Slider_C").visible
            };
            textNameD=javafx.scene.text.Text
            {
                id:"TextSliderNameD"
                font: bind javafx.scene.text.Font.font ("Arial", fxdButtons.getNode("Slider_D_Name").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
                content: "d" textAlignment:javafx.scene.text.TextAlignment.CENTER
                translateX: bind fxdButtons.getNode("Slider_D_Name").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdButtons.getNode("Slider_D_Name").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-textNameD.boundsInLocal.width/2-fxdButtons.getNode("Slider_D").translateX*getScale(sceneHeight,sceneWidth)+fxdButtons.getNode("Slider_D").translateX*getScale(sceneHeight,sceneWidth)
                translateY: bind fxdButtons.getNode("Slider_D_Name").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
                visible: bind fxdButtons.getNode("Slider_D").visible
            };
            textValueZoom=javafx.scene.text.Text
            {
                id:"TextSliderValueZoom"
                font: bind javafx.scene.text.Font.font ("Arial", fxdButtons.getNode("Slider_Zoom_Value").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
                content: "{ValueToString(javafx.util.Math.pow(10,-zoomScale*4+2))} x" textAlignment:javafx.scene.text.TextAlignment.CENTER
                translateX: bind fxdButtons.getNode("Slider_Zoom_Value").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdButtons.getNode("Slider_Zoom_Value").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-textValueZoom.boundsInLocal.width/2
                translateY: bind fxdButtons.getNode("Slider_Zoom_Value").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
                visible: bind fxdButtons.getNode("Slider_Zoom").visible
            };
            textValueA=javafx.scene.text.Text
            {
                id:"TextSliderValueA"
                font: bind javafx.scene.text.Font.font ("Arial", fxdButtons.getNode("Slider_A_Value").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
                content: "{ValueToString(ParA)}" textAlignment:javafx.scene.text.TextAlignment.CENTER
                translateX: bind fxdButtons.getNode("Slider_A_Value").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdButtons.getNode("Slider_A_Value").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-textValueA.boundsInLocal.width/2+fxdButtons.getNode("Slider_A").translateX*getScale(sceneHeight,sceneWidth)
                translateY: bind fxdButtons.getNode("Slider_A_Value").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
                visible: bind fxdButtons.getNode("Slider_A").visible
            };
            textValueB=javafx.scene.text.Text
            {
                id:"TextSliderValueB"
                font: bind javafx.scene.text.Font.font ("Arial", fxdButtons.getNode("Slider_B_Value").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
                content: "{ValueToString(ParB)}" textAlignment:javafx.scene.text.TextAlignment.CENTER
                translateX: bind fxdButtons.getNode("Slider_B_Value").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdButtons.getNode("Slider_B_Value").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-textValueB.boundsInLocal.width/2+fxdButtons.getNode("Slider_B").translateX*getScale(sceneHeight,sceneWidth)
                translateY: bind fxdButtons.getNode("Slider_B_Value").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
                visible: bind fxdButtons.getNode("Slider_B").visible
            };
            textValueC=javafx.scene.text.Text
            {
                id:"TextSliderValueC"
                font: bind javafx.scene.text.Font.font ("Arial", fxdButtons.getNode("Slider_C_Value").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
                content: "{ValueToString(ParC)}" textAlignment:javafx.scene.text.TextAlignment.CENTER
                translateX: bind fxdButtons.getNode("Slider_C_Value").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdButtons.getNode("Slider_C_Value").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-textValueC.boundsInLocal.width/2+fxdButtons.getNode("Slider_C").translateX*getScale(sceneHeight,sceneWidth)
                translateY: bind fxdButtons.getNode("Slider_C_Value").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
                visible: bind fxdButtons.getNode("Slider_C").visible
            };
            textValueD=javafx.scene.text.Text
            {
                id:"TextSliderValueD"
                font: bind javafx.scene.text.Font.font ("Arial", fxdButtons.getNode("Slider_D_Value").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
                content: "{ValueToString(ParD)}" textAlignment:javafx.scene.text.TextAlignment.CENTER
                translateX: bind fxdButtons.getNode("Slider_D_Value").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdButtons.getNode("Slider_D_Value").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-textValueD.boundsInLocal.width/2+fxdButtons.getNode("Slider_D").translateX*getScale(sceneHeight,sceneWidth)
                translateY: bind fxdButtons.getNode("Slider_D_Value").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
                visible: bind fxdButtons.getNode("Slider_D").visible
            };

        

    }
    function min(s:String):Number
    {
        return fxdButtons.getNode("Slider_{s}_Shaft").layoutBounds.minY;//+fxdButtons.getNode("Slider_{s}_Knob").layoutBounds.height*0.5;
        //def max = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_Zoom_Knob").layoutBounds.height*0.5;
    }
    function max(s:String):Number
    {
        return fxdButtons.getNode("Slider_{s}_Shaft").layoutBounds.maxY;//-fxdButtons.getNode("Slider_{s}_Knob").layoutBounds.height*0.5;
        //def max = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_Zoom_Knob").layoutBounds.height*0.5;
    }
    function y(n:Number, s:String):Number
    {
        return n*(1-fxdButtons.getNode("Slider_{s}_Knob").layoutBounds.height)-fxdButtons.getNode("Slider_{s}_Knob").layoutBounds.height*0.5;
    }


    public var textValueA:javafx.scene.text.Text;
    public var textValueB:javafx.scene.text.Text;
    public var textValueC:javafx.scene.text.Text;
    public var textValueD:javafx.scene.text.Text;
    public var textValueZoom:javafx.scene.text.Text;
    public var textNameA:javafx.scene.text.Text;
    public var textNameB:javafx.scene.text.Text;
    public var textNameC:javafx.scene.text.Text;
    public var textNameD:javafx.scene.text.Text;
    public var textNameZoom:javafx.scene.text.Text;
    function ValueToString(value:Number)
    {
        def n1:Integer=(javafx.util.Math.floor(value))as Integer;
        def n2:Integer=(javafx.util.Math.floor((value-n1)*10))as Integer;
        def n3:Integer=(javafx.util.Math.floor((value-n1-n2*0.1)*100))as Integer;
        return "{n1}.{n2}{n3}"
    }

    public function set(): Void
    {
        setDescription();
        setZoom();
        setParameter();

    }

}
