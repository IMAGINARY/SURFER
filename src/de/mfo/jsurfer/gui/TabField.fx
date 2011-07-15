/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author Panda
 */

public class TabField {
    public var language: java.util.Locale;
    public var sliders:FXSliders;
    public var frontColor: ColorChooser;
    public var backColor: ColorChooser;
    
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
    public var loadSurface:function(url:java.net.URL):Void;
    public var disableButtons:function():Void;
    public var enableButtons:function():Void;
    public-init var fxdLayoutFile:javafx.fxd.FXDNode;
    public var germanGallerys :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(java.util.Locale.GERMAN )-1])new de.mfo.jsurfer.gui.Gallery(i,java.util.Locale.GERMAN  );
    public var englishGallerys:de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(java.util.Locale.ENGLISH)-1])new de.mfo.jsurfer.gui.Gallery(i,java.util.Locale.ENGLISH );
    
    public var germanSurfaceInfo:FXSurfaceInfo;
    public var germanGalleryChooser:FXGalleryChooser;
    public var germanGalleryText:FXGalleryText;
    public var germanGalleryMini:FXGalleryMini;

    public var englishSurfaceInfo:FXSurfaceInfo;
    public var englishGalleryChooser:FXGalleryChooser;
    public var englishGalleryText:FXGalleryText;
    public var englishGalleryMini:FXGalleryMini;

    public var multiSurfaceInfo:LanguageSwitchNode;
    public var multiGalleryChooser:LanguageSwitchNode;
    public var multiGalleryText:LanguageSwitchNode;
    public var multiGalleryMini:LanguageSwitchNode;

    public var tabTextColorEng:javafx.scene.text.Text;
    public var tabTextInfoEng:javafx.scene.text.Text;
    public var tabTextGalleryEng:javafx.scene.text.Text;
    public var tabTextColorGer:javafx.scene.text.Text;
    public var tabTextInfoGer:javafx.scene.text.Text;
    public var tabTextGalleryGer:javafx.scene.text.Text;
    var pointerGallery:Integer=0;
    var pointerSurface:Integer=0;
    /*[
        new de.mfo.jsurfer.gui.Gallery(0 ),
        new de.mfo.jsurfer.gui.Gallery(1 ),
        new de.mfo.jsurfer.gui.Gallery(2 ),
        new de.mfo.jsurfer.gui.Gallery(3 ),
        new de.mfo.jsurfer.gui.Gallery(4 )
    ];*/

    

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
    function setGalleryChooser()
    {
        //fxdButtons.getNode("Surfer").visible=true;
       var R:javafx.geometry.Bounds=tabBoxNode.layoutBounds;
        //System.out.println("RenderPanelOrG:{R.minX},{R.minY},{R.maxX},{R.maxY},{R.width},{R.height}");
       germanGalleryChooser=FXGalleryChooser
        {
            language:java.util.Locale.GERMAN
            width:bind R.width*getScale(sceneHeight,sceneWidth),
            height:bind R.height*getScale(sceneHeight,sceneWidth),
            //x:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth),
            //y:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth),
            gallery:bind pointerGallery
            gallerys:germanGallerys
            setGallery:function(g:Integer):Void{pointerGallery=g;pointerSurface=0;}
        };
       englishGalleryChooser=FXGalleryChooser
        {
            language:java.util.Locale.ENGLISH
            width:bind R.width*getScale(sceneHeight,sceneWidth),
            height:bind R.height*getScale(sceneHeight,sceneWidth),
            //x:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth),
            //y:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth),
            gallery:bind pointerGallery
            gallerys:englishGallerys
            setGallery:function(g:Integer):Void{pointerGallery=g;pointerSurface=0;}
        };
        multiGalleryChooser=LanguageSwitchNode
        {
            language:bind language
            germanNode:germanGalleryChooser
            englishNode:englishGalleryChooser
            x:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth),
            y:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth),
        };


        tabBoxNode.visible=false;
    }
    function setGalleryText()
    {
        //fxdButtons.getNode("Surfer").visible=true;
       var G:javafx.geometry.Bounds=galleryTextNode.layoutBounds;
        //System.out.println("RenderPanelOrG:{R.minX},{R.minY},{R.maxX},{R.maxY},{R.width},{R.height}");
       germanGalleryText=FXGalleryText
       {
            width:bind G.width*getScale(sceneHeight,sceneWidth),
            height:bind G.height*getScale(sceneHeight,sceneWidth),
            //x:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth),
            //y:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth),
            gallerys:germanGallerys,
            gallery:bind pointerGallery
        };

       englishGalleryText=FXGalleryText
       {
            width:bind G.width*getScale(sceneHeight,sceneWidth),
            height:bind G.height*getScale(sceneHeight,sceneWidth),
            //x:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth),
            //y:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth),
            gallerys:englishGallerys,
            gallery:bind pointerGallery
        };
        multiGalleryText=LanguageSwitchNode
        {
            language:bind language
            germanNode:germanGalleryText
            englishNode:englishGalleryText
            x:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth),
            y:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth),
        };
        galleryTextNode.visible=false;
    }
    function setGalleryMini()
    {
        //fxdButtons.getNode("Surfer").visible=true;
       var G:javafx.geometry.Bounds=galleryMiniNode.layoutBounds;
        //System.out.println("RenderPanelOrG:{R.minX},{R.minY},{R.maxX},{R.maxY},{R.width},{R.height}");
       germanGalleryMini=FXGalleryMini
       {
           language:java.util.Locale.GERMAN
            width:bind G.width*getScale(sceneHeight,sceneWidth),
            height:bind G.height*getScale(sceneHeight,sceneWidth),
            //x:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth),
            //y:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth),
            gallerys:germanGallerys,
            gallery:bind pointerGallery
            surface:bind pointerSurface
            press:function(s:Integer):Void
            {
                pointerSurface=s;
                setInfoState();
                loadSurface(germanGallerys[germanGalleryMini.gallery].getEntries()[ germanGalleryMini.surface ].getJSurfURL());

            }
        };
       englishGalleryMini=FXGalleryMini
       {
           language:java.util.Locale.ENGLISH
            width:bind G.width*getScale(sceneHeight,sceneWidth),
            height:bind G.height*getScale(sceneHeight,sceneWidth),
            //x:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth),
            //y:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth),
            gallerys:englishGallerys,
            gallery:bind pointerGallery
            surface:bind pointerSurface
            press:function(s:Integer):Void
            {
                pointerSurface=s;
                setInfoState();
                loadSurface(englishGallerys[englishGalleryMini.gallery].getEntries()[ englishGalleryMini.surface ].getJSurfURL());
            }
        };
        multiGalleryMini=LanguageSwitchNode
        {
            language:bind language
            germanNode:germanGalleryMini
            englishNode:englishGalleryMini
            x:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth),
            y:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth),
        };
        galleryMiniNode.visible=false;
    }
    function setSurfaceInfo()
    {
        //fxdButtons.getNode("Surfer").visible=true;
       var R:javafx.geometry.Bounds=tabBoxNode.layoutBounds;
        //System.out.println("RenderPanelOrG:{R.minX},{R.minY},{R.maxX},{R.maxY},{R.width},{R.height}");
       germanSurfaceInfo=FXSurfaceInfo
       {
            width:bind R.width*getScale(sceneHeight,sceneWidth),
            height:bind R.height*getScale(sceneHeight,sceneWidth),
            //x:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth),
            //y:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth),
            gallerys:germanGallerys,
            gallery:bind pointerGallery,
            surface: bind pointerSurface
        };
        englishSurfaceInfo=FXSurfaceInfo
       {
            width:bind R.width*getScale(sceneHeight,sceneWidth),
            height:bind R.height*getScale(sceneHeight,sceneWidth),
            //x:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth),
            //y:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth),
            gallerys:englishGallerys,
            gallery:bind pointerGallery,
            surface: bind pointerSurface
        };
        multiSurfaceInfo=LanguageSwitchNode
        {
            language:bind language
            germanNode:germanSurfaceInfo
            englishNode:englishSurfaceInfo
            x:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth),
            y:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth),
        };
        tabBoxNode.visible=false;
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
        multiSurfaceInfo.visible=false;
        multiGalleryChooser.visible=true;
        multiGalleryText.visible=true;
        multiGalleryMini.visible=true;
        sliders.setVisibility(false);
        disableButtons();
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
        multiSurfaceInfo.visible=true;
        multiGalleryChooser.visible=false;
        multiGalleryText.visible=false;
        multiGalleryMini.visible=false;
        sliders.setVisibility(true);
        enableButtons();
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
        multiSurfaceInfo.visible=false;
        multiGalleryChooser.visible=false;
        multiGalleryText.visible=false;
        multiGalleryMini.visible=false;
        sliders.setVisibility(true);
        enableButtons();
    }
    public function setHelpState()
    {
        //germanGalleryChooser.gallery=0;
        //germanGalleryMini.surface=0;
        //englishGalleryChooser.gallery=0;
        //englishGalleryMini.surface=0;
        pointerGallery=1;
        pointerSurface=0;
        setGalleryState();
    }
    function setTextField()
    {
        tabTextColorGer=javafx.scene.text.Text
        {
            font: bind javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Color").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
            content: "Farben" textAlignment:javafx.scene.text.TextAlignment.CENTER
            translateX: bind fxdLayoutFile.getNode("Tab_Text_Color").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdLayoutFile.getNode("Tab_Text_Color").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-tabTextColorGer.boundsInLocal.width/2+fxdLayoutFile.getNode("Tab_Text_Color").translateX*getScale(sceneHeight,sceneWidth)
            translateY: bind fxdLayoutFile.getNode("Tab_Text_Color").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
            visible: bind (language==java.util.Locale.GERMAN)
        };
        tabTextColorEng=javafx.scene.text.Text
        {
            font: bind javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Color").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
            content: "Colours" textAlignment:javafx.scene.text.TextAlignment.CENTER
            translateX: bind fxdLayoutFile.getNode("Tab_Text_Color").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdLayoutFile.getNode("Tab_Text_Color").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-tabTextColorGer.boundsInLocal.width/2+fxdLayoutFile.getNode("Tab_Text_Color").translateX*getScale(sceneHeight,sceneWidth)
            translateY: bind fxdLayoutFile.getNode("Tab_Text_Color").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
            visible: bind (language==java.util.Locale.ENGLISH)
        };
        fxdLayoutFile.getNode("Tab_Text_Color").visible=false;

        tabTextInfoGer=javafx.scene.text.Text
        {
            font: bind javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Info").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
            content: "Info" textAlignment:javafx.scene.text.TextAlignment.CENTER
            translateX: bind fxdLayoutFile.getNode("Tab_Text_Info").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdLayoutFile.getNode("Tab_Text_Info").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-tabTextInfoGer.boundsInLocal.width/2+fxdLayoutFile.getNode("Tab_Text_Info").translateX*getScale(sceneHeight,sceneWidth)
            translateY: bind fxdLayoutFile.getNode("Tab_Text_Info").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
            visible: bind (language==java.util.Locale.GERMAN)
        };
        tabTextInfoEng=javafx.scene.text.Text
        {
            font: bind javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Info").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
            content: "Info" textAlignment:javafx.scene.text.TextAlignment.CENTER
            translateX: bind fxdLayoutFile.getNode("Tab_Text_Info").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdLayoutFile.getNode("Tab_Text_Info").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-tabTextInfoGer.boundsInLocal.width/2+fxdLayoutFile.getNode("Tab_Text_Info").translateX*getScale(sceneHeight,sceneWidth)
            translateY: bind fxdLayoutFile.getNode("Tab_Text_Info").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
            visible: bind (language==java.util.Locale.ENGLISH)
        };
        fxdLayoutFile.getNode("Tab_Text_Info").visible=false;

        tabTextGalleryGer=javafx.scene.text.Text
        {
            font: bind javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
            content: "Start" textAlignment:javafx.scene.text.TextAlignment.CENTER
            translateX: bind fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-tabTextGalleryGer.boundsInLocal.width/2+fxdLayoutFile.getNode("Tab_Text_Gallery").translateX*getScale(sceneHeight,sceneWidth)
            translateY: bind fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
            visible: bind (language==java.util.Locale.GERMAN)
        };
        tabTextGalleryEng=javafx.scene.text.Text
        {
            font: bind javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInParent.height*getScale(sceneHeight,sceneWidth)*1)
            content: "Start" textAlignment:javafx.scene.text.TextAlignment.CENTER
            translateX: bind fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInParent.minX*getScale(sceneHeight,sceneWidth)+fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInParent.width*getScale(sceneHeight,sceneWidth)/2-tabTextGalleryGer.boundsInLocal.width/2+fxdLayoutFile.getNode("Tab_Text_Gallery").translateX*getScale(sceneHeight,sceneWidth)
            translateY: bind fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInParent.maxY*getScale(sceneHeight,sceneWidth)
            visible: bind (language==java.util.Locale.ENGLISH)
        };
        fxdLayoutFile.getNode("Tab_Text_Gallery").visible=false;



  /*var tabTextColor:javafx.scene.text.Text;
    var tabTextInfo:javafx.scene.text.Text;
    var tabTextGallery:javafx.scene.text.Text;
    var keyboardTextParameters:javafx.scene.text.Text;
    var keyboardTextOperations:javafx.scene.text.Text;
    var keyboardTextXYZ:javafx.scene.text.Text;*/
    //"Tab_Text_Color"
    //"Tab_Text_Info"
    //"Tab_Text_Gallery"
    //"Text_Keyboard_Parameters"
    //"Text_Keyboard_Operations"
    //"Text_Keyboard_XYZ"
    }
    public function set()
    {
        setTextField();
        setColoChooser();
        setGalleryChooser();
        setButtons();
        setGalleryText();
        setGalleryMini();
        setSurfaceInfo();
        setColorState();
    }
    
}
