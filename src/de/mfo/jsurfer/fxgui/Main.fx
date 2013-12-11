/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.fxgui;

import de.mfo.jsurfer.gui.*;
//import de.mfo.jsurfer.gui.FXImpressum;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
//import java.lang.System;

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
/*var timeline = javafx.animation.Timeline
{
    keyFrames: javafx.animation.KeyFrame {
    time: 100ms
    action: function() {GUI.showImpressum=false}
    }
}*/

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

def hideCursor = Options.hideCursor;
function somethingHappend():Void
{
    //System.out.println("somethingHappend");
    timeline.playFromStart();
    if( hideCursor )
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

def stage: Stage =Stage{
    title: "SURFER"
    fullScreen: Options.fullscreen
    scene: scene = Scene {
        width: 192*6
        height: 108*6
        stylesheets: "{__DIR__}jfxsurfer.css";
        content: [
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
GUI.requestFocus();




