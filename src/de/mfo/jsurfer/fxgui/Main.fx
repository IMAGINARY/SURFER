/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.fxgui;

import de.mfo.jsurfer.gui.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;

var oldStageX = 0.0;
var oldStageY = 0.0;
var oldStageW = 0.0;
var oldStageH = 0.0;

function toggleFullscreenKey(e:KeyEvent):Void
{
    if (e.text=="F")
    {
        if(not stage.fullScreen) {
            oldStageX = stage.x;
            oldStageY = stage.y;
            oldStageW = stage.width;
            oldStageH = stage.height;
        }
        stage.fullScreen = not stage.fullScreen;
        if(not stage.fullScreen) {
            stage.x = oldStageX;
            stage.y = oldStageY;
            stage.width = oldStageW;
            stage.height = oldStageH;
        }
    }

}

javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());

def GUI: de.mfo.jsurfer.fxgui.FXGUI = de.mfo.jsurfer.fxgui.FXGUI{
                x: 0, y:bind GUI.realHeight(1080,1920), width:1920, height:1080
                onKeyReleased:toggleFullscreenKey
                visible:bind not GUI.showImpressum
                showPrint: ( Options.showPrintButton  )
                showLoad: ( Options.showLoadButton  )
                showSave: ( Options.showSaveButton  )
                showExport: ( Options.showExportButton  )
                clickMode:  ( Options.clickMode )
            }

var impressum:de.mfo.jsurfer.fxgui.FXImpressum=de.mfo.jsurfer.fxgui.FXImpressum
{
    width:1920
    height:1080
    translateY:bind GUI.realHeight(1080,1920)
    language: bind GUI.language
    visible: bind GUI.showImpressum
    onMousePressed: function(e: javafx.scene.input.MouseEvent): Void{GUI.showImpressum=false;}
}

var timeline = javafx.animation.Timeline
{
    keyFrames: javafx.animation.KeyFrame
    {
        time: 3.5m
        action: function()
        {
            //System.out.println("Time is up");
            GUI.setScreenSaver();
        }
    }
}


function somethingHappend():Void
{
    //System.out.println("somethingHappend");
    timeline.playFromStart();
    if( Options.hideCursor )
        dummyRect.cursor = javafx.scene.Cursor.NONE;
    //cursorDeactivator.playFromStart();
}

var scene: Scene;
def dummyRect : javafx.scene.shape.Rectangle = javafx.scene.shape.Rectangle
{
    width: bind scene.width, height: bind scene.height
    fill: javafx.scene.paint.Color.rgb(255, 255, 255)
    onKeyPressed:function(ke:javafx.scene.input.KeyEvent):Void{somethingHappend();}
    onKeyReleased:function(ke:javafx.scene.input.KeyEvent):Void{somethingHappend();}
    onKeyTyped:function(ke:javafx.scene.input.KeyEvent):Void{somethingHappend();}
    onMouseClicked:function(me:javafx.scene.input.MouseEvent):Void{somethingHappend();}
    onMouseDragged:function(me:javafx.scene.input.MouseEvent):Void{somethingHappend();}
    onMouseEntered:function(me:javafx.scene.input.MouseEvent):Void{somethingHappend();}
    onMouseExited:function(me:javafx.scene.input.MouseEvent):Void{somethingHappend();}
    onMouseMoved:function(me:javafx.scene.input.MouseEvent):Void{somethingHappend();}
    onMousePressed:function(me:javafx.scene.input.MouseEvent):Void{somethingHappend();}
    onMouseReleased:function(me:javafx.scene.input.MouseEvent):Void{somethingHappend();}
    onMouseWheelMoved:function(me:javafx.scene.input.MouseEvent):Void{somethingHappend();}
};

function calcInitialStageBounds() :  javafx.geometry.Rectangle2D
{
	var screenBounds = javafx.stage.Screen.primary.visualBounds;
	def prefWidth = 192*6;
	def prefHeight = 108*6;
	if( screenBounds.width < prefWidth or screenBounds.height < prefHeight )
	{
		return screenBounds;
	}
	else
	{
		return javafx.geometry.Rectangle2D{
			minX : (screenBounds.minX+screenBounds.maxX)/2 - prefWidth / 2;
			minY : (screenBounds.minY+screenBounds.maxY)/2 - prefHeight / 2;
			width : prefWidth;
			height : prefHeight;
		};
	}
}

def initialStageBounds = calcInitialStageBounds();

def stage=Stage{
    title: bind ( "{Main.class.getPackage().getImplementationTitle()}{if( GUI.showImpressum ) ' {Main.class.getPackage().getImplementationVersion()}' else ''}" )
    fullScreen: Options.fullScreen
    scene: scene = Scene {
        width: initialStageBounds.width;
        height: initialStageBounds.height;
        stylesheets: "{__DIR__}jfxsurfer.css"
        content:
        [
                    dummyRect,
                    javafx.scene.Group
                    {
                        transforms: bind (
                            if( scene.width / scene.height <= 1920.0 / 1080.0 )
                            [
                                javafx.scene.transform.Scale.scale( scene.width / 1920.0, scene.width / 1920.0 ),
                            ]
                            else
                            [
                                javafx.scene.transform.Scale.scale( scene.height / 1080.0, scene.height / 1080.0 )
                            ])
                        content: [
                            javafx.scene.Group
                            {
                                id: "surferSceneGroup"
                                content:
                                [
                                    javafx.scene.Group {
                                        id: "surferGUIGroup"
                                        content: [GUI,impressum]
                                        transforms: bind (
                                            if( scene.width / scene.height <= 1920.0 / 1080.0 )
                                            [
                                                javafx.scene.transform.Translate.translate( 0, ( 1920.0 / scene.width ) * scene.height - 1080 )
                                            ]
                                            else
                                            [
                                                javafx.scene.transform.Translate.translate( 0, 0 )
                                            ])
                                    }
                                ]
                            }
                        ]
                    }
                 ]
    }
};

// process command line arguments
if( FX.getArguments().size() > 0 )
{
    var f = new java.io.File( FX.getArguments()[ 0 ] );
    java.lang.System.out.println( "loading {f}" );
    try
    {
        GUI.loadURL( f.toURI().toURL() );
    }
    catch( e : java.lang.Exception )
    {
        java.lang.System.err.println( "Unable to load {f}" );
        java.lang.System.exit( -1 );
    }
}

GUI.requestFocus();
