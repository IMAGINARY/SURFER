/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfxsurfer;

import de.mfo.jsurfer.gui.AlgebraicExpressionButtonPanel;
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


def AP: AlgebraicExpressionButtonPanel = AlgebraicExpressionButtonPanel{
                x: 0, y:0, width:bind scene.width, height:bind scene.height, expression: "Text"
                onKeyReleased:toggleFullscreenKey
            }


var scene: Scene;
def stage: Stage =Stage{
    title: "First JavaFX Application"
    fullScreen: false //true
    scene: scene = Scene {
        width: 200
        height: 200
        content: [
                    AP
        ]
    }

};
AP.requestFocus();




