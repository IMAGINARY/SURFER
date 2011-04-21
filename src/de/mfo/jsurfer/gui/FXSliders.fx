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
    var timeline = javafx.animation.Timeline {
    keyFrames: javafx.animation.KeyFrame {
        time: 20ms
        action: function() {
            setVisibility(true);
        }
    }
}
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
        //System.out.println("PAraA:{ParA}  {fxdButtons.getNode("ParA_Thumb").translateX}  {(ParA)*(max-min)+min} {min} {max}");
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
        timeline.playFromStart();
    }
    var ParBUse:Boolean = bind surferPanel.usedB on replace
    {
        timeline.playFromStart();
        //fxdButtons.getNode("ParB").visible=ParBUse;
    }

    var ParCUse:Boolean = bind surferPanel.usedC on replace
    {
        timeline.playFromStart();
        //fxdButtons.getNode("ParB").visible=ParBUse;
    }
    var ParDUse:Boolean = bind surferPanel.usedD on replace
    {
        timeline.playFromStart();
        //fxdButtons.getNode("ParB").visible=ParBUse;
    }
    var zoomScale:Number=bind surferPanel.scale on replace
    {

        def min = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.minY;
	//def max = fxdButtons.getNode("Zoom_Shaft").layoutBounds.maxX-fxdButtons.getNode("Zoom_Shaft").layoutBounds.width;
        def max = fxdButtons.getNode("Slider_Zoom_Shaft").layoutBounds.maxY-fxdButtons.getNode("Slider_Zoom_Knob").layoutBounds.height*0.0;
        fxdButtons.getNode("Slider_Zoom_Knob").translateY = (zoomScale)*(max-min);
        //System.out.println("zoomScale:{(zoomScale)*(max-min)} {zoomScale} {min} {max}");
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

        //var ParAX:Number;
        //var ParBX:Number;
        fxdButtons.getNode("ParA_Thumb").onMousePressed = function (ev:javafx.scene.input.MouseEvent){sliderMouseDown(ev, "ParA")};
	fxdButtons.getNode("ParA_Thumb").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=sliderMouseDrag(ev, "ParA");
            def min = fxdButtons.getNode("ParA_Shaft").layoutBounds.minX;
            def max = fxdButtons.getNode("ParA_Shaft").layoutBounds.maxX-fxdButtons.getNode("ParA_Thumb").layoutBounds.width;
            ParA=((x-min)/(max-min));
        };

        def m:javafx.scene.input.MouseEvent=javafx.scene.input.MouseEvent{};
        sliderMouseDrag(m,"ParA");
        fxdButtons.getNode("ParA_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {ParA-=0.025;};
        fxdButtons.getNode("ParA_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {ParA+=0.025;};
        fxdButtons.getNode("ParA").visible=surferPanel.usedA;

        fxdButtons.getNode("ParB_Thumb").onMousePressed = function (ev:javafx.scene.input.MouseEvent){sliderMouseDown(ev, "ParB")};
	fxdButtons.getNode("ParB_Thumb").onMouseDragged = function (ev:javafx.scene.input.MouseEvent)
        {
            var x:Number=sliderMouseDrag(ev, "ParB");
            def min = fxdButtons.getNode("ParB_Shaft").layoutBounds.minX;
            def max = fxdButtons.getNode("ParB_Shaft").layoutBounds.maxX-fxdButtons.getNode("ParB_Thumb").layoutBounds.width;
            ParB=((x-min)/(max-min));
        };
        def m2:javafx.scene.input.MouseEvent=javafx.scene.input.MouseEvent{};
        sliderMouseDrag(m2,"ParB");
        fxdButtons.getNode("ParB_Minus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {ParB-=0.025;};
        fxdButtons.getNode("ParB_Plus").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {ParB+=0.025;};
        fxdButtons.getNode("ParB").visible=surferPanel.usedB;
        ParA=surferPanel.a;
        ParB=surferPanel.b;
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
