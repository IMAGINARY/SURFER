/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.fxgui;


import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import java.lang.System;
import java.lang.String;
import java.util.Locale;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.scene.layout.*;
import javafx.fxd.Duplicator;

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
    public-init var clickMode:Integer;
    public-init var gui:FXGUI;
    var knownLangs_ISO2 = [ "es", "de", "en", "pt", "ru", "sr" ];

//public-init var keyboardTextParametersEng:javafx.scene.text.Text;
    //public-init var keyboardTextOperationsEng:javafx.scene.text.Text;
    //public-init var keyboardTextXYZEng:javafx.scene.text.Text;

    public-init var keyboardTextParameters:javafx.scene.Group;
    public-init var keyboardTextOperations:javafx.scene.Group;
    public-init var keyboardTextXYZ:javafx.scene.Group;
    public-init var buttons:javafx.scene.Group;

    //public-init var keyboardTextParametersGer:javafx.scene.text.Text;
    //public-init var keyboardTextOperationsGer:javafx.scene.text.Text;
    //public-init var keyboardTextXYZGer:javafx.scene.text.Text;

    public function getDefaultLocale():java.util.Locale
    {
        var defaultLang_ISO2:String = java.util.Locale.getDefault().getLanguage();
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
    public var popUp:javafx.scene.Node;
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
        def sw2=JavaFXExtWrapper.wrap(NullField);
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
        var default = function( s : String )
        {
            fxdLayoutFile.getNode("Button_{s}").opacity = 1;
            fxdLayoutFile.getNode("Button_Over_{s}").opacity=0;
            fxdLayoutFile.getNode("Button_Pressed_{s}").opacity=0;
        }
        var hover = function( s : String )
        {
            fxdLayoutFile.getNode("Button_{s}").opacity = 0;
            fxdLayoutFile.getNode("Button_Over_{s}").opacity=1;
            fxdLayoutFile.getNode("Button_Pressed_{s}").opacity=0;
        }
        var armed = function( s : String )
        {
            fxdLayoutFile.getNode("Button_{s}").opacity = 0;
            fxdLayoutFile.getNode("Button_Over_{s}").opacity=0;
            fxdLayoutFile.getNode("Button_Pressed_{s}").opacity=1;
        }
	for (s in ButtonList)
	{
           fxdLayoutFile.getNode("Button_{s}").visible=true;
           fxdLayoutFile.getNode("Button_Over_{s}").visible=true;
           fxdLayoutFile.getNode("Button_Over_{s}").opacity=0;
           fxdLayoutFile.getNode("Button_Pressed_{s}").visible=true;
           fxdLayoutFile.getNode("Button_Pressed_{s}").opacity = 0;
           fxdLayoutFile.getNode("Button_{s}").onMouseEntered = function(e: javafx.scene.input.MouseEvent): Void
           {
                if(not enabled)return;
                hover( s );
           };
           fxdLayoutFile.getNode("Button_{s}").onMouseExited = function(e: javafx.scene.input.MouseEvent): Void { default( s ); };
           fxdLayoutFile.getNode("Button_{s}").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void 
           {
               armed( s );
               if( clickMode == 1 )
                    setChar( s );
           };
           fxdLayoutFile.getNode("Button_{s}").onMouseReleased=function(e: javafx.scene.input.MouseEvent): Void
           {
               if( fxdLayoutFile.getNode("Button_{s}").hover )
                    hover( s )
               else
                    default( s );
               if( clickMode == 2 )
                    setChar( s );
           };
           fxdLayoutFile.getNode("Button_{s}").onMouseClicked=function(e: javafx.scene.input.MouseEvent): Void
           {
               if( clickMode == 0 )
                    setChar( s );
           };
	}

        //"Button_Over_draw"
        fxdLayoutFile.getNode("Button_Over_Wrong").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_Wrong").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_draw").visible=false;
        fxdLayoutFile.getNode("Button_Over_draw").visible=false;
   
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
        var langButtonList : Node[] = [
            FXButton {
                normal: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Foldout_Upper"))
                hovered: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Over_Foldout_Upper"))
                armed: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Pressed_Foldout_Upper"))
                clickMode: clickMode
            }
            FXButton {
                normal: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Foldout_Lower"))
                hovered: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Over_Foldout_Lower"))
                armed: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Pressed_Foldout_Lower"))
                clickMode: clickMode
            }
        ];

        var languagesString : java.lang.String = FXOptions.getOption( "de.mfo.jsurfer.gui.languages" );
        if( languagesString != null )
        {
            var languages : String[] = languagesString.replaceAll( " ", "" ).split( "," );
            languages = languages[ l | javafx.util.Sequences.indexOf( knownLangs_ISO2, l ) != -1 ];
            if( languages.size() > 0 )
            {
                knownLangs_ISO2 = languages;
                language = new java.util.Locale( knownLangs_ISO2[ 0 ] ); // use first language in list
            }
        };

        while( langButtonList.size() < knownLangs_ISO2.size() )
        {
            insert
            FXButton {
                normal: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Foldout_Middle"))
                hovered: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Over_Foldout_Middle"))
                armed: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Pressed_Foldout_Middle"))
                clickMode: clickMode;
            }
            after langButtonList[ 0 ];
        }
                
        var font : Font = Globals.getJavaFXFont( 30 );

        popUp = VBox { blocksMouse: true; }
        var langButtonListWithText : Node[] = [];
        for( i in [0..langButtonList.size()-1] )
        {
            // add language code
            var code = knownLangs_ISO2[ i ];
            var t = Text { content: code font: font };
            var b : FXButton = langButtonList[ i ] as FXButton;
            b.action = function() { language=new java.util.Locale( code );popUp.visible=false; }
            insert Stack { content: [ b, t ] } into langButtonListWithText;
        };
        ( popUp as VBox ).content = langButtonListWithText;

        fxdLayoutFile.getNode("Button_Foldout_Lower").visible=false;
        fxdLayoutFile.getNode("Button_Foldout_Middle").visible=false;
        fxdLayoutFile.getNode("Button_Foldout_Upper").visible=false;
        
        fxdLayoutFile.getNode("Button_Over_Foldout_Lower").visible=false;
        fxdLayoutFile.getNode("Button_Over_Foldout_Middle").visible=false;
        fxdLayoutFile.getNode("Button_Over_Foldout_Upper").visible=false;
        
        fxdLayoutFile.getNode("Button_Pressed_Foldout_Lower").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_Foldout_Middle").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_Foldout_Upper").visible=false;

        def timeline = javafx.animation.Timeline
        {
            keyFrames: javafx.animation.KeyFrame
            {
                time: 2.0s
                action: function() { if( not popUp.hover ) popUp.visible = false; }
            }
        }
        popUp.onMouseMoved = function( me: javafx.scene.input.MouseEvent ) { timeline.playFromStart(); };
        popUp.visible=false;
        popUp.translateX = fxdLayoutFile.getNode("Button_Language").boundsInLocal.maxX-fxdLayoutFile.getNode("Button_Pressed_Foldout_Lower").boundsInLocal.width;
        popUp.translateY = fxdLayoutFile.getNode("Button_Language").boundsInLocal.minY-fxdLayoutFile.getNode("Button_Pressed_Foldout_Lower").boundsInLocal.height*langButtonListWithText.size();

        fxdLayoutFile.getNode("Button_Language").onMouseEntered=function(e:javafx.scene.input.MouseEvent):Void
        {
            popUp.visible=true;
            timeline.playFromStart();
        }
        fxdLayoutFile.getNode("Button_Language").onMouseMoved=function(e:javafx.scene.input.MouseEvent):Void
        {
            popUp.visible=true;
            timeline.playFromStart();
        }
        fxdLayoutFile.getNode("Button_Language").onMouseExited=function(e:javafx.scene.input.MouseEvent):Void
        {
            //popUp.visible=true;
            timeline.playFromStart();
            timeline.autoReverse=false;
        }

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
