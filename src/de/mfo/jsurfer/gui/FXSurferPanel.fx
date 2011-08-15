/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

import javafx.scene.CustomNode;
import de.mfo.jsurfer.rendering.*;
import javax.vecmath.*;
//import java.awt.*;
import javafx.scene.Group;
import javafx.ext.swing.*;
import javafx.scene.Node;
import de.mfo.jsurfer.parser.*;
import de.mfo.jsurfer.algebra.*;
//import java.io.IOException;
import java.lang.System;
import de.mfo.jsurfer.algebra.*;
import javafx.scene.layout.LayoutInfo;
//import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
/**
 * @author Panda
 */

public class FXSurferPanel extends CustomNode {
    /*
    var timeline = Timeline {
    keyFrames: KeyFrame {
        time: 0ms
        action: function() {

            renderer.repaintImage();
        }
    }

}
*/
    public var renderer: JSurferRenderPanel=new JSurferRenderPanel() ;
    /*public var drawCoordinatenSystem:Boolean =true on replace
    {
        System.out.println("drawCoordinatenSystem({drawCoordinatenSystem})");
        renderer.drawCoordinatenSystem(drawCoordinatenSystem);
        renderer.repaintImage();
    };*/

    public var frontColor: Color3f on replace
    {
        var frontMaterial: Material=renderer.getAlgebraicSurfaceRenderer().getFrontMaterial();
        frontMaterial.setColor( new Color3f( frontColor ) );
        renderer.getAlgebraicSurfaceRenderer().setFrontMaterial(frontMaterial );
        renderer.repaintImage();
        //System.out.println("Front has changed");
    };

    public var backColor: Color3f on replace
    {
        var backMaterial: Material=renderer.getAlgebraicSurfaceRenderer().getBackMaterial();
        backMaterial.setColor( new Color3f( backColor ) );
        renderer.getAlgebraicSurfaceRenderer().setBackMaterial(backMaterial );
        renderer.repaintImage();
        //System.out.println("Back has changed");
    };

    public var x: Number;
//setRenderSize
    public var y: Number;
    public var a: Number =0 on replace
    {
        if (a<0)a=0;if (a>1)a=1;        
        renderer.getAlgebraicSurfaceRenderer().setParameterValue("a", a);
        renderer.repaintImage();
    };
    public var b: Number =0  on replace
    {
        if (b<0)b=0;if (b>1)b=1;
        renderer.getAlgebraicSurfaceRenderer().setParameterValue("b", b);
        renderer.repaintImage();
    };
    public var c: Number =0 on replace
    {
        if (c<0)c=0;if (c>1)c=1;
        renderer.getAlgebraicSurfaceRenderer().setParameterValue("c", c);
        renderer.repaintImage();
    };
    public var d: Number =0  on replace
    {
        if (d<0)d=0;if (d>1)d=1;
        renderer.getAlgebraicSurfaceRenderer().setParameterValue("d", d);
        renderer.repaintImage();
    };
    public var usedA:Boolean=false;
    public var usedB:Boolean=false;
    public var usedC:Boolean=false;
    public var usedD:Boolean=false;
    public var correctExpression:Boolean=true;
    public var width: Number/* on replace oldValue
    {
        var d:Dimension=new Dimension(width, height);
        renderer.setRenderSize(d);
        renderer.setSize(d);
    }/**/;
    


    public var height: Number/* on replace oldValue
    {
        var d:Dimension=new Dimension(width, height);
        renderer.setRenderSize(d);
        renderer.setSize(d);
    }/**/;
    public function setScale(n:Number)
    {
        renderer.setScale( n*4-2 );
        scale=renderer.getScale()/4+0.5;
        //System.out.println("SetScale: {scale}, bindinghack.value{bindinghack.value}");
        //renderer.repaintImage();
    }

    public var scale:Number=renderer.getScale()/4+0.5 on replace
    {
        //renderer.setScale( scale*4-2 );
        renderer.repaintImage();
        //timeline.playFromStart();
        //System.out.println("Scale has changed: {scale}");
    };

    function initMaterials()
    {
        // init front material
        var frontMaterial:Material=new Material() ;
        //frontMaterial = new Material();
        frontMaterial.setColor( frontColor );
        frontMaterial.setAmbientIntensity( 0.4 );
        frontMaterial.setDiffuseIntensity( 0.8 );
        frontMaterial.setSpecularIntensity( 0.5 );
        frontMaterial.setShininess( 30 );
        renderer.getAlgebraicSurfaceRenderer().setFrontMaterial( frontMaterial );


        //frontColorPicker.setRGB( cf.getRed(), cf.getGreen(), cf.getBlue() );

        // init back material
        var backMaterial:Material=new Material();
        backMaterial.setColor( backColor );
        backMaterial.setAmbientIntensity( 0.4 );
        backMaterial.setDiffuseIntensity( 0.8 );
        backMaterial.setSpecularIntensity( 0.5 );
        backMaterial.setShininess( 30 );
        renderer.getAlgebraicSurfaceRenderer().setBackMaterial( backMaterial );

    }

    function initLights()
    {
        //var lights = [];
        //LightSource[] lights = new LightSource[ AlgebraicSurfaceRenderer.MAX_LIGHTS ];

        var light0:LightSource=new LightSource();
        light0.setPosition( new Point3d( -100, 100, 100 ) );
        light0.setIntensity( 0.7 );
        light0.setColor( new Color3f( 1, 1, 1 ) );

        var light1:LightSource=new LightSource();
        light1.setPosition( new Point3d( 100, 100, 100 ) );
        light1.setIntensity( 0.5 );
        light1.setColor( new Color3f( 1, 1, 1 ) );

        var light2:LightSource=new LightSource();
        light2.setPosition( new Point3d( 0, -100, 100 ) );
        light2.setIntensity( 0.3 );
        light2.setColor( new Color3f( 1, 1, 1 ) );

        renderer.getAlgebraicSurfaceRenderer().setLightSource(0,light0);
        renderer.getAlgebraicSurfaceRenderer().setLightSource(1,light1);
        renderer.getAlgebraicSurfaceRenderer().setLightSource(2,light2);
    }

    function getRenderer(): JSurferRenderPanel{
        initLights();
        initMaterials();
        renderer.setScale( 0.5 );
        /*void mouseWheelMoved(MouseWheelEvent e)
        {
            
        }*/

        renderer.addMouseWheelListener(
        MouseWheelListener{
            override function mouseWheelMoved(e)
            {
                scale=renderer.getScale()/4+0.5;
            }
        }

        
        );
        /*renderer.publicScaleFactor.addChangeListener(
        
       ChangeListener {
            override function stateChanged(e)
                {
                    //color= new Color3f(colorPicker.getColor());
                   // System.out.println("color2Picker has changed");
                }
            }
        );*/
        return renderer;
    }

    public override function create(): Node {
        /*initMaterials();
        initLights();*/
        def sw:SwingComponent=SwingComponent.wrap(getRenderer());
        sw.layoutInfo=LayoutInfo{
            minWidth: bind height,
            width: bind width
            maxWidth: bind width
            minHeight: bind height
            height: bind height
            maxHeight: bind height
         };
         //drawCoordinatenSystem
         onMousePressed=function(e: javafx.scene.input.MouseEvent): Void
         {
             renderer.drawCoordinatenSystem(true);
             renderer.repaintImage();
         };

         onMouseReleased=function(e: javafx.scene.input.MouseEvent): Void
         {
             renderer.drawCoordinatenSystem(false);
             renderer.repaintImage();
         };

        return Group {
                    translateX: bind x translateY: bind y;
                    content: [

                        SwingComponent.wrap(getRenderer())
                    ]
                }
    }

    public function surfaceExpressionChanged(expression:String):Boolean
    {
//        var P:AlgebraicExpressionParser= new AlgebraicExpressionParser;
  
        try
        {
/*
var p:PolynomialOperation;
           {
             var t1:PolynomialOperation=new PolynomialPower(new PolynomialVariable( PolynomialVariable.Var.valueOf( "x" ) ), 2 );
             var t2:PolynomialOperation=new PolynomialPower(new PolynomialVariable( PolynomialVariable.Var.valueOf( "y" ) ), 2 );
             var t3:PolynomialOperation=new PolynomialPower(new PolynomialVariable( PolynomialVariable.Var.valueOf( "z" ) ), 2 );
             var t4:PolynomialOperation=new PolynomialAddition(new PolynomialAddition(t1,t2),t3);
             var t5:PolynomialOperation=new PolynomialMultiplication(new PolynomialMultiplication(new DoubleValue( 2.0 ),new PolynomialVariable( PolynomialVariable.Var.valueOf( "x" ) )), new PolynomialMultiplication(new PolynomialVariable( PolynomialVariable.Var.valueOf( "y" ) ),new PolynomialVariable( PolynomialVariable.Var.valueOf( "z" ) )));
             var t6:PolynomialOperation=new PolynomialSubtraction(t5,new DoubleValue( 1.0 ));
             var t7:PolynomialOperation=new PolynomialAddition(t4,t6);
            //p.getAlgebraicSurfaceRenderer().setSurfaceExpression( t7 );
             p=t7;
           }
*/

/*var p:PolynomialOperation = AlgebraicExpressionParser.parse( "x^2+y^2-1+a" );
           var degree:Integer = p.accept( new DegreeCalculator(), ( null as java.lang.Void ) );
           var params:java.util.Set = p.accept( new DoubleVariableExtractor(), ( null as java.lang.Void ) );
           System.out.println( "degree={degree}" );
           System.out.println( "parameters={params.toString()}" );*/
           var p:PolynomialOperation = AlgebraicExpressionParser.parse( expression );
            var degree:Integer = p.accept( new DegreeCalculator(), ( null as java.lang.Void ) );
            
           // current version does not support surface parameters
           /*if( p.accept( new DoubleVariableChecker(), ( Void ) null ) )
               throw new Exception();*/
               var old:PolynomialOperation=renderer.getAlgebraicSurfaceRenderer().getSurfaceFamily();
               var oldString:String=renderer.getAlgebraicSurfaceRenderer().getSurfaceFamilyString();
               renderer.getAlgebraicSurfaceRenderer().setSurfaceFamily(p);
               renderer.getAlgebraicSurfaceRenderer().setSurfaceFamily(expression);
               var  PAR:Set=renderer.getAlgebraicSurfaceRenderer().getAllParameterNames();
               System.out.println("PAR voll:{PAR}");
               usedA=PAR.remove("a");
               usedB=PAR.remove("b");
               usedC=PAR.remove("c");
               usedD=PAR.remove("d");
               System.out.println("PAR leer({usedA},{usedB},{usedC},{usedD}):{PAR}");
               if (not PAR.isEmpty() or degree>30)
               {
                   renderer.getAlgebraicSurfaceRenderer().setSurfaceFamily(old);
                   System.out.println("falsch {oldString}");
                   correctExpression=false;
                   return false;
               }

               renderer.getAlgebraicSurfaceRenderer().setParameterValue("a", a);
               renderer.getAlgebraicSurfaceRenderer().setParameterValue("b", b);
               renderer.getAlgebraicSurfaceRenderer().setParameterValue("c", c);
               renderer.getAlgebraicSurfaceRenderer().setParameterValue("d", d);
               //setSurfaceExpression( p );
            renderer.repaintImage();
           //surfaceExpression.setBackground( Color.WHITE );
        }
        catch( Exception )
        {
            System.out.println("falsch");
            correctExpression=false;
            return false;
            //surfaceExpression.setBackground( new Color( 255, 90, 90 ).brighter() );
        }
        //parserFeld.setText(P.a()+"\n 1 \n 2 \n 3");
        //errorFeld.setText("line "+ P.errorRow()+" : "+P.errorMessage());
        correctExpression=true;
        return true;
    }


}
