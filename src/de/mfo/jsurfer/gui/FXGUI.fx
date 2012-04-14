/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author Panda
 */

import java.lang.System;

public class FXGUI extends javafx.scene.CustomNode
{
    public var showImpressum:Boolean=false;
    public-init var showPrint:Boolean;
    //public var realHeight:Number=bind fxdLayoutFile.layoutBounds.maxY*getScale(height,width);
    //public var
    public function realHeight(n:Number, w:Number):Number
        {
            var tmp:Number = w/fxdLayoutFile.layoutBounds.maxX;
            if (tmp*(fxdLayoutFile.layoutBounds.maxY)>n)
            {
                tmp=n/fxdLayoutFile.layoutBounds.maxY;
            }
            return (n-tmp*fxdLayoutFile.layoutBounds.maxY);
        }
    def fxdLayoutFile:javafx.fxd.FXDNode = javafx.fxd.FXDNode
	{
                url:    "{__DIR__}surfer_touchscreen_1920_x_1080.fxz"
		backgroundLoading: false;
	}
        
    var AlgebraicExpressionButtonPanel:FXAlgebraicExpressionButtonPanel = FXAlgebraicExpressionButtonPanel
    {
        getScale: getScale,
        sceneWidth: bind width,
        sceneHeight:bind height,
        fxdLayoutFile:fxdLayoutFile,
        surferPanel: bind surferPanel,
        showImpressum:function(){showImpressum=true;},
        showPrint:showPrint
    }
    public var language:java.util.Locale=bind AlgebraicExpressionButtonPanel.language;
    
    var sliders:FXSliders= FXSliders {
        surferPanel: bind surferPanel,
        fxdButtons: fxdLayoutFile,
        getScale: getScale,
        sceneWidth: bind width,
        sceneHeight:bind height,
        scene:scene
    }
    
    
    def loadURL:function(url:java.net.URL):Void=function(url:java.net.URL)
    {

        try
        {
            surferPanel.renderer.loadFromFile( url);
            surferPanel.renderer.repaintImage();
            try{surferPanel.a=surferPanel.renderer.getAlgebraicSurfaceRenderer().getParameterValue("a");}catch(e: java.lang.Exception  ){}
            try{surferPanel.b=surferPanel.renderer.getAlgebraicSurfaceRenderer().getParameterValue("b");}catch(e: java.lang.Exception  ){}
            try{surferPanel.c=surferPanel.renderer.getAlgebraicSurfaceRenderer().getParameterValue("c");}catch(e: java.lang.Exception  ){}
            try{surferPanel.d=surferPanel.renderer.getAlgebraicSurfaceRenderer().getParameterValue("d");}catch(e: java.lang.Exception  ){}
            surferPanel.scale=surferPanel.renderer.getScale()/4+0.5;
            def FrontColor:javax.vecmath.Color3f=surferPanel.renderer.getAlgebraicSurfaceRenderer().getFrontMaterial().getColor();
            def BackColor :javax.vecmath.Color3f=surferPanel.renderer.getAlgebraicSurfaceRenderer().getBackMaterial() .getColor();
            tabField.frontColor.setColor(FrontColor);
            tabField.backColor.setColor(BackColor);
            AlgebraicExpressionButtonPanel.ExpressionField.setText(surferPanel.renderer.getAlgebraicSurfaceRenderer().getSurfaceFamilyString());
        }
        catch(e: java.lang.Exception  )
        {
            var  message:String= "Could not open file \" {url.getPath()  } \".";
            if( e.getMessage() != null )
            message = "{message}\n\nMessage: {e.getMessage()}";
            javax.swing.JOptionPane.showMessageDialog( null, message, "Error", javax.swing.JOptionPane.OK_OPTION );
        }
    }
    var tabField:TabField = TabField
    {
        language:bind this.language//java.util.Locale.GERMAN,
        sliders: sliders,
        getScale: getScale,
        sceneWidth: bind width;
        sceneHeight:bind height;
        surferPanel: bind surferPanel;
        frontColorNode: fxdLayoutFile.getNode("Colorpicker_1"),
        backColorNode:  fxdLayoutFile.getNode("Colorpicker_2"),
        buttonGalleryNode:fxdLayoutFile.getNode("Button_Gallery"),
        buttonInfoNode:fxdLayoutFile.getNode("Button_Info"),
        buttonColorNode:fxdLayoutFile.getNode("Button_Color"),
        buttonGalleryPressedNode:fxdLayoutFile.getNode("Button_Pressed_Gallery"),
        buttonInfoPressedNode:fxdLayoutFile.getNode("Button_Pressed_Info"),
        buttonColorPressedNode:fxdLayoutFile.getNode("Button_Pressed_Color"),
        tabBoxNode:fxdLayoutFile.getNode("Tab_Box")
        galleryTextNode:fxdLayoutFile.getNode("Gallery_Text")
        galleryMiniNode:fxdLayoutFile.getNode("Gallery_Select")
        loadSurface:loadURL
        disableButtons:function(){AlgebraicExpressionButtonPanel.setIdle();}
        enableButtons:function(){AlgebraicExpressionButtonPanel.setBusy();}
        fxdLayoutFile:fxdLayoutFile
    }
    
    public function setScreenSaver():Void
    {
        showImpressum=false;
        tabField.setHelpState();
    }
    //"Tab_Text_Color"
    //"Tab_Text_Info"
    //"Tab_Text_Gallery"
    //"Text_Keyboard_Parameters"
    //"Text_Keyboard_Operations"
    //"Text_Keyboard_XYZ"
    
    /*var correctExpression:Boolean= bind surferPanel.correctExpression on replace
    {
        fxdButtons.getNode("Button_Correct").visible=surferPanel.correctExpression;
        fxdButtons.getNode("Button_Wrong").visible=not surferPanel.correctExpression;
    }*/

    //var SurfaceExpression:javafx.scene.layout.HBox= new javafx.scene.layout.HBox();
    //var EqualNull:javafx.scene.layout.HBox= new javafx.scene.layout.HBox();
    var surferPanel:FXSurferPanel;
    //var surfaceExpressionField:SwingTextField=new SwingTextField();
    //var test: javax.swing.JTextField=new javax.swing.JTextField("x^2+y^2+z^2+2*x*y*z-1");
    //var frontColor: ColorChooser;
    //var backColor: ColorChooser;
    //var zoomShaft:Node;
    //var zoomThumb:Node;





        //public var scale:Number;
    	public var x: Number;

	public var y: Number;

	public var width: Number on replace
        {
            AlgebraicExpressionButtonPanel.setTextField();

        };
        public var height: Number on replace
        {
            AlgebraicExpressionButtonPanel.setTextField();
        };

	



    function getScale(n:Number, w:Number):Number
    {
        var tmp:Number = w/fxdLayoutFile.layoutBounds.maxX;
        if (tmp*(fxdLayoutFile.layoutBounds.maxY)>n)
        {
            tmp=n/fxdLayoutFile.layoutBounds.maxY;
        }
        return tmp;
    }

    function setRenderPanel()
    {
        //fxdButtons.getNode("Surfer").visible=true;
        var R:javafx.geometry.Bounds=fxdLayoutFile.getNode("Surfer_Rendering").layoutBounds;
        //System.out.println("RenderPanelOrG:{R.minX},{R.minY},{R.maxX},{R.maxY},{R.width},{R.height}");
        surferPanel=FXSurferPanel
                   {
                        width:bind R.height*getScale(height,width),
                        height:bind R.height*getScale(height,width),
                        x:bind (fxdLayoutFile.getNode("Surfer_Rendering").translateX+R.minX)*getScale(height,width),
                        y:bind (fxdLayoutFile.getNode("Surfer_Rendering").translateY+R.minY)*getScale(height,width),
                        frontColor: bind tabField.frontColor.color
                        backColor: bind tabField.backColor.color
                        //scale: bind zoomScale
                    };
        surferPanel.surfaceExpressionChanged(AlgebraicExpressionButtonPanel.ExpressionField.getText());

        fxdLayoutFile.getNode("Surfer_Rendering").visible=false;
        //surferPanel.visible=false;
    }

    

    




    
	//var inside : Boolean=false;
        //var pressedButton : String="";
        //var copynode:Node=javafx.fxd.Duplicator.duplicate(fxdButtons.getNode("Button_a"));
        //copynode.translateX=50;

	public override function create(): javafx.scene.Node
	{
            AlgebraicExpressionButtonPanel.set();
            tabField.set();

            setRenderPanel();
            sliders.set();
            /*def fxlabe=FXLabel
                            {
                                //def jLable:JLabel=new JLabel();
                                string:"=0"
                                Bound:fxdButtons.getNode("Equals_Zero"),
                                getScale: getScale,
                                sceneWidth: bind width;
                                sceneHeight:bind height;
                                faktor:0.08
                            }*/
                           // fxlabe.set();
            //fxdButtons.opacity=0.5;
		return javafx.scene.Group
		{



                        translateX: bind x
                        translateY: bind y
                        content:
                        [
                            javafx.scene.Group
                            {
                                transforms: bind javafx.scene.transform.Transform.scale(getScale(height,width),getScale(height,width));
                                content:
                                [
                                    fxdLayoutFile,
                                    //AlgebraicExpressionButtonPanel.popUp,
                                    //AlgebraicExpressionButtonPanel.languageText
                                ]

                            }
                            tabField.frontColor,
                            tabField.backColor,
                            tabField.SurfaceInfo,
                            tabField.GalleryChooser,
                            tabField.GalleryText,
                            tabField.GalleryMini,
                            surferPanel,

                            AlgebraicExpressionButtonPanel.SurfaceExpression,
                            AlgebraicExpressionButtonPanel.EqualNull,
                            
                            sliders.textValueA,
                            sliders.textValueB,
                            sliders.textValueC,
                            sliders.textValueD,
                            sliders.textValueZoom,
                            sliders.textNameA,
                            sliders.textNameB,
                            sliders.textNameC,
                            sliders.textNameD,
                            sliders.textNameZoom,
                            //tabField.tabTextColorEng,
                            //tabField.tabTextInfoEng,
                            //tabField.tabTextGalleryEng,
                            //AlgebraicExpressionButtonPanel.keyboardTextParametersEng,
                            //AlgebraicExpressionButtonPanel.keyboardTextOperationsEng,
                            //AlgebraicExpressionButtonPanel.keyboardTextXYZEng,
                            //tabField.tabTextColorGer,
                            //tabField.tabTextInfoGer,
                            //tabField.tabTextGalleryGer,
                            //AlgebraicExpressionButtonPanel.keyboardTextParametersGer,
                            //AlgebraicExpressionButtonPanel.keyboardTextOperationsGer,
                            //AlgebraicExpressionButtonPanel.keyboardTextXYZGer,
                            javafx.scene.Group
                            {
                                transforms: bind javafx.scene.transform.Transform.scale(getScale(height,width),getScale(height,width));
                                content:
                                [
                                    //fxdLayoutFile,
                                    AlgebraicExpressionButtonPanel.popUp,
                                    AlgebraicExpressionButtonPanel.languageText,
                                    AlgebraicExpressionButtonPanel.keyboardTextParameters,
                                    AlgebraicExpressionButtonPanel.keyboardTextOperations,
                                    AlgebraicExpressionButtonPanel.keyboardTextXYZ,
                                    tabField.tabTextColor,
                                    tabField.tabTextInfo,
                                    tabField.tabTextGallery,
                                ]

                            }
                            //fxlabe,
                            /*javafx.scene.Group{
                                content:[EqualNull]
                            }*/



                            //copynode

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
	


    function load():Void
    {
       var fc:javax.swing.JFileChooser  = new javax.swing.JFileChooser();
       fc.setFileSelectionMode( javax.swing.JFileChooser.FILES_ONLY );
       fc.setAcceptAllFileFilterUsed( false );
       var jsurfFilter:JSurfFilter  = new JSurfFilter();
       fc.addChoosableFileFilter( jsurfFilter );

       var returnVal:Integer = fc.showOpenDialog( surferPanel.renderer );
       if( returnVal == javax.swing.JFileChooser.APPROVE_OPTION )
       {
          var f:java.io.File  = fc.getSelectedFile();
          f = jsurfFilter.ensureExtension( f );
          loadURL(f.toURL());
          /*try
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
           }*/
        }
      }

      function save():Void
      {
        var fc:javax.swing.JFileChooser  = new javax.swing.JFileChooser();
        fc.setFileSelectionMode( javax.swing.JFileChooser.FILES_ONLY );
        fc.setAcceptAllFileFilterUsed( false );
        var jsurfFilter:JSurfFilter  = new JSurfFilter();
        fc.addChoosableFileFilter( jsurfFilter );

        var returnVal:Integer = fc.showSaveDialog( surferPanel.renderer );
        if( returnVal == javax.swing.JFileChooser.APPROVE_OPTION )
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
                javax.swing.JOptionPane.showMessageDialog( null, message, "Error", javax.swing.JOptionPane.OK_OPTION );
            }
        }
      }
}
