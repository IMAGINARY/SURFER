/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import javax.vecmath.*;

import de.mfo.jsurfer.rendering.*;
import de.mfo.jsurfer.rendering.cpu.*;
import de.mfo.jsurfer.parser.*;
import de.mfo.jsurfer.util.*;
import de.mfo.jsurfer.algebra.*;
//import java.lang.System;
//import javax.swing.event.*;

/**
 * This panel displays an algebraic surface in its center. All settings of the used
 * @see{AlgebraicSurfaceRenderer} must be made by the user of this panel.
 * Only the surface an camera transformations are set automatically by this#
 * class. Changing same directly on the @see{AlgebraicSurfaceRenderer} or
 * @see{Camera} does not affect rendering at all.
 * Additionally it keeps the aspect ratio and anti-aliases the image, if there
 * is no user interaction.
 * @author Christian Stussak <christian at knorf.de>
 */
public class JSurferRenderPanel extends JComponent
{
    CPUAlgebraicSurfaceRenderer asr;
    BufferedImage surfaceImage;
    int[] colorBuffer;
    MemoryImageSource memoryImageSource;
    boolean refreshImage;
    boolean refreshImageAntiAliased;
    boolean renderSizeChanged;
    boolean resizeImageWithComponent;
    Dimension renderSize;
    RotateSphericalDragger rsd;
    Matrix4f scale;
    public float publicScaleFactor;//0.0 bis 1.0

    public JSurferRenderPanel()
    {
        renderSize = new Dimension( 240, 240 );
        
        refreshImage = true;
        refreshImageAntiAliased = true;
        renderSizeChanged = true;
        resizeImageWithComponent = false;
        
        asr = new CPUAlgebraicSurfaceRenderer();
        
        rsd = new RotateSphericalDragger();
        scale = new Matrix4f();
        scale.setIdentity();
        publicScaleFactor=0.5f;
        MouseAdapter ma = new MouseAdapter(){
            public void mousePressed( MouseEvent me ) { JSurferRenderPanel.this.mousePressed( me ); }
            public void mouseDragged( MouseEvent me ) { JSurferRenderPanel.this.mouseDragged( me ); }
            public void mouseWheelMoved( MouseWheelEvent mwe ) { JSurferRenderPanel.this.scaleSurface ( mwe.getWheelRotation() ); }
        };
        
        addMouseListener( ma );
        addMouseMotionListener( (MouseMotionListener) ma);
        addMouseWheelListener( (MouseWheelListener) ma);
        
        KeyAdapter ka = new KeyAdapter() {
            public void keyPressed( KeyEvent e )
            {
                if( e.getKeyCode() == e.VK_DOWN || e.getKeyCode() == e.VK_MINUS )
                    scaleSurface( 1 );
                else if( e.getKeyCode() == e.VK_UP || e.getKeyCode() == e.VK_PLUS )
                    scaleSurface( -1 );
            }
        };
        addKeyListener( ka );
        
        ComponentAdapter ca = new ComponentAdapter() {
            public void componentResized( ComponentEvent ce ) { JSurferRenderPanel.this.componentResized( ce ); }
        };
        addComponentListener( ca );
        
        setDoubleBuffered( true );
        setFocusable( true );
    }

    public AlgebraicSurfaceRenderer getAlgebraicSurfaceRenderer()
    {
        return this.asr;
    }
    
    public void setResizeImageWithComponent( boolean resize )
    {
        resizeImageWithComponent = resize;
        if( resizeImageWithComponent )
        {
            renderSize = getSize();
            renderSize.width = Math.max( 1, renderSize.width );
            renderSize.height = Math.max( 1, renderSize.height );
            renderSizeChanged = true;
            refreshImage = true;
            repaint();
        }
    }
    
    public boolean getResizeWithComponent()
    {
        return resizeImageWithComponent;
    }
    
    public void repaintImage()
    {
        refreshImage = true;
        repaint();
    }
    
    void createBufferedImage()
    {
        colorBuffer = new int[ renderSize.width * renderSize.height ];
        memoryImageSource = new MemoryImageSource( renderSize.width, renderSize.height, colorBuffer, 0, renderSize.width );
        memoryImageSource.setAnimated( true );
        memoryImageSource.setFullBufferUpdates( true );
    
        DirectColorModel colormodel = new DirectColorModel( 24, 0xff0000, 0xff00, 0xff );
        SampleModel sampleModel = colormodel.createCompatibleSampleModel( renderSize.width, renderSize.height );
        DataBufferInt data = new DataBufferInt( colorBuffer, renderSize.width * renderSize.height );
        WritableRaster raster = WritableRaster.createWritableRaster( sampleModel, data, new Point( 0, 0 ) );
        surfaceImage = new BufferedImage( colormodel, raster, false, null );
    }
    
    public Dimension getPreferredSize()
    {
        return new Dimension( renderSize.width, renderSize.height );
    }
    
    public void setRenderSize( Dimension d )
    {
        if( !resizeImageWithComponent )
        {
            if( !d.equals( renderSize ) )
            {
                renderSizeChanged = true;
                refreshImage = true;
                refreshImageAntiAliased = false;
                renderSize = new Dimension( d );
            }
        }
    }
    
    public Dimension getRenderSize()
    {
        return renderSize;
    }
    
    public void setScale( float scaleFactor )
    {
        if (scaleFactor<0.0f)scaleFactor=0.0f;
        if (scaleFactor>1.0f)scaleFactor=1.0f;
        publicScaleFactor=scaleFactor;
        
        scaleFactor=( float ) Math.pow( 10, scaleFactor*2.0);
        //System.out.println(publicScaleFactor+" "+scaleFactor);
        scale.setScale( scaleFactor );
    }

    public float getScale()
    {
        return publicScaleFactor;
    }

    public void saveToPNG( java.io.File f, int width, int height )
            throws java.io.IOException
    {
        Dimension oldDim = getRenderSize();
        setRenderSize( new Dimension( width, height ) );
        createBufferedImage();
        refreshImage( true );
        saveToPNG( f );
        setRenderSize( oldDim );
    }

    public void saveToPNG( java.io.File f )
            throws java.io.IOException
    {
        javax.imageio.ImageIO.write( surfaceImage, "png", f );
    }

    protected void paintComponent( Graphics g )
    {
        if( g instanceof Graphics2D )
        {
            if( renderSizeChanged )
            {
                createBufferedImage();
                renderSizeChanged = false;
            }
            if( refreshImage )
            {
                refreshImage( refreshImageAntiAliased );
                if( !refreshImageAntiAliased )
                {
                    refreshImage = refreshImageAntiAliased = true;
                    repaint();
                }
                else
                {
                    refreshImage = refreshImageAntiAliased = false;
                }
            }
            
            Graphics2D g2 = ( Graphics2D ) g;
            
            g2.setColor( this.asr.getBackgroundColor().get() );
            
            // calculate necessary painting information
            Point startPosition;            
            double scale;
            double aspectRatioComponent = this.getWidth() / ( double ) this.getHeight();
            double aspectRatioImage = renderSize.width / ( double ) renderSize.height;
            Rectangle r1, r2;
            if( aspectRatioImage > aspectRatioComponent )
            {
                // scale image width to component width
                scale = this.getWidth() / ( double ) renderSize.width;
                int newImageHeight = ( int ) ( renderSize.height * scale );
                startPosition = new Point( 0, ( this.getHeight() - newImageHeight ) / 2 );
                r1 = new Rectangle( 0, 0, this.getWidth(), startPosition.y );
                r2 = new Rectangle( 0, startPosition.y + newImageHeight, this.getWidth(), this.getHeight() - newImageHeight );
            }
            else
            {
                // scale image height to component height
                scale = this.getHeight() / ( double ) renderSize.height;
                int newImageWidth = ( int ) ( renderSize.width * scale );
                startPosition = new Point( ( this.getWidth() - newImageWidth ) / 2, 0 );
                r1 = new Rectangle( 0, 0, startPosition.x, this.getHeight() );
                r2 = new Rectangle( startPosition.x + newImageWidth, 0, this.getWidth() - newImageWidth, this.getHeight() );
            }

            // fill margins with background color
            g2.fillRect( r1.x, r1.y, r1.width, r1.height );
            g2.fillRect( r2.x, r2.y, r2.width, r2.height );
            
            // draw the surface image to the component and apply appropriate scaling
            AffineTransform t = new AffineTransform();
            t.scale( scale, scale );
            g2.drawImage( surfaceImage, new AffineTransformOp( t, AffineTransformOp.TYPE_BILINEAR ), startPosition.x, startPosition.y );
        }
        else
        {
            super.paintComponents( g );
            g.drawString( "this component needs a Graphics2D for painting", 2, this.getHeight() - 2 );
        }
    }
    
    protected void refreshImage( boolean antiAliased )
    {
        Matrix4f rotation = new Matrix4f();
        rotation.invert( rsd.getRotation() );
        asr.setTransform( rotation );
        asr.setSurfaceTransform( scale );
        asr.setAntiAliasingMode( CPUAlgebraicSurfaceRenderer.AntiAliasingMode.ADAPTIVE_SUPERSAMPLING );
        if( antiAliased )
            asr.setAntiAliasingPattern( AntiAliasingPattern.PATTERN_4x4 );
        else
            asr.setAntiAliasingPattern( AntiAliasingPattern.PATTERN_2x2 );
        
        setOptimalCameraDistance( asr.getCamera() );
        
        asr.draw( colorBuffer, renderSize.width, renderSize.height );
    }
    
    protected static void setOptimalCameraDistance( Camera c )
    {
        float cameraDistance;
        switch( c.getCameraType() )
        {
            case ORTHOGRAPHIC_CAMERA:
                cameraDistance = 1.0f;
                break;
            case PERSPECTIVE_CAMERA:
                cameraDistance = ( float ) ( 1.0 / Math.sin( ( Math.PI / 180.0 ) * ( c.getFoVY() / 2.0 ) ) );
                break;
            default:
                throw new RuntimeException();
        }
        c.lookAt( new Point3f( 0f, 0f, cameraDistance ), new Point3f( 0f, 0f, 0f ), new Vector3f( 0f, 1f, 0f ) );
    }
    
    protected void componentResized( ComponentEvent ce )
    {
        rsd.setXSpeed( 180.0f / this.getWidth() );
        rsd.setYSpeed( 180.0f / this.getHeight() );
        if( resizeImageWithComponent )
        {
            renderSize = new Dimension( Math.max( 1, this.getWidth() ), Math.max( 1, this.getHeight() ) );
            renderSizeChanged = true;
            refreshImage = true;
            refreshImageAntiAliased = false;
            repaint();
        }
    }
    
    protected void mousePressed( MouseEvent me )
    {
        grabFocus();
        rsd.startDrag( me.getPoint() );
    }

    protected void mouseDragged( MouseEvent me )
    {
        rsd.dragTo( me.getPoint() );
        refreshImage = true;
        refreshImageAntiAliased = false;
        repaint();
    }

    protected void scaleSurface( int units )
    {
        
        /*Matrix4f tmp = new Matrix4f();
        tmp.setIdentity();
        tmp.setScale( ( float ) Math.pow( 1.0625, units ) );
        scale.mul( tmp );*/
        setScale(publicScaleFactor-((float)(units))/200.0f );
        
        refreshImage = true;
        refreshImageAntiAliased = false;
        repaint();        
    }
    
    public static void main( String[]args )
    {
        JSurferRenderPanel p = new JSurferRenderPanel();
        //p.setResizeImageWithComponent( true );

        try
        {
            p.getAlgebraicSurfaceRenderer().setSurfaceExpression( AlgebraicExpressionParser.parse( "x^2+y^2-1" ) );
            /*
            PolynomialOperation t1=new PolynomialPower(new PolynomialVariable( PolynomialVariable.Var.valueOf( "x" ) ), 2 );
    PolynomialOperation t2=new PolynomialPower(new PolynomialVariable( PolynomialVariable.Var.valueOf( "y" ) ), 2 );
    PolynomialOperation t3=new PolynomialPower(new PolynomialVariable( PolynomialVariable.Var.valueOf( "z" ) ), 2 );
    PolynomialOperation t4=new PolynomialAddition(new PolynomialAddition(t1,t2),t3);
    PolynomialOperation t5=new PolynomialMultiplication(new PolynomialMultiplication(new DoubleValue( 2.0 ),new PolynomialVariable( PolynomialVariable.Var.valueOf( "x" ) )),
                                                        new PolynomialMultiplication(new PolynomialVariable( PolynomialVariable.Var.valueOf( "y" ) ),new PolynomialVariable( PolynomialVariable.Var.valueOf( "z" ) )));
    PolynomialOperation t6=new PolynomialSubtraction(t5,new DoubleValue( 1.0 ));
    PolynomialOperation t7=new PolynomialAddition(t4,t6);
            p.getAlgebraicSurfaceRenderer().setSurfaceExpression( t7 );

             */
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        JFrame f = new JFrame();
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.getContentPane().add( p );
        f.pack();
        f.setVisible( true );
    }
}