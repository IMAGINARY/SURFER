/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfxsurfer;

//import de.mfo.jsurfer.gui.AlgebraicExpressionButtonPanel;
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

def GUI: de.mfo.jsurfer.gui.FXGUI = de.mfo.jsurfer.gui.FXGUI{
                x: 0, y:0, width:bind scene.width, height:bind scene.height
                onKeyReleased:toggleFullscreenKey
            }


var scene: Scene;
def stage: Stage =Stage{
    title: "First JavaFX Application"
    fullScreen: false
    scene: scene = Scene {
        width: 192*4
        height: 108*4
        content: [
                    GUI
                    //TestCode.testGallery()
        ]
    }

};
GUI.requestFocus();




