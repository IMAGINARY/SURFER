/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author Panda
 */

public class FXAlgebraicExpressionButtonPanel
{
    var timeline = javafx.animation.Timeline
    {
        keyFrames: javafx.animation.KeyFrame {
        time: 100ms
        action: function() {showImpressum();}
        }
    }
    public var language:java.util.Locale=java.util.Locale.GERMAN;
    public-init var getScale:function (n:Number, w:Number):Number;
    public-init var showImpressum:function ():Void;
    public var sceneWidth:Number;
    public var sceneHeight:Number;
    public-init var fxdLayoutFile:javafx.fxd.FXDNode;
    public var surferPanel:FXSurferPanel;
    public function set()
    {
        setButtons();
        setTextField();
        ExpressionField.getDocument().addDocumentListener
        (
            javax.swing.event.DocumentListener
            {
                override function changedUpdate( e ){surferPanel.surfaceExpressionChanged(ExpressionField.getText());}
                override function insertUpdate( e ){surferPanel.surfaceExpressionChanged(ExpressionField.getText());}
                override function removeUpdate( e ){surferPanel.surfaceExpressionChanged(ExpressionField.getText());}
            }
        );
    }

    public var SurfaceExpression:javafx.scene.layout.HBox= new javafx.scene.layout.HBox();
    public var EqualNull:javafx.scene.layout.HBox= javafx.scene.layout.HBox
    {
        //content: [FXLabel{  string:"=0" Bound:fxdLayoutFile.getNode("Equals_Zero"), getScale: getScale,sceneWidth: bind sceneWidth; sceneHeight:bind sceneHeight; faktor:0.08} ]
    };
    public var expression:String;
    public var ExpressionField: javax.swing.JTextField=new javax.swing.JTextField("x^2+y^2+z^2+2*x*y*z-1");
    public var NullField: javax.swing.JLabel=new javax.swing.JLabel("=0");
    public var pos:Number;
    def caret:javax.swing.text.Caret=javax.swing.text.DefaultCaret{ override function setVisible(b:Boolean){super.setVisible(true);}} ;
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
        def sw:javafx.ext.swing.SwingComponent=javafx.ext.swing.SwingComponent.wrap(ExpressionField);
        sw.layoutInfo=javafx.scene.layout.LayoutInfo
        {
            minWidth: T.width*getScale(sceneHeight,sceneWidth)
            width: T.width*getScale(sceneHeight,sceneWidth)
            maxWidth: T.width*getScale(sceneHeight,sceneWidth)
            minHeight: T.height*getScale(sceneHeight,sceneWidth)
            height: T.height*getScale(sceneHeight,sceneWidth)
            maxHeight: T.height*getScale(sceneHeight,sceneWidth)
         };
        SurfaceExpression.content=[sw];
        EqualNull.content=[sw2];
        var input:java.io.InputStream = getClass().getResourceAsStream("/de/mfo/jsurfer/gui/Nimbus Sans L Regular Surfer.ttf");
        var f:java.awt.Font=java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT , input);
        f=f.deriveFont(T.minY*getScale(sceneHeight,sceneWidth)*size);
        ExpressionField.setFont(f);
        ExpressionField.setBorder( javax.swing.BorderFactory.createEmptyBorder() );
        
        

        caret.setBlinkRate(500);
        ExpressionField.setCaret(caret);
        //ExpressionField.setCaretPosition(1);
        caret.setVisible(true);
        NullField.setFont(f);
        NullField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        SurfaceExpression.layoutX=T.minX*getScale(sceneHeight,sceneWidth);
        SurfaceExpression.layoutY=T.minY*getScale(sceneHeight,sceneWidth);
        EqualNull.layoutX=T2.minX*getScale(sceneHeight,sceneWidth);
        EqualNull.layoutY=T2.minY*getScale(sceneHeight,sceneWidth);
    }
    function setButtons()
    {
	for (s in["Cursor_Left","Cursor_Right","Delete","Complete_Delete","a","b","c","d","x","y","z","Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close","0","1","2","3","4","5","6","7","8","9","Comma","Help","Imprint"])
	{
           fxdLayoutFile.getNode("Button_Over_{s}").visible=false;
           fxdLayoutFile.getNode("Button_Pressed_{s}").visible=false;
           fxdLayoutFile.getNode("Button_{s}").onMouseEntered =function(e: javafx.scene.input.MouseEvent): Void
           {
               if(not enabled)return;
               if (e.primaryButtonDown) {Press(s);}else {MouseOver(s);}
               inside=true;
               for (t in["Cursor_Left","Cursor_Right","Delete","Complete_Delete","a","b","c","d","x","y","z","Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close","0","1","2","3","4","5","6","7","8","9","Comma","Help","Imprint"])
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
               for (t in["Cursor_Left","Cursor_Right","Delete","Complete_Delete","a","b","c","d","x","y","z","Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close","0","1","2","3","4","5","6","7","8","9","Comma","Help","Imprint"])
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
               for (t in["Cursor_Left","Cursor_Right","Delete","Complete_Delete","a","b","c","d","x","y","z","Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close","0","1","2","3","4","5","6","7","8","9","Comma","Help","Imprint"])
                  if (t!=s)Standard(t);
           };
           fxdLayoutFile.getNode("Button_Pressed_{s}").onMouseExited=function(e: javafx.scene.input.MouseEvent): Void {Standard(s); inside=false;};
           fxdLayoutFile.getNode("Button_Pressed_{s}").onMousePressed=function(e: javafx.scene.input.MouseEvent): Void {Press(s); setChar(s);};
           fxdLayoutFile.getNode("Button_Pressed_{s}").onMouseClicked=function(e: javafx.scene.input.MouseEvent): Void
           {
               if (e.button== javafx.scene.input.MouseButton.PRIMARY){if(e.primaryButtonDown){Press(s);}else {Release(s);}}
           };
	}
        //language
        fxdLayoutFile.getNode("Button_Pressed_German").visible=true;
        fxdLayoutFile.getNode("Button_Over_German").visible=false;
        fxdLayoutFile.getNode("Button_Cursor_German").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_English").visible=false;
        fxdLayoutFile.getNode("Button_Over_English").visible=false;
        fxdLayoutFile.getNode("Button_Cursor_English").visible=true;
        fxdLayoutFile.getNode("Button_Cursor_German").onMousePressed=function(e:javafx.scene.input.MouseEvent):Void
        {
            fxdLayoutFile.getNode("Button_Pressed_German").visible=true;
            fxdLayoutFile.getNode("Button_Cursor_German").visible=false;
            fxdLayoutFile.getNode("Button_Pressed_English").visible=false;
            fxdLayoutFile.getNode("Button_Cursor_English").visible=true;
            language=java.util.Locale.GERMAN;
        }
        fxdLayoutFile.getNode("Button_Cursor_English").onMousePressed=function(e:javafx.scene.input.MouseEvent):Void
        {
            fxdLayoutFile.getNode("Button_Pressed_German").visible=false;
            fxdLayoutFile.getNode("Button_Cursor_German").visible=true;
            fxdLayoutFile.getNode("Button_Pressed_English").visible=true;
            fxdLayoutFile.getNode("Button_Cursor_English").visible=false;
            language=java.util.Locale.ENGLISH;
        }


    }

    public function forward()
    {
        pos=ExpressionField.getCaretPosition();
        expression=ExpressionField.getText();
        if (pos<expression.length())pos++;
        ExpressionField.setCaretPosition(pos);
    }

    public function back()
    {
            pos=ExpressionField.getCaretPosition();
            if (pos>0)pos--;
            ExpressionField.setCaretPosition(pos);
    }

    public function backspace()
    {
        pos = ExpressionField.getSelectionStart();
        expression = ExpressionField.getText();
        if( pos == ExpressionField.getSelectionEnd() )
            pos--;// no text is selected; just remove one character
        ExpressionField.setText( "{expression.substring(0,pos)}{expression.substring(ExpressionField.getSelectionEnd())}" );
        ExpressionField.setCaretPosition( pos );
    }

    public function CompleteDelete()
    {
        pos=0;
        ExpressionField.setText("");
        ExpressionField.setCaretPosition(pos);
    }

    public function insertStringInTextfield(c:String)
    {
        pos=ExpressionField.getCaretPosition();
        expression=ExpressionField.getText();
        expression="{expression.substring(0,pos)}{c}{expression.substring(pos)}";
        pos+=c.length();
        ExpressionField.setText(expression);
        ExpressionField.setCaretPosition(pos);
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
        else if (c=="Wrong"){/*ToDo NahrichtenFensten ausgeben*/}
        else if (c=="Help"){/*ToDo Help*/}
        else if (c=="Imprint"){timeline.playFromStart();}
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
        fxdLayoutFile.getNode("Button_{s}").visible=true;
        fxdLayoutFile.getNode("Button_Over_{s}").visible=false;
        fxdLayoutFile.getNode("Button_Pressed_{s}").visible=false;
        if (pressedButton==s){pressedButton="";}
    }
    var enabled:Boolean=true;
    public function setIdle()
    {
        ExpressionField.setEnabled(false);
        NullField.setEnabled(false);
        EqualNull.effect=javafx.scene.effect.GaussianBlur{};
        SurfaceExpression.effect=javafx.scene.effect.GaussianBlur{};
        fxdLayoutFile.getNode("Button_Correct").effect=javafx.scene.effect.GaussianBlur{};
        fxdLayoutFile.getNode("Button_Wrong").effect=javafx.scene.effect.GaussianBlur{};
        for (s in["Cursor_Left","Cursor_Right","Delete","Complete_Delete","a","b","c","d","x","y","z","Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close","0","1","2","3","4","5","6","7","8","9","Comma"])
        {
            fxdLayoutFile.getNode("Button_{s}").effect=javafx.scene.effect.GaussianBlur{};
        }
        enabled=false;
        caret.setBlinkRate(0);

    }
    public function setBusy()
    {
        ExpressionField.setEnabled(true);
        NullField.setEnabled(true);
        EqualNull.effect=null;
        SurfaceExpression.effect=null;
        fxdLayoutFile.getNode("Button_Correct").effect=null;
        fxdLayoutFile.getNode("Button_Wrong").effect=null;
        for (s in["Cursor_Left","Cursor_Right","Delete","Complete_Delete","a","b","c","d","x","y","z","Plus","Minus","Times","Exp_n","Exp_2","Exp_3","Bracket_open","Bracket_close","0","1","2","3","4","5","6","7","8","9","Comma"])
        {
            fxdLayoutFile.getNode("Button_{s}").effect=null;
        }
        enabled=true;
        caret.setBlinkRate(500);
    }


}
