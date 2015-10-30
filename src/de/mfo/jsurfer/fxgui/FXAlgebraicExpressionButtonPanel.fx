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
import java.util.ArrayList;
import java.util.ResourceBundle;
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
    public-init var showLoad:Boolean;
    public-init var showSave:Boolean;
    public-init var showExport:Boolean;
    public-init var clickMode:Integer;
    public-init var gui:FXGUI;
    var knownLangs_ISO2 = getAvailableLocaleNames();

    function getAvailableLocaleNames() : String[]
    {
        var t = System.currentTimeMillis();
        var locales = Locale.getAvailableLocales()[ l | localeAvailable( l ) ];
        var localeNames : String[];
        for( l in locales )
            insert l.toString() into localeNames;
        localeNames = javafx.util.Sequences.sort( localeNames ) as String[];
        System.out.println( "{ System.currentTimeMillis() - t }ms for detecting the following locales:" );
        System.out.println( localeNames );

        return localeNames;
    }

    function localeAvailable( l : Locale ) : Boolean
    {
        try
        {
            return ResourceBundle.getBundle( "de.mfo.jsurfer.fxgui.MessagesBundle", l ).getLocale().equals( l );
        }
        catch( mre : java.util.MissingResourceException )
        {
            return false;
        }
    }

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

    public var language:java.util.Locale;
    var messages:java.util.ResourceBundle=bind ResourceBundle.getBundle( "de.mfo.jsurfer.fxgui.MessagesBundle", language );
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
        setPopUp();
        setButtons();
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

        if (not showLoad )
        {
           fxdLayoutFile.getNode("Button_Open_File").visible=false;
           fxdLayoutFile.getNode("Button_Pressed_Open_File").visible=false;
           fxdLayoutFile.getNode("Button_Over_Open_File").visible=false;
        }

        if (not showSave )
        {
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
        var languagesTmp : String[] = de.mfo.jsurfer.gui.Options.languages.toArray() as String[]; // languages IDs are already trimmed
        var starLanguages : String[] = knownLangs_ISO2[ l | javafx.util.Sequences.indexOf( languagesTmp, l ) == -1 ];
        var languageList : java.util.LinkedList = new java.util.LinkedList();
        for( l in languagesTmp )
        {
            if( l == "*")
            {
                for( sl in starLanguages )
                    languageList.add( sl );
            }
            else
            {
                languageList.add( l );
            }
        }
        var languages : String[] = languageList.toArray() as String[];
        languages = languages[ l | javafx.util.Sequences.indexOf( knownLangs_ISO2, l ) != -1 ];

        if( languages.size() > 0 )
            knownLangs_ISO2 = languages;

        if( languagesTmp.size() == 1 and languagesTmp[ 0 ].trim() == "*" )
        {
            language = getDefaultLocale();
        }
        else
        {
            language = new java.util.Locale( knownLangs_ISO2[ 0 ] ); // use first language in list
        }

        var langButtonList : Node[];
        if( knownLangs_ISO2.size() > 0 )
        {
            insert FXButton {
                normal: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Foldout_Upper"))
                hovered: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Over_Foldout_Upper"))
                armed: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Pressed_Foldout_Upper"))
                clickMode: clickMode
            } into langButtonList;
        }
        if( knownLangs_ISO2.size() > 1 )
        {
            insert FXButton {
                normal: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Foldout_Lower"))
                hovered: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Over_Foldout_Lower"))
                armed: Duplicator.duplicate(fxdLayoutFile.getNode("Button_Pressed_Foldout_Lower"))
                clickMode: clickMode
            } into langButtonList;
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

        popUp = VBox { blocksMouse: true; }
        var langButtonListWithText : Node[] = [];
        for( i in [0..langButtonList.size()-1] )
        {
            // add language code
            var code = knownLangs_ISO2[ i ];
            var messageBundleForCode = java.util.ResourceBundle.getBundle( "de.mfo.jsurfer.fxgui.MessagesBundle", new Locale( code ) );
            var t = Text { content: messageBundleForCode.getString( "language" ) font: javafx.scene.text.Font.font( "Arial", 24 ) };
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
                javafx.scene.shape.SVGPath {
                    content: "M654 458q-1 -3 -12.5 0.5t-31.5 11.5l-20 9q-44 20 -87 49q-7 5 -41 31.5t-38 28.5q-67 -103 -134 -181q-81 -95 -105 -110q-4 -2 -19.5 -4t-18.5 0q6 4 82 92q21 24 85.5 115t78.5 118q17 30 51 98.5t36 77.5q-8 1 -110 -33q-8 -2 -27.5 -7.5t-34.5 -9.5t-17 -5 q-2 -2 -2 -10.5t-1 -9.5q-5 -10 -31 -15q-23 -7 -47 0q-18 4 -28 21q-4 6 -5 23q6 2 24.5 5t29.5 6q58 16 105 32q100 35 102 35q10 2 43 19.5t44 21.5q9 3 21.5 8t14.5 5.5t6 -0.5q2 -12 -1 -33q0 -2 -12.5 -27t-26.5 -53.5t-17 -33.5q-25 -50 -77 -131l64 -28 q12 -6 74.5 -32t67.5 -28q4 -1 10.5 -25.5t4.5 -30.5zM449 944q3 -15 -4 -28q-12 -23 -50 -38q-30 -12 -60 -12q-26 3 -49 26q-14 15 -18 41l1 3q3 -3 19.5 -5t26.5 0t58 16q36 12 55 14q17 0 21 -17zM1147 815l63 -227l-139 42zM39 15l694 232v1032l-694 -233v-1031z M1280 332l102 -31l-181 657l-100 31l-216 -536l102 -31l45 110l211 -65zM777 1294l573 -184v380zM1088 -29l158 -13l-54 -160l-40 66q-130 -83 -276 -108q-58 -12 -91 -12h-84q-79 0 -199.5 39t-183.5 85q-8 7 -8 16q0 8 5 13.5t13 5.5q4 0 18 -7.5t30.5 -16.5t20.5 -11 q73 -37 159.5 -61.5t157.5 -24.5q95 0 167 14.5t157 50.5q15 7 30.5 15.5t34 19t28.5 16.5zM1536 1050v-1079l-774 246q-14 -6 -375 -127.5t-368 -121.5q-13 0 -18 13q0 1 -1 3v1078q3 9 4 10q5 6 20 11q106 35 149 50v384l558 -198q2 0 160.5 55t316 108.5t161.5 53.5 q20 0 20 -21v-418z",
                    scaleX: 0.020,
                    scaleY: -0.020,
                    translateX: -755,
                    translateY: -614
                }
            ]
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
                    content: bind messages.getString( "parameters" )
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Parameters").boundsInLocal.maxY
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
                    content: bind messages.getString( "arithmeticOperations" )
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_Operations").boundsInLocal.maxY
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
                    content: bind messages.getString( "variables" )
                    textAlignment:javafx.scene.text.TextAlignment.CENTER
                    translateX: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.minX
                    translateY: fxdLayoutFile.getNode("Text_Keyboard_XYZ").boundsInLocal.maxY
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
        else if (c=="Open_File"){if(showLoad) gui.load();}
        else if (c=="Save_File"){if(showSave) gui.save();}
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
        if (s=="Open_File" and not showLoad) return;
        if (s=="Save_File" and not showSave) return;
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


};
