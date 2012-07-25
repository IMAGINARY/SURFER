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

// input/output
import java.net.URL;
import java.util.*;
import java.io.*;

import de.mfo.jsurfer.rendering.*;
import de.mfo.jsurfer.rendering.cpu.*;
import de.mfo.jsurfer.parser.*;
import de.mfo.jsurfer.util.*;
import static de.mfo.jsurfer.rendering.cpu.CPUAlgebraicSurfaceRenderer.AntiAliasingMode;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.*;
import javax.media.opengl.awt.GLJPanel;

import java.awt.BorderLayout;

import java.util.concurrent.*;

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
    class ImgBuffer
    {
        public int[] rgbBuffer;
        public int width;
        public int height;

        public ImgBuffer( int w, int h ) { rgbBuffer = new int[ 3 * w * h ]; width = w; height = h; }
    }


    CPUAlgebraicSurfaceRenderer asr;
    ImgBuffer currentSurfaceImage;
    boolean resizeImageWithComponent;
    boolean renderCoordinatenSystem;
    Dimension renderSize;
    Dimension minLowResRenderSize;
    Dimension maxLowResRenderSize;
    RotateSphericalDragger rsd;
    Matrix4d scale;
    RenderWorker rw;
    GLJPanel glcanvas;

    class RenderWorker extends Thread
    {
        Semaphore semaphore = new Semaphore( 0 );
        boolean finish = false;
        boolean is_drawing_hi_res = false;
        double time_per_pixel = 1000.0;
        final double desired_fps = 15.0;
        boolean skip_hi_res = false;

        public void finish()
        {
            finish = true;
        }

        public void scheduleRepaint()
        {
            // schedule redraw
            semaphore.release();

            // try to ensure, that high resolution drawing is canceled
            if( is_drawing_hi_res )
                JSurferRenderPanel.this.asr.stopDrawing();
        }

        public void stopHighResolutionRendering()
        {
            semaphore.drainPermits(); // remove all currently available permits
            skip_hi_res = true;

            // try to ensure, that current high resolution rendering is canceled
            if( is_drawing_hi_res )
                JSurferRenderPanel.this.asr.stopDrawing();
        }

        @Override
        public void run()
        {
            this.setPriority( Thread.MIN_PRIORITY );
            while( !finish )
            {
                try
                {
                    int available_permits = semaphore.availablePermits();
                    semaphore.acquire( Math.max( 1, available_permits ) ); // wait for new task and grab all permits
                    skip_hi_res = false;
                    long minPixels = JSurferRenderPanel.this.minLowResRenderSize.width * JSurferRenderPanel.this.minLowResRenderSize.height;
                    long maxPixels = JSurferRenderPanel.this.maxLowResRenderSize.width * JSurferRenderPanel.this.maxLowResRenderSize.height;
                    maxPixels = Math.max( 1, Math.min( maxPixels, JSurferRenderPanel.this.getWidth() * JSurferRenderPanel.this.getHeight() ) );
                    minPixels = Math.min( minPixels, maxPixels );
                    long numPixelsAt15FPS = ( long ) ( 1.0 / ( desired_fps * time_per_pixel ) );
                    long pixelsToUse = Math.max( minPixels, Math.min( maxPixels, numPixelsAt15FPS ) );
                    JSurferRenderPanel.this.renderSize = new Dimension( (int) Math.sqrt( pixelsToUse ), (int) Math.sqrt( pixelsToUse ) );

                    // render low res
                    {
                        ImgBuffer ib = draw( renderSize.width, renderSize.height, AntiAliasingMode.ADAPTIVE_SUPERSAMPLING, AntiAliasingPattern.QUINCUNX, true );
                        if( ib != null )
                        {
                            currentSurfaceImage =  ib;
                            JSurferRenderPanel.this.repaint();
                        }
                    }

                    if( semaphore.tryAcquire( 100, TimeUnit.MILLISECONDS ) ) // wait some time, then start with high res drawing
                    {
                        semaphore.release();
                        continue;
                    }
                    else if( skip_hi_res )
                        continue;

                    // render high res, if no new low res rendering is scheduled
                    {
                        is_drawing_hi_res = true;
                        ImgBuffer ib = draw( JSurferRenderPanel.this.getWidth(), JSurferRenderPanel.this.getHeight(), AntiAliasingMode.ADAPTIVE_SUPERSAMPLING, AntiAliasingPattern.OG_4x4, false );
                        if( ib != null )
                        {
                            currentSurfaceImage =  ib;
                            JSurferRenderPanel.this.repaint();
                        }
                        is_drawing_hi_res = false;
                    }

                    if( semaphore.availablePermits() > 0 ) // restart, if user has changes the view
                        continue;
                    else if( skip_hi_res )
                        continue;

                    // render high res with even better quality
                    {
                        //System.out.println( "drawing hi res");
                        is_drawing_hi_res = true;
                        ImgBuffer ib = draw( JSurferRenderPanel.this.getWidth(), JSurferRenderPanel.this.getHeight(), AntiAliasingMode.SUPERSAMPLING, AntiAliasingPattern.OG_4x4, false );
                        if( ib != null )
                        {
                            currentSurfaceImage =  ib;
                            JSurferRenderPanel.this.repaint();
                        }
                        is_drawing_hi_res = false;
                        //System.out.println( "finised hi res");
                    }
                }
                catch( InterruptedException ie )
                {
                }
            }
        }

        public ImgBuffer draw( int width, int height, CPUAlgebraicSurfaceRenderer.AntiAliasingMode aam, AntiAliasingPattern aap )
        {
            return draw( width, height, aam, aap, false );
        }

        public ImgBuffer draw( int width, int height, CPUAlgebraicSurfaceRenderer.AntiAliasingMode aam, AntiAliasingPattern aap, boolean save_fps )
        {
            // create color buffer
            ImgBuffer ib = new ImgBuffer( width, height );

            // do rendering
            Matrix4d rotation = new Matrix4d();
            rotation.invert( rsd.getRotation() );
            Matrix4d id = new Matrix4d();
            id.setIdentity();
            Matrix4d tm = new Matrix4d( rsd.getRotation() );
            tm.mul( scale );
            asr.setTransform( rsd.getRotation() );
            asr.setSurfaceTransform( scale );
            asr.setAntiAliasingMode( aam );
            asr.setAntiAliasingPattern( aap );
            setOptimalCameraDistance( asr.getCamera() );

            try
            {
                long t_start = System.nanoTime();
                asr.draw( ib.rgbBuffer, width, height );
                long t_end = System.nanoTime();
                double fps = 1000000000.0 / ( t_end - t_start );
                System.err.println( fps + "fps at " + width +"x" + height );
                if( save_fps )
                    time_per_pixel = ( ( t_end - t_start ) / 1000000000.0 ) / ( width * height );
                return ib;
            }
            catch( RenderingInterruptedException rie )
            {
                return null;
            }
            catch( Throwable t )
            {
                System.out.println( t );
                return null;
            }
        }
    }

    public JSurferRenderPanel()
    {
        renderCoordinatenSystem = false;
        minLowResRenderSize = new Dimension( 150, 150 );
        maxLowResRenderSize = new Dimension( 512, 512 );
        //renderSize = minLowResRenderSize;

        resizeImageWithComponent = false;

        asr = new CPUAlgebraicSurfaceRenderer();

        rsd = new RotateSphericalDragger();
        scale = new Matrix4d();
        scale.setIdentity();
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

        glcanvas = createGLCanvas();
        setLayout( new BorderLayout() );
        add( glcanvas, BorderLayout.CENTER );
/*
        final JFrame jframe = new JFrame( "One Triangle Swing GLCanvas" );
        jframe.getContentPane().add( glcanvas, BorderLayout.CENTER );
        jframe.setSize( 640, 480 );
        jframe.setVisible( true );
*/
        rw = new RenderWorker();
        rw.start();
        currentSurfaceImage = null;
    }

    protected GLJPanel createGLCanvas()
    {
        GLCapabilities caps = new GLCapabilities( GLProfile.get( GLProfile.GL2 ) );
        caps.setSampleBuffers( true );
        caps.setNumSamples( 4 );
        glcanvas = new GLJPanel( caps );
        glcanvas.addGLEventListener( new GLEventListener() {

            int textureId;

            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                GL2 gl = glautodrawable.getGL().getGL2();
                gl.glViewport(x, y, width, height);
                gl.glMatrixMode( GL2.GL_PROJECTION );
                gl.glLoadIdentity();
                if( width > height )
                    gl.glOrtho( 0, width / ( double ) height, 0, 1, -2, 2 );
                else
                    gl.glOrtho( 0, 1, 0, height / ( double ) width, -2, 2 );
            }

            @Override
            public void init( GLAutoDrawable glautodrawable )
            {
                GL2 gl = glautodrawable.getGL().getGL2();
                
                // create texture
                int[] tmpId = new int[ 1 ];
                gl.glGenTextures( 1, tmpId, 0 );
                textureId = tmpId[ 0 ];

                // more initialization
                //gl.glClearColor( 1.0f, 0.0f, 0.0f, 1.0f );
                gl.glClearColor( 1.0f, 1.0f, 1.0f, 1.0f );
                gl.glClearDepth( 1 );
                gl.glEnable( GL2.GL_DEPTH_TEST );

                // antialias lines
                gl.glLineWidth( 2 );
                gl.glEnable( GL2.GL_LINE_SMOOTH );
                gl.glHint( GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST );
                gl.glEnable( GL2.GL_BLEND );
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

                gl.glEnable( GL2.GL_TEXTURE_2D );

                gl.glEnable( GL2.GL_CULL_FACE );
                gl.glCullFace( GL2.GL_BACK );

                gl.glShadeModel( GL2.GL_SMOOTH );

                gl.glHint( GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST );

                gl.glEnable( GL2.GL_NORMALIZE );
                gl.glEnable( GL2.GL_MULTISAMPLE );

                gl.glEnable( GL2.GL_LIGHT0 );
                gl.glDisable( GL2.GL_LIGHT1 );
                gl.glDisable( GL2.GL_LIGHT2 );
                gl.glDisable( GL2.GL_LIGHT3 );
                gl.glDisable( GL2.GL_LIGHT4 );
                gl.glDisable( GL2.GL_LIGHT5 );
                gl.glDisable( GL2.GL_LIGHT6 );
                gl.glDisable( GL2.GL_LIGHT7 );

                float ambientLight0[] = { 0.4f, 0.4f, 0.4f, 1.0f };
                float diffuseLight0[] = { 1f, 1f, 1f, 1.0f };
                float specularLight0[] = { 1f, 1f, 1f, 1.0f };

                gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight0, 0 );
                gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight0, 0 );
                gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_SPECULAR, specularLight0, 0 );
            }

            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {
            }

            private void drawCylinder( GL2 gl, double bottomRadius, double topRadius, double height )
            {
                GLU glu = new GLU();
                GLUquadric gluQuadric = glu.gluNewQuadric();
                glu.gluQuadricNormals( gluQuadric, GLU.GLU_SMOOTH );
                glu.gluCylinder( gluQuadric, bottomRadius, topRadius, height, 32, 32 );

                glu.gluQuadricOrientation( gluQuadric, GLU.GLU_INSIDE );
                glu.gluDisk( gluQuadric, 0.0, bottomRadius, 32, 5 );
                glu.gluQuadricOrientation( gluQuadric, GLU.GLU_OUTSIDE );

                gl.glPushAttrib( GL2.GL_MATRIX_MODE );
                gl.glMatrixMode( GL2.GL_MODELVIEW );
                gl.glPushMatrix();
                gl.glTranslated( 0, 0, height );
                glu.gluDisk( gluQuadric, 0.0, topRadius, 32, 5 );
                gl.glPopMatrix();
                gl.glPopAttrib();
            }

            private void drawCoordinateSystem( GL2 gl )
            {
                gl.glMatrixMode( GL2.GL_MODELVIEW );

                gl.glTranslated(1- 0.08, 0.08, 0 );
                gl.glScaled( 0.08, 0.08, 0.08 );

                Matrix4d r = rsd.getRotation();
                r.transpose();

                double[] rf = { r.m00, r.m10, r.m20, r.m30,
                               r.m01, r.m11, r.m21, r.m31,
                               r.m02, r.m12, r.m22, r.m32,
                               r.m03, r.m13, r.m23, r.m33 };

//                gl.glScaled( 1, -1, -1 );
                gl.glMultMatrixd( rf, 0 );
//                gl.glScaled( 1, -1, -1 );

                double radiusCyl = 0.04;
                double tipLength = 0.33;
                double radiusTip = 0.125;


                float ambientMatX[] = { 0.1745f, 0.01175f, 0.01175f, 1f };
                float diffuseMatX[] = { 0.61424f, 0.04136f, 0.04136f, 1f };
                float specularMatX[] = { 0.727811f, 0.626959f, 0.626959f, 1f };
                float shininessX[] = { 76.8f };

                float ambientMatY[] = { 0.01175f, 0.1745f, 0.08725f, 1f };
                float diffuseMatY[] = { 0.04136f, 0.61424f, 0.30712f, 1f };
                float specularMatY[] = { 0.626959f, 0.727811f, 0.626959f, 1f };
                float shininessY[] = { 76.8f };

                float ambientMatZ[] = { 0.01175f, 0.01175f, 0.1745f, 1f };
                float diffuseMatZ[] = { 0.04136f, 0.04136f, 0.61424f, 1f };
                float specularMatZ[] = { 0.626959f, 0.626959f, 0.727811f, 1f };
                float shininessZ[] = { 76.8f };

                gl.glEnable( GL2.GL_LIGHTING );
                gl.glDisable( GL2.GL_TEXTURE_2D );

                gl.glPushMatrix();
                gl.glBegin( GL2.GL_QUADS );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambientMatZ, 0 );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuseMatZ, 0 );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specularMatZ, 0 );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininessZ, 0 );
                gl.glEnd();
                drawCylinder( gl, radiusCyl, radiusCyl, 1.0 - tipLength ); // z-axis
                gl.glTranslated( 0, 0, 1 - tipLength );
                drawCylinder( gl, radiusTip, 0, tipLength ); // tip of y-axis
                gl.glPopMatrix();

                gl.glPushMatrix();
                gl.glRotated( 90.0, 0, 1, 0 );
                gl.glBegin( GL2.GL_QUADS );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambientMatX, 0 );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuseMatX, 0 );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specularMatX, 0 );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininessX, 0 );
                gl.glEnd();
                drawCylinder( gl, radiusCyl, radiusCyl, 1.0 - tipLength ); // x-axis
                gl.glTranslated( 0, 0, 1 - tipLength );
                drawCylinder( gl, radiusTip, 0, tipLength ); // tip of x-axis
                gl.glPopMatrix();

                gl.glPushMatrix();
                gl.glRotated( 90.0, -1, 0, 0 );
                gl.glBegin( GL2.GL_QUADS );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambientMatY, 0 );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuseMatY, 0 );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specularMatY, 0 );
                    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininessY, 0 );
                gl.glEnd();
                drawCylinder( gl, radiusCyl, radiusCyl, 1.0 - tipLength ); // Y-axis
                gl.glTranslated( 0, 0, 1 - tipLength );
                drawCylinder( gl, radiusTip, 0, tipLength ); // tip of z-axis
                gl.glPopMatrix();
            }

            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                GL2 gl = glautodrawable.getGL().getGL2();

                gl.glEnable( GL2.GL_TEXTURE_2D );
                gl.glBindTexture( GL2.GL_TEXTURE_2D, textureId );

                gl.glPixelStorei( GL2.GL_UNPACK_ALIGNMENT, 1 );

                ImgBuffer tmpImg = currentSurfaceImage;
                if( tmpImg != null )
                {
                    gl.glTexImage2D( GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, tmpImg.width, tmpImg.height, 0, GL2.GL_BGRA, GL2.GL_UNSIGNED_BYTE, java.nio.IntBuffer.wrap( tmpImg.rgbBuffer ) );
                }

                gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP );
                gl.glTexParameteri ( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP );
                gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR );
		gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR );
                Color3f bg_color = JSurferRenderPanel.this.asr.getBackgroundColor();
                float[] borderColor={ bg_color.x, bg_color.y, bg_color.z, 1.0f };
                gl.glTexParameterfv( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_BORDER_COLOR, borderColor, 0 ); // set texture border to background color to guarantee correct texture interpolation at the boundary

                gl.glTexEnvf( GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL );

                gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

                gl.glMatrixMode( GL2.GL_MODELVIEW );
                gl.glLoadIdentity();
                
                float position0[] = { 0f, 0f, 10f, 1.0f };
                gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_POSITION, position0, 0 );

                gl.glDisable( GL2.GL_LIGHTING );
                gl.glEnable( GL2.GL_TEXTURE_2D );

                int w = glautodrawable.getWidth();
                int h = glautodrawable.getHeight();

                gl.glMatrixMode( GL2.GL_PROJECTION );
                gl.glPushMatrix();
                gl.glLoadIdentity();
                gl.glOrtho(0, w, 0, h, -2, 2 );

                gl.glBegin( GL2.GL_QUADS );
                    gl.glColor3d( 1, 1, 1 );
                    gl.glTexCoord2d( 1.0, 0.0 );
                    gl.glVertex3d( w, 0, -1.5 );
                    gl.glTexCoord2d( 1.0, 1.0 );
                    gl.glVertex3d( w, h, -1.5 );
                    gl.glTexCoord2d( 0.0, 1.0 );
                    gl.glVertex3d( 0, h, -1.5 );
                    gl.glTexCoord2d( 0.0, 0.0 );
                    gl.glVertex3d( 0, 0, -1.5 );
                gl.glEnd();
                gl.glPopMatrix();

                if( renderCoordinatenSystem )
                    drawCoordinateSystem( gl );
            }
        });
        return glcanvas;
    }

    public AlgebraicSurfaceRenderer getAlgebraicSurfaceRenderer()
    {
        return this.asr;
    }

    public void setResizeImageWithComponent( boolean resize )
    {
        resizeImageWithComponent = resize;
    }

    public boolean getResizeWithComponent()
    {
        return resizeImageWithComponent;
    }

    public void repaintImage()
    {
        scheduleSurfaceRepaint();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension( minLowResRenderSize.width, minLowResRenderSize.height );
    }


    public void setMinLowResRenderSize( Dimension d )
    {
        this.minLowResRenderSize = d;
    }

    public void setMaxLowResRenderSize( Dimension d )
    {
        this.maxLowResRenderSize = d;
    }

    public Dimension getMinLowResRenderSize()
    {
        return this.minLowResRenderSize;
    }

    public Dimension getMaxLowResRenderSize()
    {
        return this.maxLowResRenderSize;
    }

    public Dimension getRenderSize()
    {
        return this.renderSize;
    }

    public void setScale( double scaleFactor )
    {
        if (scaleFactor<-2.0)scaleFactor=-2.0;
        if (scaleFactor>2.0)scaleFactor=2.0;

        scaleFactor= Math.pow( 10, scaleFactor);
        //System.out.println(" scaleFactor: "+scaleFactor);
        scale.setScale( scaleFactor );
    }

    public double getScale()
    {
        //System.out.println("getScale "+this.scale.getScale()+" "+this.scale.m00+" "+(float)Math.log10(this.scale.getScale()));
        return Math.log10(this.scale.getScale());
    }

    public void saveToPNG( java.io.File f, int width, int height )
            throws java.io.IOException
    {
        Dimension oldMinDim = getMinLowResRenderSize();
        Dimension oldMaxDim = getMaxLowResRenderSize();
        setMinLowResRenderSize( new Dimension( width, height ) );
        setMaxLowResRenderSize( new Dimension( width, height ) );
        scheduleSurfaceRepaint();
        try
        {
            saveToPNG( f, (ImgBuffer) rw.draw( width, height, CPUAlgebraicSurfaceRenderer.AntiAliasingMode.ADAPTIVE_SUPERSAMPLING, AntiAliasingPattern.OG_4x4 ) );
        }
        catch( java.util.concurrent.CancellationException ce ) {}
        setMinLowResRenderSize( oldMinDim );
        setMaxLowResRenderSize( oldMaxDim );
        scheduleSurfaceRepaint();
    }
    public void saveString(java.io.File file, java.lang.String string)
            throws java.io.IOException
    {
        java.io.FileWriter writer=new java.io.FileWriter(file ,false);
        writer.write(string);
        writer.flush();
        writer.close();
    }
    static BufferedImage createBufferedImageFromRGB( ImgBuffer ib )
    {
        int w = ib.width;
        int h = ib.height;

        DirectColorModel colormodel = new DirectColorModel( 24, 0xff0000, 0xff00, 0xff );
        SampleModel sampleModel = colormodel.createCompatibleSampleModel( w, h );
        DataBufferInt data = new DataBufferInt( ib.rgbBuffer, w * h );
        WritableRaster raster = WritableRaster.createWritableRaster( sampleModel, data, new Point( 0, 0 ) );
        return new BufferedImage( colormodel, raster, false, null );
    }

    public void saveToPNG( java.io.File f )
            throws java.io.IOException
    {
        saveToPNG( f, currentSurfaceImage );
    }

    public static void saveToPNG( java.io.File f, ImgBuffer imgbuf )
            throws java.io.IOException
    {
        BufferedImage bufferedImage = createBufferedImageFromRGB( imgbuf );
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -bufferedImage.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bufferedImage = op.filter(bufferedImage, null);
        javax.imageio.ImageIO.write( bufferedImage, "png", f );
    }

    protected void paintComponent( Graphics g )
    {
        super.paintComponent( g );
//        glcanvas.repaint();
        if( true )
            return;
/*
        if( g instanceof Graphics2D )
        {
            final Graphics2D g2 = ( Graphics2D ) g;

            g2.setColor( this.asr.getBackgroundColor().get() );

            // calculate necessary painting information
            BufferedImage bi = this.currentSurfaceImage;
            if( bi == null )
            {
                g2.fillRect( 0, 0, this.getWidth(), this.getHeight() );
                return;
            }

            final Point startPosition;
            final double scale;
            double aspectRatioComponent = this.getWidth() / ( double ) this.getHeight();
            double aspectRatioImage = bi.getWidth() / ( double ) bi.getHeight();
            final Rectangle r1, r2;
            if( aspectRatioImage > aspectRatioComponent )
            {
                // scale image width to component width
                scale = this.getWidth() / ( double ) bi.getWidth();
                int newImageHeight = ( int ) ( bi.getHeight() * scale );
                startPosition = new Point( 0, ( this.getHeight() - newImageHeight ) / 2 );
                r1 = new Rectangle( 0, 0, this.getWidth(), startPosition.y );
                r2 = new Rectangle( 0, startPosition.y + newImageHeight, this.getWidth(), this.getHeight() - newImageHeight );
            }
            else
            {
                // scale image height to component height
                scale = this.getHeight() / ( double ) bi.getHeight();
                int newImageWidth = ( int ) ( bi.getWidth() * scale );
                startPosition = new Point( ( this.getWidth() - newImageWidth ) / 2, 0 );
                r1 = new Rectangle( 0, 0, startPosition.x, this.getHeight() );
                r2 = new Rectangle( startPosition.x + newImageWidth, 0, this.getWidth() - newImageWidth, this.getHeight() );
            }
            final AffineTransform t = new AffineTransform();
            t.scale( scale, scale );

            // fill margins with background color
            g2.fillRect( r1.x, r1.y, r1.width, r1.height );
            g2.fillRect( r2.x, r2.y, r2.width, r2.height );

            // draw the surface image to the component and apply appropriate scaling
            g2.drawImage( bi, new AffineTransformOp( t, AffineTransformOp.TYPE_BILINEAR ), startPosition.x, startPosition.y );
        }
        else
        {
            super.paintComponents( g );
            g.drawString( "this component needs a Graphics2D for painting", 2, this.getHeight() - 2 );
        }
 * */
    }

    protected void scheduleSurfaceRepaint()
    {
        rw.scheduleRepaint();
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
        c.lookAt( new Point3d( 0, 0, cameraDistance ), new Point3d( 0, 0, -1 ), new Vector3d( 0, 1, 0 ) );
    }

    protected void componentResized( ComponentEvent ce )
    {
        rsd.setXSpeed( 180.0f / this.getWidth() );
        rsd.setYSpeed( 180.0f / this.getHeight() );
        scheduleSurfaceRepaint();
        repaint();
    }

    protected void mousePressed( MouseEvent me )
    {
        grabFocus();
        rsd.startDrag( me.getPoint() );
    }

    protected void mouseDragged( MouseEvent me )
    {
        rsd.dragTo( me.getPoint() );
        //drawCoordinatenSystem(true);
        scheduleSurfaceRepaint();
    }

    protected void scaleSurface( int units )
    {

        /*Matrix4f tmp = new Matrix4f();
        tmp.setIdentity();
        tmp.setScale( ( float ) Math.pow( 1.0625, units ) );
        scale.mul( tmp );*/

        this.setScale(this.getScale()-units/50.0 );
        //this.setScale(0);
        scheduleSurfaceRepaint();
    }

    public void loadFromString( String s )
            throws Exception
    {
        Properties props = new Properties();
        props.load( new ByteArrayInputStream( s.getBytes() ) );
        loadFromProperties( props );
    }

    public void loadFromFile( URL url )
            throws IOException, Exception
    {
        Properties props = new Properties();
        props.load( url.openStream() );
        loadFromProperties( props );
    }

    public void loadFromProperties( Properties props )
            throws Exception
    {
        asr.setSurfaceFamily( props.getProperty( "surface_equation" ) );

        Set< Map.Entry< Object, Object > > entries = props.entrySet();
        String parameter_prefix = "surface_parameter_";
        for( Map.Entry< Object, Object > entry : entries )
        {
            String name = (String) entry.getKey();
            if( name.startsWith( parameter_prefix ) )
            {
                String parameterName = name.substring( parameter_prefix.length() );
                asr.setParameterValue( parameterName, Float.parseFloat( ( String ) entry.getValue() ) );
                System.out.println("LoadRenderPar: " + parameterName + "=" + entry.getValue() + " (" + Float.parseFloat( (String) entry.getValue()) + ") "+ asr.getParameterValue( parameterName));
            }
        }

        asr.getCamera().loadProperties( props, "camera_", "" );
        asr.getFrontMaterial().loadProperties(props, "front_material_", "");
        asr.getBackMaterial().loadProperties(props, "back_material_", "");
        for( int i = 0; i < asr.MAX_LIGHTS; i++ )
        {
            asr.getLightSource( i ).setStatus(LightSource.Status.OFF);
            asr.getLightSource( i ).loadProperties( props, "light_", "_" + i );
        }
        asr.setBackgroundColor( BasicIO.fromColor3fString( props.getProperty( "background_color" ) ) );
        this.setScale( Float.parseFloat( props.getProperty( "scale_factor" ) ) );
        rsd.setRotation( BasicIO.fromMatrix4dString( props.getProperty( "rotation_matrix" ) ) );
    }

    public void saveToFile( URL url )
            throws IOException
    {
        Properties props = new Properties();
        props.setProperty( "surface_equation", asr.getSurfaceFamilyString() );

        Set< String > paramNames = asr.getAllParameterNames();
        for( String paramName : paramNames )
        {
            try
            {
                props.setProperty( "surface_parameter_" + paramName, "" + asr.getParameterValue( paramName ) );
            }
            catch( Exception e ) {}
        }

        asr.getCamera().saveProperties( props, "camera_", "" );
        asr.getFrontMaterial().saveProperties(props, "front_material_", "");
        asr.getBackMaterial().saveProperties(props, "back_material_", "");
        for( int i = 0; i < asr.MAX_LIGHTS; i++ )
            asr.getLightSource( i ).saveProperties( props, "light_", "_" + i );
        props.setProperty( "background_color", BasicIO.toString( asr.getBackgroundColor() ) );

        props.setProperty( "scale_factor", ""+this.getScale() );
        props.setProperty( "rotation_matrix", BasicIO.toString( rsd.getRotation() ));

        File property_file = new File( url.getFile() );
        props.store( new FileOutputStream( property_file ), "jSurfer surface description" );
    }
    public void drawCoordinatenSystem(boolean b)
    {
        renderCoordinatenSystem=b;
    }
    public static void generateGalleryThumbnails( String jsurf_folder, String png_folder )
    {
        JSurferRenderPanel p = new JSurferRenderPanel();
        synchronized( p.asr )
        {
            try
            {
                new File( png_folder ).mkdir();
                JSurferRenderPanel.ImgBuffer ib = p.new ImgBuffer( 120, 120 );

                String[] jsurf_dir_content = new File( jsurf_folder ).list();
                if( jsurf_dir_content == null )
                        System.err.println( new File( jsurf_folder ) + " does not exist or is not a directory" );
                for( String filename : jsurf_dir_content )
                {
                    if( filename.endsWith( ".jsurf" ) )
                    {
                        String key = filename.substring( 0, filename.length() - 6 );
                        File jsurf_file = new File( jsurf_folder + File.separator + filename );
                        File png_file = new File( png_folder + File.separator + key + "_icon.png" );
                        System.out.print( "generating thumbnail for " + jsurf_file + " at " + png_file );
                        p.loadFromFile( jsurf_file.getAbsoluteFile().toURL() );

                        // do rendering
                        p.asr.setTransform( p.rsd.getRotation() );
                        p.asr.setSurfaceTransform( p.scale );
                        p.asr.setAntiAliasingMode( CPUAlgebraicSurfaceRenderer.AntiAliasingMode.ADAPTIVE_SUPERSAMPLING );
                        p.asr.setAntiAliasingPattern( AntiAliasingPattern.RG_2x2 );

                        p.asr.draw( ib.rgbBuffer, ib.width, ib.height );

                        saveToPNG( png_file, ib );
                        System.out.println( " ... done" );
                    }
                }
            }
            catch( Throwable t )
            {
                System.err.println( t );
                t.printStackTrace( System.err );
            }
        }
        System.exit( 0 );
    }

    public static void main( String[]args )
    {
        generateGalleryThumbnails( "./src/de/mfo/jsurfer/gui/gallery", "/home/stussak/Desktop/JFXSurferGalleryThumbnails" );
        if( true ) return;
        JSurferRenderPanel p = new JSurferRenderPanel();
        //p.setResizeImageWithComponent( true );

        try
        {
            p.getAlgebraicSurfaceRenderer().setSurfaceFamily( "x^2+y^2+z^2+2*x*y*z-1" );
            p.setScale( 1.025 );
            de.mfo.jsurfer.algebra.XYZPolynomial poly = p.getAlgebraicSurfaceRenderer().getSurfaceFamily().accept( new de.mfo.jsurfer.algebra.Expand(), null );
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
