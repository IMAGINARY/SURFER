/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author Panda
 */

public class FXSliders {
    public var surferPanel:FXSurferPanel;
    public var fxdButtons:javafx.fxd.FXDNode;
    public var getScale:function():Number;
    var sliderWidth:Number=0;
    var DragStartY:Number;
    /*var timeline = javafx.animation.Timeline {
    keyFrames: javafx.animation.KeyFrame {
        time: 20ms
        action: function() {
            setVisibility(true);
        }
    }
}*/
    var ParA:Number= bind surferPanel.a on replace
    {
        def min = fxdButtons.getNode("Slider_A_Shaft").layoutBounds.minY;
	def max = fxdButtons.getNode("Slider_A_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_A_Knob").layoutBounds.height*0.0;
        var value:Number=ParA;if (value<0)value=0;if (value>1)value=1;
        fxdButtons.getNode("Slider_A_Knob").translateY = (value)*(max-min);
    };
    var ParB:Number=0.5 on replace
    {
        def min = fxdButtons.getNode("Slider_B_Shaft").layoutBounds.minY;
	def max = fxdButtons.getNode("Slider_B_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_B_Knob").layoutBounds.height*0.0;
        var value:Number=ParB;if (value<0)value=0;if (value>1)value=1;
        fxdButtons.getNode("Slider_B_Knob").translateY = (value)*(max-min);
    };
    var ParC:Number=0.5 on replace
    {
        def min = fxdButtons.getNode("Slider_C_Shaft").layoutBounds.minY;
	def max = fxdButtons.getNode("Slider_C_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_C_Knob").layoutBounds.height*0.0;
        var value:Number=ParC;if (value<0)value=0;if (value>1)value=1;
        fxdButtons.getNode("Slider_C_Knob").translateY = (value)*(max-min);

    };
    var ParD:Number=0.5 on replace
    {
        def min = fxdButtons.getNode("Slider_D_Shaft").layoutBounds.minY;
	def max = fxdButtons.getNode("Slider_D_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_D_Knob").layoutBounds.height*0.0;
        var value:Number=ParD;if (value<0)value=0;if (value>1)value=1;
        fxdButtons.getNode("Slider_D_Knob").translateY = (value)*(max-min);
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
        def min = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.minY;
        def max = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_Zoom_Knob").layoutBounds.height*0.0;
        fxdButtons.getNode("Slider_Zoom_Knob").translateY = (zoomScale)*(max-min);
    };
    function setZoom()
    {
        sliderWidth= fxdButtons.getNode("Slider_Plus_Parameter_Middle_Background").layoutBounds.width;
        fxdButtons.getNode("Slider_Zoom_Knob").onMousePressed = function (ev:javafx.scene.input.MouseEvent){sliderMouseDown(ev, "Slider_Zoom")};
	fxdButtons.getNode("Slider_Zoom_Knob").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=sliderMouseDrag(ev, "Slider_Zoom");
            def min = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.minY;
            def max = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_Zoom_Knob").layoutBounds.height*0.8;
            surferPanel.setScale((x-min)/(max-min));
        };
        def m:javafx.scene.input.MouseEvent=javafx.scene.input.MouseEvent{};
        
        fxdButtons.getNode("Slider_Zoom_Button_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.setScale(surferPanel.scale+0.025); };
        fxdButtons.getNode("Slider_Zoom_Button_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.setScale(surferPanel.scale-0.025); };
        surferPanel.setScale(0.66);
        sliderMouseDrag(m,"Slider_Zoom");
    }
    function setParameter()
    {
        fxdButtons.getNode("Slider_A_Knob").onMousePressed = function (ev:javafx.scene.input.MouseEvent){sliderMouseDown(ev, "Slider_A")};
	fxdButtons.getNode("Slider_A_Knob").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=sliderMouseDrag(ev, "Slider_A");
            def min = fxdButtons.getNode("Slider_A_Shaft").layoutBounds.minY;
            def max = fxdButtons.getNode("Slider_A_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_A_Knob").layoutBounds.height*0.8;
            surferPanel.a=((x-min)/(max-min));
        };
        def mA:javafx.scene.input.MouseEvent=javafx.scene.input.MouseEvent{}; sliderMouseDrag(mA,"Slider_A");
        fxdButtons.getNode("Slider_A_Button_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.a-=0.025;};
        fxdButtons.getNode("Slider_A_Button_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.a+=0.025;};

        fxdButtons.getNode("Slider_B_Knob").onMousePressed = function (ev:javafx.scene.input.MouseEvent){sliderMouseDown(ev, "Slider_B")};
	fxdButtons.getNode("Slider_B_Knob").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=sliderMouseDrag(ev, "Slider_B");
            def min = fxdButtons.getNode("Slider_B_Shaft").layoutBounds.minY;
            def max = fxdButtons.getNode("Slider_B_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_B_Knob").layoutBounds.height*0.8;
            surferPanel.b=((x-min)/(max-min));
        };
        def mB:javafx.scene.input.MouseEvent=javafx.scene.input.MouseEvent{}; sliderMouseDrag(mB,"Slider_B");
        fxdButtons.getNode("Slider_B_Button_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.b-=0.025;};
        fxdButtons.getNode("Slider_B_Button_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.b+=0.025;};

        fxdButtons.getNode("Slider_C_Knob").onMousePressed = function (ev:javafx.scene.input.MouseEvent){sliderMouseDown(ev, "Slider_C")};
	fxdButtons.getNode("Slider_C_Knob").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=sliderMouseDrag(ev, "Slider_C");
            def min = fxdButtons.getNode("Slider_C_Shaft").layoutBounds.minY;
            def max = fxdButtons.getNode("Slider_C_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_C_Knob").layoutBounds.height*0.8;
            surferPanel.c=((x-min)/(max-min));
        };
        def mC:javafx.scene.input.MouseEvent=javafx.scene.input.MouseEvent{}; sliderMouseDrag(mC,"Slider_C");
        fxdButtons.getNode("Slider_C_Button_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.c-=0.025;};
        fxdButtons.getNode("Slider_C_Button_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.c+=0.025;};

        fxdButtons.getNode("Slider_D_Knob").onMousePressed = function (ev:javafx.scene.input.MouseEvent){sliderMouseDown(ev, "Slider_D")};
	fxdButtons.getNode("Slider_D_Knob").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=sliderMouseDrag(ev, "Slider_D");
            def min = fxdButtons.getNode("Slider_D_Shaft").layoutBounds.minY;
            def max = fxdButtons.getNode("Slider_D_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_D_Knob").layoutBounds.height*0.8;
            surferPanel.d=((x-min)/(max-min));
        };
        def mD:javafx.scene.input.MouseEvent=javafx.scene.input.MouseEvent{}; sliderMouseDrag(mD,"Slider_D");
        fxdButtons.getNode("Slider_D_Button_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.d-=0.025;};
        fxdButtons.getNode("Slider_D_Button_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {surferPanel.d+=0.025;};
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
            //System.out.println("setParList({surferPanel.usedA},{surferPanel.usedB},{surferPanel.usedC},{surferPanel.usedD}): {listIn}");
            for (e in listOut)
            {
                fxdButtons.getNode("Slider_{e}").visible=false;
                //System.out.println(" in : {e}");
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
                    //System.out.println("doPar: Slider_{e} {fxdButtons.getNode("Slider_{e}").translateX} {-num*sliderWidth} ");
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
        return DragStartY + ev.dragY/getScale();
    }
    public function set(): Void
    {
        setZoom();
        setParameter()

    }

}
