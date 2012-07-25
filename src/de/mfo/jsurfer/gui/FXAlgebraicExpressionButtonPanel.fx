/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;


import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import java.lang.System;
import java.lang.String;
import java.util.Locale;

/**
 * @author Panda
 */

public class FXAlgebraicExpressionButtonPanel
{
    /*var timeline = javafx.animation.Timeline
    {
        keyFrames: javafx.animation.KeyFrame {
        time: 100ms
        action: function() {showImpressum();}
        }
    }*/
    public-init var showPrint:Boolean;
    public-init var showLoadSave:Boolean;
    public-init var showExport:Boolean;
    public-init var gui:FXGUI;
    //public-init var keyboardTextParametersEng:javafx.scene.text.Text;
    //public-init var keyboardTextOperationsEng:javafx.scene.text.Text;
    //public-init var keyboardTextXYZEng:javafx.scene.text.Text;

    public-init var keyboardTextParameters:javafx.scene.Group;
    public-init var keyboardTextOperations:javafx.scene.Group;
    public-init var keyboardTextXYZ:javafx.scene.Group;

    //public-init var keyboardTextParametersGer:javafx.scene.text.Text;
    //public-init var keyboardTextOperationsGer:javafx.scene.text.Text;
    //public-init var keyboardTextXYZGer:javafx.scene.text.Text;

    public function getDefaultLocale():java.util.Locale
    {
        var defaultLang_ISO2:String = java.util.Locale.getDefault().getLanguage();
        var knownLangs_ISO2 = [ "es", "de", "en", "pt", "ru", "sr" ];
        println( defaultLang_ISO2 );
        for( l in knownLangs_ISO2 )
            if( l == defaultLang_ISO2 )
                return new java.util.Locale( l );
        return new java.util.Locale( "en" );
    }

    public var language:java.util.Locale=getDefaultLocale();
    public-init var getScale:function (n:Number, w:Number):Number;
    public-init var showImpressum:function ():Void;
    public var sceneWidth:Number;
    public var sceneHeight:Number;
    public-init var fxdLayoutFile:javafx.fxd.FXDNode;
    public var surferPanel:FXSurferPanel;
    public var popUp:javafx.scene.Group;
    public var languageText:javafx.scene.Group;

    public var ExpressionField: AlwaysFocusedTextBox = AlwaysFocusedTextBox{ text:"x^2+y^2+z^2+2*x*y*z-1", selectOnFocus:false };

    public function set()
    {
        setButtons();
        setPopUp();
        setTextField();
        setTextField2();

// ExpressionFieldToDo
//        ExpressionField.addKeyListener( java.awt.event.KeyListener
//        {
//          override function keyPressed( keyEvent:KeyEvent ) { rewrite( keyEvent/*,KEY_PRESSED*/  ); }
//          override function keyReleased( keyEvent:KeyEvent ) { rewrite( keyEvent/*, KEY_RELEASED*/); }
//          override function keyTyped( keyEvent:KeyEvent ) { rewrite( keyEvent, ); }
//          function rewrite( keyEvent:KeyEvent )
//          {
//              System.out.println(keyEvent);
//              if( keyEvent.getKeyCode() == KeyEvent.VK_COMMA )
//                keyEvent.setKeyCode( KeyEvent.VK_COLON );
//              if( keyEvent.getKeyChar() == ",".charAt( 0 ) )
//                keyEvent.setKeyChar( ".".charAt( 0 ) );
//              if(   (keyEvent.getModifiers()==java.awt.event.InputEvent.CTRL_MASK
//                    or keyEvent.getModifiers()==java.awt.event.InputEvent.CTRL_DOWN_MASK) and
//                    keyEvent.getKeyCode()==java.awt.event.KeyEvent.VK_P and
//                    keyEvent.getID()==java.awt.event.KeyEvent.KEY_RELEASED)
//              {print();}
//          }
//
//        } );
    }


    public var SurfaceExpression:javafx.scene.layout.VBox= new javafx.scene.layout.VBox;
    public var EqualNull:javafx.scene.layout.HBox= javafx.scene.layout.HBox
    {
        //content: [FXLabel{  string:"=0" Bound:fxdLayoutFile.getNode("Equals_Zero"), getScale: getScale,sceneWidth: bind sceneWidth; sceneHeight:bind sceneHeight; faktor:0.08} ]
    };
    public var expression:String;
    public var NullField: javax.swing.JLabel=new javax.swing.JLabel("=0");
    public var pos:Number;
    //var caret:javax.swing.text.Caret=javax.swing.text.DefaultCaret{ override function setVisible(b:Boolean){super.setVisible(true);}} ;
    var surfaceExpression:String = bind ExpressionField.rawText on replace { surferPanel.surfaceExpressionChanged(ExpressionField.rawText) };
    var correctExpression:Boolean= bind surferPanel.correctExpression on replace
    {
        fxdLayoutFile.getNode("Button_Correct").visible=surferPanel.correctExpression;
        fxdLayoutFile.getNode("Button_Wrong").visible=not surferPanel.correctExpression;
    }

    var inside : Boolean=false;
    var pressedButton : String="";
    def size:Number=0.05;

    public function setTextField()
    {
        var T:javafx.geometry.Bounds=fxdLayoutFile.getNode("Equation").boundsInParent;
        var T2:javafx.geometry.Bounds=fxdLayoutFile.getNode("Equals_Zero").boundsInParent;
        fxdLayoutFile.getNode("Equation").visible=false;
        fxdLayoutFile.getNode("Equals_Zero").visible=false;
        //def textField0:javax.swing.JLabel=new javax.swing.JLabel("=0");
        //textField0.setEnabled(arg0);
        def sw2:javafx.ext.swing.SwingComponent=javafx.ext.swing.SwingComponent.wrap(NullField);
        sw2.layoutInfo=javafx.scene.layout.LayoutInfo
        {
            minWidth: T2.width*getScale(sceneHeight,sceneWidth)
            minHeight: T2.height*getScale(sceneHeight,sceneWidth)
            height: T2.height*getScale(sceneHeight,sceneWidth)
            maxHeight: T2.height*getScale(sceneHeight,sceneWidth)
         };
        
        ExpressionField.layoutInfo=javafx.scene.layout.LayoutInfo
        {
            minWidth: T.width*getScale(sceneHeight,sceneWidth)
            width: T.width*getScale(sceneHeight,sceneWidth)
            maxWidth: T.width*getScale(sceneHeight,sceneWidth)
//            minHeight: T.height*getScale(sceneHeight,sceneWidth)
//            height: T.height*getScale(sceneHeight,sceneWidth)
            maxHeight: T.height*getScale(sceneHeight,sceneWidth)
         };
        ExpressionField.styleClass=".TextBoxWOBorder";
        SurfaceExpression.vpos=javafx.geometry.VPos.CENTER;
        SurfaceExpression.layoutInfo=javafx.scene.layout.LayoutInfo
        {
            minWidth: T.width*getScale(sceneHeight,sceneWidth)
            width: T.width*getScale(sceneHeight,sceneWidth)
            maxWidth: T.width*getScale(sceneHeight,sceneWidth)
            minHeight: T.height*getScale(sceneHeight,sceneWidth)
            height: T.height*getScale(sceneHeight,sceneWidth)
            maxHeight: T.height*getScale(sceneHeight,sceneWidth)
         };
        SurfaceExpression.content=[ExpressionField];
        EqualNull.content=[sw2];

        var f=Globals.getJavaFont( T.minY*getScale(sceneHeight,sceneWidth)*size);

        ExpressionField.font = Globals.getJavaFXFont( T.minY*getScale(sceneHeight,sceneWidth)*size);
        // ExpressionFieldToDo
        //ExpressionField.setBorder( javax.swing.BorderFactory.createEmptyBorder() );
/*
        ExpressionField.setDisabledTextColor( java.awt.Color.BLACK );
		ExpressionField.addFocusListener
		(
			java.awt.event.FocusListener
			{
				override function focusGained( e )
                {
                    //do nothing
                }
                override function focusLost( e )
                {
                    ExpressionField.requestFocus();
                }

            }
		);
		ExpressionField.requestFocus();
*/
        //caret.setBlinkRate(500);
        //ExpressionField.setCaret(caret);
        //caret.setVisible(true);
        NullField.setFont(f);
        NullField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        SurfaceExpression.layoutX=T.minX*getScale(sceneHeight,sceneWidth);
        SurfaceExpression.layoutY=T.minY*getScale(sceneHeight,sceneWidth);
        EqualNull.layoutX=T2.minX*getScale(sceneHeight,sceneWidth);
        EqualNull.layoutY=T2.minY*getScale(sceneHeight,sceneWidth);
    }
    def ButtonList = ["Cursor_Left","Cursor_Right","Delete", "Complete_Delete","a","b","c","d","x","y","z","Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close","0","1","2","3","4","5","6","7","8","9","Comma","Print", "Open_File", "Save_File", "Preferences", "Export"/*,"Help"/*,"Imprint"*/];
    function setButtons()
    {
        fxdLayoutFile.getNode("Buttons Over").visible=true;
        fxdLayoutFile.getNode("Buttons Pressed").visible=true;
        fxdLayoutFile.getNode("Buttons normal state").visible=true;
	for (s in ButtonList)
	{
           fxdLayoutFile.getNode("Button_{s}").visible=true;
           fxdLayoutFile.getNode("Button_Over_{s}").visible=false;
           fxdLayoutFile.getNode("Button_Pressed_{s}").visible=false;
           fxdLayoutFile.getNode("Button_{s}").onMouseEntered =function(e: javafx.scene.input.MouseEvent): Void
           {
               if(not enabled)return;
               if (e.primaryButtonDown) {Press(s);}else {MouseOver(s);}
               inside=true;
               for (t in ButtonList)
                  if (t!=s)Standard(t);
           };
           fxdLayoutFile.getNode("Button_{s}").onMouseExited  =function(e: javafx.scene.input.MouseEvent): Void {Standard(s); inside=false;};
           fxdLayoutFile.getNode("Button_{s}").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {if(not enabled)return;Press(s); setChar(s);};
           fxdLayoutFile.getNode("Button_{s}").onMouseClicked=function(e: javafx.scene.input.MouseEvent): Void
           {
               if(not enabled)return;
               if (e.button == javafx.scene.input.MouseButton.PRIMARY){if(e.primaryButtonDown){Press(s);}else {Release(s);}}
           };
           fxdLayoutFile.getNode("Button_Over_{s}").onMouseEntered=function(e: javafx.scene.input.MouseEvent): Void
           {
               if (e.primaryButtonDown) {Press(s);}else {MouseOver(s);}
               inside=true;
               for (t in ButtonList)
                  if (t!=s)Standard(t);
           };
           fxdLayoutFile.getNode("Button_Over_{s}").onMouseExited=function(e: javafx.scene.input.MouseEvent): Void {Standard(s); inside=false;};
           fxdLayoutFile.getNode("Button_Over_{s}").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {Press(s);setChar(s); };
           fxdLayoutFile.getNode("Button_Over_{s}").onMouseClicked=function(e: javafx.scene.input.MouseEvent): Void
           {
               if (e.button== javafx.scene.input.MouseButton.PRIMARY){ if(e.primaryButtonDown){Press(s);}else {Release(s);}}
           };
           fxdLayoutFile.getNode("Button_Pressed_{s}").onMouseEntered=function(e: javafx.scene.input.MouseEvent): Void
           {
               if (e.primaryButtonDown) {Press(s);}else {MouseOver(s);}
               inside=true;
               for (t in ButtonList)
                  if (t!=s)Standard(t);
           };
           fxdLayoutFile.getNode("Button_Pressed_{s}").onMouseExited=function(e: javafx.scene.input.MouseEvent): Void {Standard(s); inside=false;};
           fxdLayoutFile.getNode("Button_Pressed_{s}").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {Press(s); setChar(s);};
           fxdLayoutFile.getNode("Button_Pressed_{s}").onMouseClicked=function(e: javafx.scene.input.MouseEvent): Void
           {
               if (e.button== javafx.scene.input.MouseButton.PRIMARY){if(e.primaryButtonDown){Press(s);}else {Release(s);}}
           };


	}
        /*
        //"Button_Complete_Delete"
        fxdLayoutFile.getNode("Button_Complete_Delete").onMousePressed=function(e:javafx.scene.input.MouseEvent):Void
        {
            CompleteDelete();
        }
        fxdLayoutFile.getNode("Button_Over_Complete_Delete").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_Complete_Delete").visible=false;
*/

        //"Button_Over_draw"
        fxdLayoutFile.getNode("Button_Over_Wrong").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_Wrong").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_draw").visible=false;
        fxdLayoutFile.getNode("Button_Over_draw").visible=false;
        //language
        fxdLayoutFile.getNode("Button_Pressed_German").visible=false;
        fxdLayoutFile.getNode("Button_Over_German").visible=false;
        fxdLayoutFile.getNode("Button_German").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_English").visible=false;
        fxdLayoutFile.getNode("Button_Over_English").visible=false;
        fxdLayoutFile.getNode("Button_English").visible=true;
        fxdLayoutFile.getNode("Button_German").onMousePressed=function(e:javafx.scene.input.MouseEvent):Void
        {
            //fxdLayoutFile.getNode("Button_Pressed_German").visible=false;
            fxdLayoutFile.getNode("Button_German").visible=false;
            //fxdLayoutFile.getNode("Button_Pressed_English").visible=false;
            fxdLayoutFile.getNode("Button_English").visible=true;
            language=java.util.Locale.GERMAN;
            System.out.println("German pressed");
        }
        fxdLayoutFile.getNode("Button_English").onMousePressed=function(e:javafx.scene.input.MouseEvent):Void
        {
            //fxdLayoutFile.getNode("Button_Pressed_German").visible=false;
            fxdLayoutFile.getNode("Button_German").visible=true;
            //fxdLayoutFile.getNode("Button_Pressed_English").visible=false;
            fxdLayoutFile.getNode("Button_English").visible=false;
            language=java.util.Locale.ENGLISH;
            System.out.println("English pressed");
        }
        //"Imprint"
        fxdLayoutFile.getNode("Button_Imprint").onMousePressed=function(e:javafx.scene.input.MouseEvent):Void
        {
            //timeline.playFromStart();
            showImpressum();
        }

        
        if (true) //disable prefs button
        {
           fxdLayoutFile.getNode("Button_Preferences").visible=false;
           fxdLayoutFile.getNode("Button_Pressed_Preferences").visible=false;
           fxdLayoutFile.getNode("Button_Over_Preferences").visible=false;
        }

        if (not showPrint)
        {
           fxdLayoutFile.getNode("Button_Print").visible=false;
           fxdLayoutFile.getNode("Button_Pressed_Print").visible=false;
           fxdLayoutFile.getNode("Button_Over_Print").visible=false;
        }

        if (not showLoadSave )
        {
           fxdLayoutFile.getNode("Button_Open_File").visible=false;
           fxdLayoutFile.getNode("Button_Pressed_Open_File").visible=false;
           fxdLayoutFile.getNode("Button_Over_Open_File").visible=false;
           fxdLayoutFile.getNode("Button_Save_File").visible=false;
           fxdLayoutFile.getNode("Button_Pressed_Save_File").visible=false;
           fxdLayoutFile.getNode("Button_Over_Save_File").visible=false;
        }

        if (not showExport)
        {
           fxdLayoutFile.getNode("Button_Export").visible=false;
           fxdLayoutFile.getNode("Button_Pressed_Export").visible=false;
           fxdLayoutFile.getNode("Button_Over_Export").visible=false;
        }

    }
    function setPopUp()
    {

        def deltaX:Number=fxdLayoutFile.getNode("Button_Language").boundsInLocal.maxX-fxdLayoutFile.getNode("Button_Foldout_Lower").boundsInLocal.maxX;
        def deltaY:Number=fxdLayoutFile.getNode("Button_Language").boundsInLocal.minY-fxdLayoutFile.getNode("Button_Foldout_Lower").boundsInLocal.maxY;
        //def offset1:Number=fxdLayoutFile.getNode("Button_Foldout_Lower").boundsInLocal.height;
        def offset:Number=fxdLayoutFile.getNode("Button_Foldout_Middle").boundsInLocal.height;
        //def lb=fxdLayoutFile.getNode("Button_Language");
        def lang1 = javafx.fxd.Duplicator.duplicate(fxdLayoutFile.getNode("Button_Foldout_Lower"))as javafx.scene.Node;
        def lang2 = javafx.fxd.Duplicator.duplicate(fxdLayoutFile.getNode("Button_Foldout_Middle"))as javafx.scene.Node;
        def lang3 = javafx.fxd.Duplicator.duplicate(fxdLayoutFile.getNode("Button_Foldout_Middle"))as javafx.scene.Node;
        def lang4 = javafx.fxd.Duplicator.duplicate(fxdLayoutFile.getNode("Button_Foldout_Middle"))as javafx.scene.Node;
        def lang5 = javafx.fxd.Duplicator.duplicate(fxdLayoutFile.getNode("Button_Foldout_Middle"))as javafx.scene.Node;
        def lang6 = javafx.fxd.Duplicator.duplicate(fxdLayoutFile.getNode("Button_Foldout_Upper"))as javafx.scene.Node;
        fxdLayoutFile.getNode("Button_Foldout_Lower").visible=false;
        fxdLayoutFile.getNode("Button_Foldout_Middle").visible=false;
        fxdLayoutFile.getNode("Button_Foldout_Upper").visible=false;
        
        fxdLayoutFile.getNode("Button_Over_Foldout_Lower").visible=false;
        fxdLayoutFile.getNode("Button_Over_Foldout_Middle").visible=false;
        fxdLayoutFile.getNode("Button_Over_Foldout_Upper").visible=false;
        
        fxdLayoutFile.getNode("Button_Pressed_Foldout_Lower").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_Foldout_Middle").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_Foldout_Upper").visible=false;

        lang3.translateY+=-offset-offset;
        lang4.translateY+=-offset;
        lang5.translateY+=-offset-offset-offset;
        lang6.translateY+=-offset-offset-offset;
        lang1.visible=true;lang2.visible=true;lang3.visible=true;lang4.visible=true;lang5.visible=true;lang6.visible=true;
        lang1.onMousePressed=function(e:javafx.scene.input.MouseEvent):Void{language=java.util.Locale.ENGLISH;popUp.visible=false;}
        lang2.onMousePressed=function(e:javafx.scene.input.MouseEvent):Void{language=java.util.Locale.GERMAN;popUp.visible=false;}
        lang3.onMousePressed=function(e:javafx.scene.input.MouseEvent):Void{language=new java.util.Locale("ru");popUp.visible=false;}
        lang4.onMousePressed=function(e:javafx.scene.input.MouseEvent):Void{language=new java.util.Locale("sr");popUp.visible=false;}
        lang5.onMousePressed=function(e:javafx.scene.input.MouseEvent):Void{language=new java.util.Locale("pt");popUp.visible=false;}
        lang6.onMousePressed=function(e:javafx.scene.input.MouseEvent):Void{language=new java.util.Locale("es");popUp.visible=false;}
        var inside:Boolean=false;
        def timeline = javafx.animation.Timeline
        {
            keyFrames: javafx.animation.KeyFrame
            {
                time: 2.0s
                action: function()
                {
                    //System.out.println("Time is up");
                    //timeline.autoReverse=inside;
                    if(not inside) popUp.visible=false;
                }
            }
        }
        
        fxdLayoutFile.getNode("Button_Language").onMouseEntered=function(e:javafx.scene.input.MouseEvent):Void
        {
            popUp.visible=true;
            inside=true;
            //timeline.playFromStart();
            timeline.autoReverse=true;
        }
        fxdLayoutFile.getNode("Button_Language").onMouseMoved=function(e:javafx.scene.input.MouseEvent):Void
        {
            popUp.visible=true;
            inside=true;
            //timeline.playFromStart();
            timeline.autoReverse=true;
        }
        fxdLayoutFile.getNode("Button_Language").onMouseExited=function(e:javafx.scene.input.MouseEvent):Void
        {
            //popUp.visible=true;
            timeline.playFromStart();
            timeline.autoReverse=false;
            inside=false;
        }
        
        popUp=javafx.scene.Group
        {
            blocksMouse: true
            content: 
            [
                lang1,lang2,lang3,lang4,lang5,lang6,
                javafx.scene.text.Text
                {
                    x:lang1.boundsInLocal.minX+0.1*lang1.boundsInLocal.width
                    y:lang1.boundsInLocal.minY+0.7*lang1.boundsInLocal.height
                    font: javafx.scene.text.Font { size: lang1.boundsInLocal.height * 0.8 }
                    content: "en"
                }
                javafx.scene.text.Text
                {
                    x:lang2.boundsInLocal.minX+0.1*lang2.boundsInLocal.width
                    y:lang2.boundsInLocal.minY+0.7*lang2.boundsInLocal.height
                    font: javafx.scene.text.Font { size: lang2.boundsInLocal.height * 0.8 }
                    content: "de"
                }
                javafx.scene.text.Text
                {
                    x:lang3.boundsInLocal.minX+0.1*lang3.boundsInLocal.width
                    y:lang3.boundsInLocal.minY+0.7*lang3.boundsInLocal.height-2*offset
                    font: javafx.scene.text.Font { size: lang3.boundsInLocal.height * 0.8 }
                    content: "ru"
                }
                javafx.scene.text.Text
                {
                    x:lang4.boundsInLocal.minX+0.1*lang4.boundsInLocal.width
                    y:lang4.boundsInLocal.minY+0.7*lang4.boundsInLocal.height-offset
                    font: javafx.scene.text.Font { size: lang4.boundsInLocal.height * 0.8 }
                    content: "sr"
                }
                javafx.scene.text.Text
                {
                    x:lang5.boundsInLocal.minX+0.1*lang5.boundsInLocal.width
                    y:lang5.boundsInLocal.minY+0.8*lang5.boundsInLocal.height-3*offset
                    font: javafx.scene.text.Font { size: lang5.boundsInLocal.height * 0.8 }
                    content: "pt"
                }
                javafx.scene.text.Text
                {
                    x:lang6.boundsInLocal.minX+0.1*lang6.boundsInLocal.width
                    y:lang6.boundsInLocal.minY+0.8*lang6.boundsInLocal.height-3*offset
                    font: javafx.scene.text.Font { size: lang6.boundsInLocal.height * 0.8 }
                    content: "es"
                }
            ]
            translateX: deltaX;
            translateY: deltaY;
            onMouseMoved:function(e:javafx.scene.input.MouseEvent):Void{inside=true;timeline.autoReverse=true;}
            onMouseEntered:function(e:javafx.scene.input.MouseEvent):Void{inside=true;timeline.autoReverse=true;}
            onMouseExited:function(e:javafx.scene.input.MouseEvent):Void{inside=false;timeline.autoReverse=false;timeline.playFromStart();}
        }
        popUp.visible=false;
        languageText=javafx.scene.Group
        {
            content:
            [
                javafx.scene.text.Text
                {
                    //x:0.2*fxdLayoutFile.getNode("Button_Language").boundsInLocal.width
                    y:0.7*fxdLayoutFile.getNode("Button_Language").boundsInLocal.height
                    font: javafx.scene.text.Font { size: fxdLayoutFile.getNode("Button_Language").boundsInLocal.height * 0.8 }
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    content: bind language.getLanguage()
                }
            ]
            //translateX: bind fxdLayoutFile.getNode("Button_Language").boundsInLocal.minX+fxdLayoutFile.getNode("Button_Language").boundsInLocal.width/2-languageText.boundsInLocal.width/2;
            translateX: bind fxdLayoutFile.getNode("Button_Language").boundsInLocal.minX+fxdLayoutFile.getNode("Button_Language").translateX+fxdLayoutFile.getNode("Button_Language").boundsInLocal.width/2-languageText.boundsInLocal.width/2.5
            translateY: fxdLayoutFile.getNode("Button_Language").boundsInLocal.minY;
        }
    }
    
    function setTextField2()
    {
        keyboardTextParameters=javafx.scene.Group
        {
            content:
            [
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.height)
                    content: "Parameter"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.maxY
                    visible: bind (language==java.util.Locale.GERMAN)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.height)
                    content: "Parameter"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.maxY
                    visible: bind (language==java.util.Locale.ENGLISH)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.height)
                    content: "Параметр"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("ru"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.height)
                    content: "Parâmetro"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("pt"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.height)
                    content: "Параметри"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("sr"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.height)
                    content: "Parámetros"
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("es"))
                }
            ]
        }

        fxdLayoutFile.getNode("Text_Keyboard_Parameters").visible=false;

        keyboardTextOperations=javafx.scene.Group
        {
            content:
            [
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.height)
                    content: "Rechenoperationen" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.maxY
                    visible: bind (language==java.util.Locale.GERMAN)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.height)
                    content: "Arithmetic operations" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.maxY
                    visible: bind (language==java.util.Locale.ENGLISH)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.height)
                    content: "Арифметические операции" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("ru"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.height)
                    content: "Operações aritméticas" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("pt"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.height)
                    content: "Аритметичке операције" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("sr"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.height)
                    content: "Operadores Aritméticos" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("es"))
                }
            ]
        }
        
        fxdLayoutFile.getNode("Text_Keyboard_Operations").visible=false;

        keyboardTextXYZ=javafx.scene.Group
        {
            content:
            [
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.height)
                    content: "Variablen" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.maxY
                    visible: bind (language==java.util.Locale.GERMAN)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.height)
                    content: "Variables" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.maxY
                    visible: bind (language==java.util.Locale.ENGLISH)
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.height)
                    content: "Переменные" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("ru"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.height)
                    content: "Variáveis" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("pt"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.height)
                    content: "Променљиве" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("sr"))
                }
                javafx.scene.text.Text
                {
                    font: javafx.scene.text.Font.font ("Arial", fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.height)
                    content: "Variables" textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.maxY
                    visible: bind (language==new java.util.Locale("es"))
                }
            ]
        }

        fxdLayoutFile.getNode("Text_Keyboard_XYZ").visible=false;
    }
    public function forward()
    {
        ExpressionField.forward();
    }

    public function back()
    {
        ExpressionField.backward();
    }

    public function backspace()
    {
        ExpressionField.deletePreviousChar();
    }

    public function CompleteDelete()
    {
        ExpressionField.clear();
    }

    public function insertStringInTextfield(s:String)
    {
        ExpressionField.replaceSelection( s );
    }

    public function setChar(c:String )
    {
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
        //else if (c=="Wrong"){/*ToDo NahrichtenFensten ausgeben*/}
        else if (c=="Help"){/*ToDo Help*/}
        else if (c=="Print"){if(showPrint) gui.print();}
        else if (c=="Open_File"){if(showLoadSave) gui.load();}
        else if (c=="Save_File"){if(showLoadSave) gui.save();}
        else if (c=="Export"){if(showExport) gui.export();}
        //else if (c=="Imprint"){timeline.playFromStart();}
    }

    function Press(s: String)
    {
        fxdLayoutFile.getNode("Button_{s}").visible=false;
        fxdLayoutFile.getNode("Button_Over_{s}").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_{s}").visible=true;
        pressedButton=s;
    }

    function Release(s: String)
    {
        if(inside) {MouseOver(s);}else {Standard(s);}
        pressedButton="";
    }

    function MouseOver(s: String)
    {
        if (s!=pressedButton)
        {
            fxdLayoutFile.getNode("Button_{s}").visible=false;
            fxdLayoutFile.getNode("Button_Over_{s}").visible=true;
            fxdLayoutFile.getNode("Button_Pressed_{s}").visible=false;/**/
        }
    }

    function Standard(s: String)
    {
        if (s=="Print" and not showPrint) return;
        if (s=="Open_File" and not showLoadSave) return;
        if (s=="Save_File" and not showLoadSave) return;
        if (s=="Preferences" ) return;
        if (s=="Export" and not showExport) return;
        fxdLayoutFile.getNode("Button_{s}").visible=true;
        fxdLayoutFile.getNode("Button_Over_{s}").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_{s}").visible=false;
        if (pressedButton==s){pressedButton="";}
    }

    var enabled:Boolean=true;
    public function setIdle()
    {
        ExpressionField.disable=true;
        //NullField.setEnabled(false);
        EqualNull.effect=javafx.scene.effect.GaussianBlur{};
        SurfaceExpression.effect=javafx.scene.effect.GaussianBlur{};
        fxdLayoutFile.getNode("Button_Correct").effect=javafx.scene.effect.GaussianBlur{};
        fxdLayoutFile.getNode("Button_Wrong").effect=javafx.scene.effect.GaussianBlur{};
        fxdLayoutFile.getNode("Formula_Box").effect=javafx.scene.effect.GaussianBlur{};

        keyboardTextParameters.effect=javafx.scene.effect.GaussianBlur{};
        keyboardTextOperations.effect=javafx.scene.effect.GaussianBlur{};
        keyboardTextXYZ.effect=javafx.scene.effect.GaussianBlur{};

        //keyboardTextParametersGer.effect=javafx.scene.effect.GaussianBlur{};
        //keyboardTextOperationsGer.effect=javafx.scene.effect.GaussianBlur{};
        //keyboardTextXYZGer.effect=javafx.scene.effect.GaussianBlur{};

        for (s in ButtonList)
        {
            fxdLayoutFile.getNode("Button_{s}").effect=javafx.scene.effect.GaussianBlur{};
            fxdLayoutFile.getNode("Button_Over_{s}").effect=javafx.scene.effect.GaussianBlur{};
            fxdLayoutFile.getNode("Button_Pressed_{s}").effect=javafx.scene.effect.GaussianBlur{};
        }
        enabled=false;
        //caret.setBlinkRate(0);
        //ExpressionField.setCaret(caret);

    }
    public function setBusy()
    {
        ExpressionField.disable=false;
        //NullField.setEnabled(true);
        EqualNull.effect=null;
        SurfaceExpression.effect=null;
        fxdLayoutFile.getNode("Button_Correct").effect=null;
        fxdLayoutFile.getNode("Button_Wrong").effect=null;
        fxdLayoutFile.getNode("Formula_Box").effect=null;

        keyboardTextParameters.effect=null;
        keyboardTextOperations.effect=null;
        keyboardTextXYZ.effect=null;

        //keyboardTextParametersGer.effect=null;
        //keyboardTextOperationsGer.effect=null;
        //keyboardTextXYZGer.effect=null;

        for (s in ButtonList)
        {
            fxdLayoutFile.getNode("Button_{s}").effect=null;
            fxdLayoutFile.getNode("Button_Over_{s}").effect=null;
            fxdLayoutFile.getNode("Button_Pressed_{s}").effect=null;
        }
        enabled=true;
        //caret=javax.swing.text.DefaultCaret{ override function setVisible(b:Boolean){super.setVisible(true);}} ;
        //caret.setBlinkRate(500);
        //ExpressionField.setCaret(caret);
    }


}
