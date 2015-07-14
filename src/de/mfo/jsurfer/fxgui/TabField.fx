/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.fxgui;

/**
 * @author Panda
 */

public class TabField {
    public var language: java.util.Locale on replace { setTextField(); };
    var messages:java.util.ResourceBundle=bind java.util.ResourceBundle.getBundle( "de.mfo.jsurfer.fxgui.MessagesBundle", language );
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

    public var galleries:de.mfo.jsurfer.gui.Gallery[] = bind for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries( language )-1])new de.mfo.jsurfer.gui.Gallery(i, language );

/*
    public var germanGallerys    :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(java.util.Locale.GERMAN   )-1])new de.mfo.jsurfer.gui.Gallery(i,java.util.Locale.GERMAN    );
    public var englishGallerys   :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(java.util.Locale.ENGLISH  )-1])new de.mfo.jsurfer.gui.Gallery(i,java.util.Locale.ENGLISH   );
    public var russianGallerys   :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("ru"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("ru") );
    public var portugueseGallerys:de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("pt"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("pt") );
    public var serbianGallerys   :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("sr"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("sr") );
    public var spanishGallerys   :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("es"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("es") );
    public var norskGallerys     :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("no"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("no") );
    public var koreanGallerys     :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("ko"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("ko") );
    public var chineseGallerys     :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("ko"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("zh") );
    public var turkishGallerys     :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("ko"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("tr") );
    */

    public var SurfaceInfo:javafx.scene.Group;
    public var GalleryChooser:javafx.scene.Group;
    public var GalleryText:javafx.scene.Group;
    public var GalleryMini:javafx.scene.Group;

    /*public var tabTextColorEng:javafx.scene.text.Text;
    public var tabTextInfoEng:javafx.scene.text.Text;
    public var tabTextGalleryEng:javafx.scene.text.Text;
    public var tabTextColorGer:javafx.scene.text.Text;
    public var tabTextInfoGer:javafx.scene.text.Text;
    public var tabTextGalleryGer:javafx.scene.text.Text;*/
    
    public var tabTextColor:javafx.scene.Group;
    public var tabTextInfo:javafx.scene.Group;
    public var tabTextGallery:javafx.scene.Group;
    
    var pointerGallery:Integer=0;
    var pointerSurface:Integer=0;

    function setColorChooser()
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
        GalleryChooser=javafx.scene.Group
        {
            content:
            [
                FXGalleryChooser
                {
                    language: this.language,
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallery:bind pointerGallery
                    gallerys:bind galleries
                    setGallery:function(g:Integer):Void{pointerGallery=g;pointerSurface=0;}
                }
            ]
            translateX:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth)
            translateY:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth)
        }

        tabBoxNode.visible=false;
    }
    function setGalleryText()
    {
        //fxdButtons.getNode("Surfer").visible=true;
        var G:javafx.geometry.Bounds=galleryTextNode.layoutBounds;
        //System.out.println("RenderPanelOrG:{R.minX},{R.minY},{R.maxX},{R.maxY},{R.width},{R.height}");
        GalleryText=javafx.scene.Group
        {
            content:
            [
                FXGalleryText
                {
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:bind galleries,
                    gallery:bind pointerGallery
                }
            ]
            translateX:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth)
            translateY:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth)
        }
        galleryTextNode.visible=false;
    }

    function setGalleryMini()
    {
        //fxdButtons.getNode("Surfer").visible=true;
        var G:javafx.geometry.Bounds=galleryMiniNode.layoutBounds;
        //System.out.println("RenderPanelOrG:{R.minX},{R.minY},{R.maxX},{R.maxY},{R.width},{R.height}");
        GalleryMini=javafx.scene.Group
        {
            content:
            [
                FXGalleryMini
                {
                    language: this.language,
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys: bind galleries,
                    gallery:bind pointerGallery
                    surface:bind pointerSurface
                    press:function(s:Integer):Void
                    {
                        pointerSurface=s;
                        setInfoState();
                        loadSurface(galleries[pointerGallery].getEntries()[pointerSurface].getJSurfURL());
                    }
                }
            ]
            translateX:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth)
            translateY:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth)

        }
        galleryMiniNode.visible=false;
    }
    function setSurfaceInfo()
    {
        //fxdButtons.getNode("Surfer").visible=true;
        var R:javafx.geometry.Bounds=tabBoxNode.layoutBounds;
        //System.out.println("RenderPanelOrG:{R.minX},{R.minY},{R.maxX},{R.maxY},{R.width},{R.height}");
        SurfaceInfo=javafx.scene.Group
        {
            content:
            [
                FXSurfaceInfo
                {
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallerys: bind galleries,
                    gallery:bind pointerGallery,
                    surface: bind pointerSurface
                }
            ]
            translateX:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth)
            translateY:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth)
        }
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
        SurfaceInfo.visible=false;
        GalleryChooser.visible=true;
        GalleryText.visible=true;
        GalleryMini.visible=true;
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
        SurfaceInfo.visible=true;
        GalleryChooser.visible=false;
        GalleryText.visible=false;
        GalleryMini.visible=false;
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
        SurfaceInfo.visible=false;
        GalleryChooser.visible=false;
        GalleryText.visible=false;
        GalleryMini.visible=false;
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
        tabTextColor=javafx.scene.Group
        {
            content:
            [
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Color").boundsInLocal.height)
                    content: bind messages.getString( "colors" )
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                }
            ]
            translateX: bind fxdLayoutFile.getNode("Tab_Text_Color").boundsInLocal.minX+fxdLayoutFile.getNode("Tab_Text_Color").translateX+fxdLayoutFile.getNode("Tab_Text_Color").boundsInLocal.width/2-tabTextColor.boundsInLocal.width/2
            translateY: fxdLayoutFile.getNode("Tab_Text_Color").boundsInLocal.maxY
        }
        
        fxdLayoutFile.getNode("Tab_Text_Color").visible=false;

        tabTextInfo=javafx.scene.Group
        {
            content:
            [
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Info").boundsInLocal.height)
                    content: bind messages.getString( "info" )
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                }
            ]
            translateX: bind fxdLayoutFile.getNode("Tab_Text_Info").boundsInLocal.minX+fxdLayoutFile.getNode("Tab_Text_Info").translateX+fxdLayoutFile.getNode("Tab_Text_Color").boundsInLocal.width/2-tabTextInfo.boundsInLocal.width/2
            translateY: fxdLayoutFile.getNode("Tab_Text_Info").boundsInLocal.maxY
        }
        fxdLayoutFile.getNode("Tab_Text_Info").visible=false;

        tabTextGallery=javafx.scene.Group
        {
            content:
            [
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInLocal.height)
                    content: bind messages.getString( "start" )
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                }
            ]
            translateX: bind fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInLocal.minX+fxdLayoutFile.getNode("Tab_Text_Gallery").translateX+fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInLocal.width/2-tabTextGallery.boundsInLocal.width/2
            translateY: fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInLocal.maxY
        }
        
        fxdLayoutFile.getNode("Tab_Text_Gallery").visible=false;
    }
    public function set()
    {
        setTextField();
        setColorChooser();
        setGalleryChooser();
        setButtons();
        setGalleryText();
        setGalleryMini();
        setSurfaceInfo();
        setColorState();
    }
    
}
