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
    public var germanGallerys    :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(java.util.Locale.GERMAN   )-1])new de.mfo.jsurfer.gui.Gallery(i,java.util.Locale.GERMAN    );
    public var englishGallerys   :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(java.util.Locale.ENGLISH  )-1])new de.mfo.jsurfer.gui.Gallery(i,java.util.Locale.ENGLISH   );
    public var russianGallerys   :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("ru"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("ru") );
    public var portugueseGallerys:de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("pt"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("pt") );
    public var serbianGallerys   :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("sr"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("sr") );
    public var spanishGallerys   :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("es"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("es") );
    public var norskGallerys     :de.mfo.jsurfer.gui.Gallery[]=for (i in [0..de.mfo.jsurfer.gui.Gallery.getNumberOfGalleries(new java.util.Locale("no"))-1])new de.mfo.jsurfer.gui.Gallery(i,new java.util.Locale("no") );
    
    //public var germanSurfaceInfo:FXSurfaceInfo;
    //public var germanGalleryChooser:FXGalleryChooser;
    //public var germanGalleryText:FXGalleryText;
    //public var germanGalleryMini:FXGalleryMini;

    //public var englishSurfaceInfo:FXSurfaceInfo;
    //public var englishGalleryChooser:FXGalleryChooser;
    //public var englishGalleryText:FXGalleryText;
    //public var englishGalleryMini:FXGalleryMini;

    //public var multiSurfaceInfo:LanguageSwitchNode;
    //public var multiGalleryChooser:LanguageSwitchNode;
    //public var multiGalleryText:LanguageSwitchNode;
    //public var multiGalleryMini:LanguageSwitchNode;

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
    /*[
        new de.mfo.jsurfer.gui.Gallery(0 ),
        new de.mfo.jsurfer.gui.Gallery(1 ),
        new de.mfo.jsurfer.gui.Gallery(2 ),
        new de.mfo.jsurfer.gui.Gallery(3 ),
        new de.mfo.jsurfer.gui.Gallery(4 )
    ];*/

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
                    language:java.util.Locale.GERMAN
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallery:bind pointerGallery
                    gallerys:germanGallerys
                    setGallery:function(g:Integer):Void{pointerGallery=g;pointerSurface=0;}
                    visible: bind (language==java.util.Locale.GERMAN)
                }
                FXGalleryChooser
                {
                    language:java.util.Locale.ENGLISH
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallery:bind pointerGallery
                    gallerys:englishGallerys
                    setGallery:function(g:Integer):Void{pointerGallery=g;pointerSurface=0;}
                    visible: bind (language==java.util.Locale.ENGLISH)
                }
                FXGalleryChooser
                {
                    language:new java.util.Locale("ru")
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallery:bind pointerGallery
                    gallerys:russianGallerys
                    setGallery:function(g:Integer):Void{pointerGallery=g;pointerSurface=0;}
                    visible: bind (language==new java.util.Locale("ru"))
                }
                FXGalleryChooser
                {
                    language:new java.util.Locale("pt")
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallery:bind pointerGallery
                    gallerys:portugueseGallerys
                    setGallery:function(g:Integer):Void{pointerGallery=g;pointerSurface=0;}
                    visible: bind (language==new java.util.Locale("pt"))
                }
                FXGalleryChooser
                {
                    language:new java.util.Locale("sr")
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallery:bind pointerGallery
                    gallerys:serbianGallerys
                    setGallery:function(g:Integer):Void{pointerGallery=g;pointerSurface=0;}
                    visible: bind (language==new java.util.Locale("sr"))
                }
                FXGalleryChooser
                {
                    language:new java.util.Locale("es")
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallery:bind pointerGallery
                    gallerys:spanishGallerys
                    setGallery:function(g:Integer):Void{pointerGallery=g;pointerSurface=0;}
                    visible: bind (language==new java.util.Locale("es"))
                }
                FXGalleryChooser
                {
                    language:new java.util.Locale("no")
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallery:bind pointerGallery
                    gallerys:norskGallerys
                    setGallery:function(g:Integer):Void{pointerGallery=g;pointerSurface=0;}
                    visible: bind (language==new java.util.Locale("no"))
                }
            ]
            translateX:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth)
            translateY:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth)
        }

       /*germanGalleryChooser=FXGalleryChooser
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
        };*/


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
                    gallerys:germanGallerys,
                    gallery:bind pointerGallery
                    visible: bind (language==java.util.Locale.GERMAN)
                }
                FXGalleryText
                {
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:englishGallerys,
                    gallery:bind pointerGallery
                    visible: bind (language==java.util.Locale.ENGLISH)
                }
                FXGalleryText
                {
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:russianGallerys,
                    gallery:bind pointerGallery
                    visible: bind (language==new java.util.Locale("ru"))
                }
                FXGalleryText
                {
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:portugueseGallerys,
                    gallery:bind pointerGallery
                    visible: bind (language==new java.util.Locale("pt"))
                }
                FXGalleryText
                {
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:serbianGallerys,
                    gallery:bind pointerGallery
                    visible: bind (language==new java.util.Locale("sr"));
                }
                FXGalleryText
                {
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:spanishGallerys,
                    gallery:bind pointerGallery
                    visible: bind (language==new java.util.Locale("es"));
                }
                FXGalleryText
                {
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:norskGallerys,
                    gallery:bind pointerGallery
                    visible: bind (language==new java.util.Locale("no"));
                }
            ]
            translateX:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth)
            translateY:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth)
        }
/*        
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
        };*/
        galleryTextNode.visible=false;
    }
    /*function setNewPointer()
    {
        germanSurfaceInfo.gallery=pointerGallery;
        germanSurfaceInfo.surface=s;
        englishSurfaceInfo.gallery=pointerGallery;
                englishSurfaceInfo.surface=s;
                pointerSurface=s;
    }*/

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
                    language:java.util.Locale.GERMAN
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:germanGallerys,
                    gallery:bind pointerGallery
                    surface:bind pointerSurface
                    press:function(s:Integer):Void
                    {
                        pointerSurface=s;
                        setInfoState();
                        loadSurface(germanGallerys[pointerGallery].getEntries()[pointerSurface].getJSurfURL());
                    }
                    visible:bind (java.util.Locale.GERMAN==language)
                }
                FXGalleryMini
                {
                    language:java.util.Locale.ENGLISH
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:englishGallerys,
                    gallery:bind pointerGallery
                    surface:bind pointerSurface
                    press:function(s:Integer):Void
                    {
                        pointerSurface=s;
                        setInfoState();
                        loadSurface(englishGallerys[pointerGallery].getEntries()[pointerSurface].getJSurfURL());
                    }
                    visible:bind (java.util.Locale.ENGLISH==language)
                }
                FXGalleryMini
                {
                    language:new java.util.Locale("ru")
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:russianGallerys,
                    gallery:bind pointerGallery
                    surface:bind pointerSurface
                    press:function(s:Integer):Void
                    {
                        pointerSurface=s;
                        setInfoState();
                        loadSurface(russianGallerys[pointerGallery].getEntries()[pointerSurface].getJSurfURL());
                    }
                    visible:bind (new java.util.Locale("ru")==language)
                }
                FXGalleryMini
                {
                   language:new java.util.Locale("pt")
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:portugueseGallerys,
                    gallery:bind pointerGallery
                    surface:bind pointerSurface
                    press:function(s:Integer):Void
                    {
                        pointerSurface=s;
                        setInfoState();
                        loadSurface(portugueseGallerys[pointerGallery].getEntries()[pointerSurface].getJSurfURL());
                    }
                    visible:bind (new java.util.Locale("pt")==language)
                }
                FXGalleryMini
                {
                   language:new java.util.Locale("sr")
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:serbianGallerys,
                    gallery:bind pointerGallery
                    surface:bind pointerSurface
                    press:function(s:Integer):Void
                    {
                        pointerSurface=s;
                        setInfoState();
                        loadSurface(serbianGallerys[pointerGallery].getEntries()[pointerSurface].getJSurfURL());
                    }
                    visible:bind (new java.util.Locale("sr")==language)
                }
                FXGalleryMini
                {
                   language:new java.util.Locale("es")
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:spanishGallerys,
                    gallery:bind pointerGallery
                    surface:bind pointerSurface
                    press:function(s:Integer):Void
                    {
                        pointerSurface=s;
                        setInfoState();
                        loadSurface(serbianGallerys[pointerGallery].getEntries()[pointerSurface].getJSurfURL());
                    }
                    visible:bind (new java.util.Locale("es")==language)
                }
                FXGalleryMini
                {
                   language:new java.util.Locale("no")
                    width:bind G.width*getScale(sceneHeight,sceneWidth),
                    height:bind G.height*getScale(sceneHeight,sceneWidth),
                    gallerys:norskGallerys,
                    gallery:bind pointerGallery
                    surface:bind pointerSurface
                    press:function(s:Integer):Void
                    {
                        pointerSurface=s;
                        setInfoState();
                        loadSurface(serbianGallerys[pointerGallery].getEntries()[pointerSurface].getJSurfURL());
                    }
                    visible:bind (new java.util.Locale("no")==language)
                }


            ]
            translateX:bind (tabBoxNode.translateX+G.minX)*getScale(sceneHeight,sceneWidth)
            translateY:bind (tabBoxNode.translateY+G.minY)*getScale(sceneHeight,sceneWidth)

        }

       /*germanGalleryMini=FXGalleryMini
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

                germanSurfaceInfo.gallery=pointerGallery;
                germanSurfaceInfo.surface=s;
                englishSurfaceInfo.gallery=pointerGallery;
                englishSurfaceInfo.surface=s;
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
                germanSurfaceInfo.gallery=pointerGallery;
                germanSurfaceInfo.surface=s;
                englishSurfaceInfo.gallery=pointerGallery;
                englishSurfaceInfo.surface=s;
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
        };*/
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
                    gallerys:germanGallerys,
                    gallery:bind pointerGallery,
                    surface: bind pointerSurface
                    visible: bind (java.util.Locale.GERMAN==language)
                }
                FXSurfaceInfo
                {
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallerys:englishGallerys,
                    gallery:bind pointerGallery,
                    surface: bind pointerSurface
                    visible: bind (java.util.Locale.ENGLISH==language)
                }
                FXSurfaceInfo
                {
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallerys:russianGallerys,
                    gallery:bind pointerGallery,
                    surface: bind pointerSurface
                    visible: bind (new java.util.Locale("ru")==language)
                }
                FXSurfaceInfo
                {
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallerys:portugueseGallerys,
                    gallery:bind pointerGallery,
                    surface: bind pointerSurface
                    visible: bind (new java.util.Locale("pt")==language)
                }
                FXSurfaceInfo
                {
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallerys:serbianGallerys,
                    gallery:bind pointerGallery,
                    surface: bind pointerSurface
                    visible: bind (new java.util.Locale("sr")==language)
                }
                FXSurfaceInfo
                {
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallerys:spanishGallerys,
                    gallery:bind pointerGallery,
                    surface: bind pointerSurface
                    visible: bind (new java.util.Locale("es")==language)
                }
                FXSurfaceInfo
                {
                    width:bind R.width*getScale(sceneHeight,sceneWidth),
                    height:bind R.height*getScale(sceneHeight,sceneWidth),
                    gallerys:norskGallerys,
                    gallery:bind pointerGallery,
                    surface: bind pointerSurface
                    visible: bind (new java.util.Locale("no")==language)
                }
            ]
            translateX:bind (tabBoxNode.translateX+R.minX)*getScale(sceneHeight,sceneWidth)
            translateY:bind (tabBoxNode.translateY+R.minY)*getScale(sceneHeight,sceneWidth)
        }

       /*germanSurfaceInfo=FXSurfaceInfo
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
        };*/
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
                    content: "Farben" 
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==java.util.Locale.GERMAN)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Color").boundsInLocal.height)
                    content: "Colours" 
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==java.util.Locale.ENGLISH)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Color").boundsInLocal.height)
                    content: "Цвет"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==new java.util.Locale("ru"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Color").boundsInLocal.height)
                    content: "Cores"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==new java.util.Locale("pt"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Color").boundsInLocal.height)
                    content: "Боје"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==new java.util.Locale("sr"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Color").boundsInLocal.height)
                    content: "Colores"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==new java.util.Locale("es"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Color").boundsInLocal.height)
                    content: "Farger"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==new java.util.Locale("no"))
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
                    content: "Info"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==java.util.Locale.GERMAN)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Info").boundsInLocal.height)
                    content: "Info"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==java.util.Locale.ENGLISH)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Info").boundsInLocal.height)
                    content: "Инфо"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==new java.util.Locale("ru"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Info").boundsInLocal.height)
                    content: "Info"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==new java.util.Locale("pt"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Info").boundsInLocal.height)
                    content: "Информације"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==new java.util.Locale("sr"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Info").boundsInLocal.height)
                    content: "Info"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==new java.util.Locale("es"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Info").boundsInLocal.height)
                    content: "Info"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (language==new java.util.Locale("no"))
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
                    content: "Start"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (java.util.Locale.GERMAN == language)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInLocal.height)
                    content: "Start"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (java.util.Locale.ENGLISH == language)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInLocal.height)
                    content: "Старт"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (new java.util.Locale("ru") == language)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInLocal.height)
                    content: "Início"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (new java.util.Locale("pt") == language)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInLocal.height)
                    content: "Старт"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (new java.util.Locale("sr") == language)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInLocal.height)
                    content: "Inicio"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (new java.util.Locale("es") == language)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Tab_Text_Gallery").boundsInLocal.height)
                    content: "Start"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    visible: bind (new java.util.Locale("no") == language)
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
