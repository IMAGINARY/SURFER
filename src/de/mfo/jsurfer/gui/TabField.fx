/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author Panda
 */

public class TabField {
    public var sliders:FXSliders;
    public var frontColor: ColorChooser;
    public var backColor: ColorChooser;
    public var surfaceInfo:FXSurfaceInfo;
    public var galleryChooser:FXGalleryChooser;
    public var galleryText:FXGalleryText;
    public var galleryMini:FXGalleryMini;
    public var getScale:function (n:Number, w:Number):Number;
    public var sceneWidth:Number;
    public var sceneHeight:Number;
    public var frontColorNode:javafx.scene.Node;
    public var backColorNode:javafx.scene.Node;
    public var buttonGalleryNode:javafx.scene.Node;
    public var buttonInfoNode:javafx.scene.Node;
    public var buttonColorNode:javafx.scene.Node;
    public var buttonGalleryPressedNode:javafx.scene.Node;
    public var buttonInfoPressedNode:javafx.scene.Node;
    public var buttonColorPressedNode:javafx.scene.Node;
    public var tabBoxNode:javafx.scene.Node;
    public var galleryTextNode:javafx.scene.Node;
    public var galleryMiniNode:javafx.scene.Node;
    public var surferPanel:FXSurferPanel;

    

    function setColoChooser()
    {
        var F:javafx.geometry.Bounds=frontColorNode.layoutBounds;
        frontColor=ColorChooser
                   {
                        color: new javax.vecmath.Color3f( 0.70588, 0.22745, 0.14117 ),
                        width:bind F.width*getScale(sceneHeight,sceneWidth),
                        height:bind F.height*getScale(sceneHeight,sceneWidth),
                        x:bind (frontColorNode.translateX+F.minX)*getScale(sceneHeight,sceneWidth),
                        y:bind (frontColorNode.translateY+F.minY)*getScale(sceneHeight,sceneWidth)
                    };
        var B:javafx.geometry.Bounds=backColorNode.layoutBounds;
        backColor=ColorChooser
                   {
                        color: new javax.vecmath.Color3f( 1.0, 0.8, 0.4 ),
                        width:bind B.width*getScale(sceneHeight,sceneWidth),
                        height:bind B.height*getScale(sceneHeight,sceneWidth),
                        x:bind (backColorNode.translateX+B.minX)*getScale(sceneHeight,sceneWidth),
                        y:bind (backColorNode.translateY+B.minY)*getScale(sceneHeight,sceneWidth)
                    };
        backColorNode.visible=false;
        frontColorNode.visible=false;

    }
    function setSurfaceInfoAndGalleryChooser()
    {
        //fxdButtons.getNode("Surfer").visible=true;
       var R:javafx.geometry.Bounds=tabBoxNode.layoutBounds;
        //System.out.println("RenderPanelOrG:{R.minX},{R.minY},{R.maxX},{R.maxY},{R.width},{R.height}");
       surfaceInfo=FXSurfaceInfo
       {
            width:bind R.width*getScale(sceneHeight,sceneWidth),
            height:bind R.height*getScale(sceneHeight,sceneWidth),
            x:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth),
            y:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth)
        };
        galleryChooser=FXGalleryChooser
        {
            width:bind R.width*getScale(sceneHeight,sceneWidth),
            height:bind R.height*getScale(sceneHeight,sceneWidth),
            x:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth),
            y:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth)
        };
        tabBoxNode.visible=false;
    }
    function setGalleryText()
    {
        //fxdButtons.getNode("Surfer").visible=true;
       var G:javafx.geometry.Bounds=galleryTextNode.layoutBounds;
        //System.out.println("RenderPanelOrG:{R.minX},{R.minY},{R.maxX},{R.maxY},{R.width},{R.height}");
       galleryText=FXGalleryText
       {
            width:bind G.width*getScale(sceneHeight,sceneWidth),
            height:bind G.height*getScale(sceneHeight,sceneWidth),
            x:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth),
            y:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth)
        };
        galleryTextNode.visible=false;
    }
    function setGalleryMini()
    {
        //fxdButtons.getNode("Surfer").visible=true;
       var G:javafx.geometry.Bounds=galleryMiniNode.layoutBounds;
        //System.out.println("RenderPanelOrG:{R.minX},{R.minY},{R.maxX},{R.maxY},{R.width},{R.height}");
       galleryMini=FXGalleryMini
       {
            width:bind G.width*getScale(sceneHeight,sceneWidth),
            height:bind G.height*getScale(sceneHeight,sceneWidth),
            x:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth),
            y:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth)
        };
        galleryMiniNode.visible=false;
    }
    function setButtons()
    {
        buttonGalleryNode.onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {setGalleryState();};
        buttonInfoNode.onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {setInfoState();};
        buttonColorNode.onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {setColorState();};
    }
    function setGalleryState()
    {
        buttonGalleryNode.visible=false;
        buttonInfoNode.visible=true;
        buttonColorNode.visible=true;
        buttonGalleryPressedNode.visible=true;
        buttonInfoPressedNode.visible=false;
        buttonColorPressedNode.visible=false;
        frontColor.visible=false;
        backColor.visible=false;
        surferPanel.visible=false;
        surfaceInfo.visible=false;
        galleryChooser.visible=true;
        galleryText.visible=true;
        galleryMini.visible=true;
        sliders.setVisibility(false);

    }
    function setInfoState()
    {
        buttonGalleryNode.visible=true;
        buttonInfoNode.visible=false;
        buttonColorNode.visible=true;
        buttonGalleryPressedNode.visible=false;
        buttonInfoPressedNode.visible=true;
        buttonColorPressedNode.visible=false;
        frontColor.visible=false;
        backColor.visible=false;
        surferPanel.visible=true;
        surfaceInfo.visible=true;
        galleryChooser.visible=false;
        galleryText.visible=false;
        galleryMini.visible=false;
        sliders.setVisibility(true);
    }
    function setColorState()
    {
        buttonGalleryNode.visible=true;
        buttonInfoNode.visible=true;
        buttonColorNode.visible=false;
        buttonGalleryPressedNode.visible=false;
        buttonInfoPressedNode.visible=false;
        buttonColorPressedNode.visible=true;
        frontColor.visible=true;
        backColor.visible=true;
        surferPanel.visible=true;
        surfaceInfo.visible=false;
        galleryChooser.visible=false;
        galleryText.visible=false;
        galleryMini.visible=false;
        sliders.setVisibility(true);
    }
    public function set()
    {
        setColoChooser();
        setSurfaceInfoAndGalleryChooser();
        setButtons();
        setGalleryText();
        setGalleryMini();
        setColorState();
    }
    
}
