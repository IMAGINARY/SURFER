/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mfo.jsurfer.gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import de.mfo.jsurfer.rendering.*;
import de.mfo.jsurfer.rendering.cpu.*;
import de.mfo.jsurfer.debug.*;
import de.mfo.jsurfer.util.*;

import java.awt.image.*;
import javax.vecmath.*;

import de.mfo.jsurfer.algebra.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class TestMain
{
    CPUAlgebraicSurfaceRenderer c;
    int[] colorBuffer;
    MemoryImageSource mis;
    int width;
    int height;
    JFrame f;
    MausZeuch mz;
    RotateSphericalDragger rsd;
    

    class MausZeuch extends MouseInputAdapter
    {
        public void mousePressed( MouseEvent me )
        {
            rsd.startDrag( me.getPoint() );
        }

        public void mouseDragged( MouseEvent me )
        {
            rsd.dragTo( me.getPoint() );
            draw();
        }

        public void draw()
        {
            long startTime = System.currentTimeMillis();

            Matrix4d scale = new Matrix4d();
            scale.setIdentity();
            scale.setScale( 1 / ( 0.2808988764044944 / 0.9 ) );

            Matrix4d translation = new Matrix4d();
            translation.setIdentity();
            translation.setTranslation( new Vector3d( 0f, 0.0f, 0.0f ) );
            translation.setScale( 1f );

            Matrix4d surfaceTransform = rsd.getRotation();
            surfaceTransform.mul( scale );

            c.setSurfaceTransform( surfaceTransform );
            c.setTransform( translation );
            c.draw( colorBuffer, width, height );
            mis.newPixels();
            //f.repaint();
            f.paint( f.getGraphics() );
            System.out.println( 1000.0 / ( ( System.currentTimeMillis() - startTime ) ) );
        }
    }

    public TestMain()
    {
        rsd = new RotateSphericalDragger();
        createAndShowGUI();
        rsd.setXSpeed( 180.0f / width );
        rsd.setYSpeed( 180.0f / height );
        
        c = new CPUAlgebraicSurfaceRenderer();
        //c.setBackgroundColor( new Color3f( 1.0f, 1.0f, 1.0f ) );
        c.setBackgroundColor( new Color3f( 60f / 255f, 138f / 255f, 188f / 255f ) );
        
/*
        LightSource lightSource0 = new LightSource();
        lightSource0.setPosition( new Point3f( -100f, 100f, 100f ) );
        lightSource0.setIntensity( 0.5f );
        lightSource0.setColor( new Color3f( 1f, 1f, 1f ) );

        LightSource lightSource1 = new LightSource();
        lightSource1.setPosition( new Point3f( 100f, 100f, 100f ) );
        lightSource1.setIntensity( 0.7f );
        lightSource1.setColor( new Color3f( 1f, 1f, 1f ) );

        LightSource lightSource2 = new LightSource();
        lightSource2.setPosition( new Point3f( 0f, -100f, 100f ) );
        lightSource2.setIntensity( 0.3f );
        lightSource2.setColor( new Color3f( 1f, 1f, 1f ) );
 */
        
        LightSource lightSource0 = new LightSource();
        lightSource0.setPosition( new Point3d( 100f, -100f, 100f ) );
        lightSource0.setIntensity( 0.5f );
        lightSource0.setColor( new Color3f( 1f, 1f, 1f ) );

        LightSource lightSource1 = new LightSource();
        lightSource1.setPosition( new Point3d( -100f, -100f, 100f ) );
        lightSource1.setIntensity( 0.7f );
        lightSource1.setColor( new Color3f( 1f, 1f, 1f ) );

        LightSource lightSource2 = new LightSource();
        lightSource2.setPosition( new Point3d( 0f, 100f, 100f ) );
        lightSource2.setIntensity( 0.3f );
        lightSource2.setColor( new Color3f( 1f, 1f, 1f ) );

        c.setLightSource( 0, lightSource0 );
        c.setLightSource( 1, lightSource1 );
        c.setLightSource( 2, lightSource2 );

        Material frontMaterial = new Material();
        //frontMaterial.setColor( new Color3f( 1.0f, 0.8f, 0.4f ) );
        frontMaterial.setColor( new Color3f( 191f / 255f, 99f / 255f, 168f / 255f ) );
        frontMaterial.setAmbientIntensity( 0.4f );
        frontMaterial.setDiffuseIntensity( 0.65f );
        frontMaterial.setSpecularIntensity( 0.5f );
        frontMaterial.setShininess( 50 );

        Material backMaterial = new Material();
        backMaterial.setColor( new Color3f( 168f / 255f, 66f / 255f, 66f / 255f ) );
        backMaterial.setAmbientIntensity( 0.4f );
        backMaterial.setDiffuseIntensity( 0.8f );
        backMaterial.setSpecularIntensity( 0.8f );
        backMaterial.setShininess( 50 );

        c.setFrontMaterial( frontMaterial );
        c.setBackMaterial( backMaterial );

        Camera camera = new Camera();
        camera.setFoVY( 60 );
        //camera.setCameraType( Camera.CameraType.PERSPECTIVE_CAMERA );
        camera.setCameraType( Camera.CameraType.ORTHOGRAPHIC_CAMERA );
        camera.lookAt( new Point3d( 0f, 0f, 2f ), new Point3d( 0f, 0f, 0f ), new Vector3d( 0f, 1f, 0f ) );

        c.setCamera( camera );
        c.setAntiAliasingPattern( AntiAliasingPattern.OG_1x1 );

        try
        {
            //c.setSurfaceExpression( AlgebraicExpressionParser.parse( "1*0.99*(64*(0.5*z)^7-112*(0.5*z)^5+56*(0.5*z)^3-7*(0.5*z)-1)+(.7818314825-.3765101982*y-.7818314825*x)*(.7818314824-.8460107361*y-.1930964297*x)*(.7818314825-.6784479340*y+.5410441731*x)*(.7818314825+.8677674789*x)*(.7818314824+.6784479339*y+.541044172*x)*(.7818314824+.8460107358*y-.193096429*x)*(.7818314821+.3765101990*y-.781831483*x)" ) );
            //c.setSurfaceExpression( AlgebraicExpressionParser.parse( "4*(1.618033988^2*x^2-y^2)*(1.618033988^2*y^2-z^2)*(1.618033988^2*z^2-x^2)-(1+2*1.618033988)*(x^2+y^2+z^2-1)^2" ) );
            //c.setSurfaceExpression( AlgebraicExpressionParser.parse( "(8*x^4-8*x^2+1)^2+(8*y^4-8*y^2+1)^2+(8*z^4-8*z^2+1)^2-1" ) );
            //c.setSurfaceExpression( AlgebraicExpressionParser.parse( "4*((1*(1+sqrt(5))/2)^2*(x+0.2)^2-y^2)*((1*(1+sqrt(5))/2)^2*y^2-z^2)*((1*(1+sqrt(5))/2)^2*z^2-(x+0.2)^2)-(1+2*(1*(1+sqrt(5))/2))*((x+0.2)^2+y^2+z^2-1)^3" ) );
            //c.setSurfaceExpression( AlgebraicExpressionParser.parse( "x^2-x^3+y^2+y^4+z^3-1*z^4" ) );
            //c.setSurfaceExpression( AlgebraicExpressionParser.parse( "x^2+y^2+z^2+2*x*y*z-1" ) );
            //c.setSurfaceExpression( AlgebraicExpressionParser.parse( "x" ) );
            //c.setSurfaceExpression( AlgebraicExpressionParser.parse( "x^2+y^2-z^2" ) );
            //c.setSurfaceExpression( AlgebraicExpressionParser.parse( "-x^4-y^4-z^4+4*(x^2+y^2*z^2+y^2+x^2*z^2+z^2+y^2*x^2)-12*1.7320508*x*y*z-1" ) );
            //c.setSurfaceExpression( AlgebraicExpressionParser.parse( "0.25^4*x^2*y^2+0.25^4*y^2*z^2+0.25^4*z^2*x^2-0.25^3*x*y*z" ) );
           // c.setSurfaceExpression( AlgebraicExpressionParser.parse( "(2*z^2+x^2+y^2-1)^3-0.1*z^2*y^3-x^2*y^3" ) );
        } catch( Exception e )
        {
            e.printStackTrace();
        }

        rsd.startDrag( new Point() );
        rsd.dragTo( new Point( 0, 100 ) );
        rsd.dragTo( new Point( 100, 100 ) );
        mz.draw();
        /*
        BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        bi.setRGB( 0, 0, width, height, colorBuffer, 0, width );
        try
        {
            javax.imageio.ImageIO.write( bi, "png", new java.io.File( "C:\\Users\\Christian\\Desktop\\heart.png" ) );
        }
        catch( Exception e )
        {
            System.out.println( e );
        }*/
    }

    public void createAndShowGUI()
    {
        width = 240;
        height = 240;

        f = new JFrame( "JSurfer" );
        f.setSize( width, height );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        mz = new MausZeuch();
        f.addMouseListener( mz );
        f.addMouseMotionListener( mz );

        // create buffer for image data
        colorBuffer = new int[ width * height ];

        // create Java-MemoryImageSource
        mis = new MemoryImageSource( width, height, colorBuffer, 0, width );
        mis.setAnimated( true );
        mis.setFullBufferUpdates( true );

        // create corresponding image for display
        Image screenImage = Toolkit.getDefaultToolkit().createImage( mis );

        // put image into a JLabel
        JLabel l = new JLabel( new ImageIcon( screenImage ) );
        f.getContentPane().add( l );

        f.pack();
        f.setVisible( true );
    }

    public static void main( String[] args )
    {
        Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler()
                                               {
                                                   public void uncaughtException( Thread t, Throwable e )
                                                   {
                                                       System.err.println( "Uncaught exception in thread " + t + ": " + e );
                                                       e.printStackTrace();
                                                   }
                                               } );                                              

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater( new Runnable()
                                            {
                                                public void run()
                                                {
                                                    TestMain t = new TestMain();
                                                }
                                            } );

    }
}
