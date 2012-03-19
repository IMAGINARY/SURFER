/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

import de.mfo.jsurfer.rendering.*;
import de.mfo.jsurfer.algebra.*;
import de.mfo.jsurfer.parser.AlgebraicExpressionParser;

import javax.swing.JApplet;
import javax.vecmath.Color3f;
import java.awt.Color;

/**
 *
 * @author stussak
 */
public class JSurferRenderPanelApplet extends JApplet
{
    JSurferRenderPanel jsrp;
    String cayley_cubic_jsurf = "#jSurfer surface description\n#Fri Jul 08 16:40:21 CEST 2011\nfront_material_specular_iIntensity=0.5\ncamera_type=ORTHOGRAPHIC_CAMERA\nback_material_specular_iIntensity=0.5\nsurface_parameter_b=0.5\nsurface_parameter_a=0.5\nlight_position_7=0.0 0.0 0.0\nlight_position_6=0.0 0.0 0.0\nlight_position_5=0.0 0.0 0.0\nlight_position_4=0.0 0.0 0.0\nlight_position_3=0.0 0.0 0.0\nlight_position_2=0.0 -100.0 100.0\ncamera_fov_y=60.0\nlight_position_1=100.0 100.0 100.0\nlight_position_0=-100.0 100.0 100.0\nfront_material_color=0.7058824 0.47843137 0.45882353\nbackground_color=1.0 1.0 1.0\nlight_intensity_7=1.0\nfront_material_ambient_intensity=0.4\nlight_intensity_6=1.0\nscale_factor=0.65999985\nlight_intensity_5=1.0\nlight_intensity_4=1.0\nlight_intensity_3=1.0\nlight_intensity_2=0.3\nlight_intensity_1=0.7\nlight_intensity_0=0.5\nrotation_matrix=0.26447517 -0.47794443 -0.8376317 0.0 0.5578079 -0.6327149 0.5371454 0.0 -0.78670573 -0.60929716 0.0992635 0.0 0.0 0.0 0.0 1.0\nlight_color_7=1.0 1.0 1.0\nlight_color_6=1.0 1.0 1.0\nfront_material_diffuse_intensity=0.8\nlight_color_5=1.0 1.0 1.0\nlight_color_4=1.0 1.0 1.0\nlight_color_3=1.0 1.0 1.0\nlight_color_2=1.0 1.0 1.0\nlight_color_1=1.0 1.0 1.0\nlight_color_0=1.0 1.0 1.0\nlight_status_7=OFF\nlight_status_6=OFF\nlight_status_5=OFF\nlight_status_4=OFF\nfront_material_shininess=30.0\nlight_status_3=OFF\nlight_status_2=ON\nlight_status_1=ON\nlight_status_0=ON\ncamera_height=2.0\nsurface_equation=(b-0.5)*(x+y+z-1)^2+(a-0.5)*(x+y+z+3)^2+x^3+y^3+z^3+1-0.25*(x+y+z+1)^3\ncamera_transform=1.0 0.0 0.0 -0.0 0.0 1.0 0.0 -0.0 0.0 0.0 1.0 -1.0 0.0 0.0 0.0 1.0\nback_material_ambient_intensity=0.4\nback_material_color=0.8392157 0.06666667 0.24705882\nback_material_shininess=30.0\nback_material_diffuse_intensity=0.8";

    public void init() {
        super.init();
        javax.swing.SwingUtilities.invokeLater( new Runnable()
                                            {
                                                public void run()
                                                {
                                                    jsrp = new JSurferRenderPanel();
                                                    jsrp.drawCoordinatenSystem( true );
                                                    try { jsrp.loadFromString( cayley_cubic_jsurf ); } catch( Exception e ) { e.printStackTrace(); }
                                                    getContentPane().add( jsrp );
                                                }
                                            } );
    }

    // returns degree of the surface; -1 if equation contains errors
    public int setEquation( String s )
    {
        try
        {
            // remove whitespace
            s = s.replaceAll( "\\p{Space}", "" );
            // remove leading "0=" and trailing "=0"
            s = s.replaceFirst( "^0=", "" );
            s = s.replaceFirst( "=0$", "" );

            // parse
            jsrp.getAlgebraicSurfaceRenderer().setSurfaceFamily( s );
            jsrp.scheduleSurfaceRepaint();

            return jsrp.getAlgebraicSurfaceRenderer().getSurfaceTotalDegree();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean setParameter( String paramName, double value )
    {
        boolean validParam = jsrp.getAlgebraicSurfaceRenderer().getAllParameterNames().contains( paramName );
        if( validParam )
            jsrp.getAlgebraicSurfaceRenderer().setParameterValue( paramName, value );
        jsrp.scheduleSurfaceRepaint();
        return validParam;
    }

    public String getParameterNamesCSV()
    {
        String paramNames = jsrp.getAlgebraicSurfaceRenderer().getAllParameterNames().toString();
        paramNames = paramNames.replaceAll( "[\\[\\]]", "" );
        paramNames = paramNames.replaceAll( ", ", "," );
        return paramNames;
    }

    public void setFrontColor( byte r, byte g, byte b )
    {
        setFrontColor( new Color3f( new Color( r, g, b ) ) );
    }

    public void setFrontColor( String htmlColor )
    {
        setFrontColor( new Color3f( htmlColorToColor( htmlColor ) ) );
    }

    public void setBackColor( byte r, byte g, byte b )
    {
        setBackColor( new Color3f( new Color( r, g, b ) ) );
    }

    public void setBackColor( String htmlColor )
    {
        setBackColor( new Color3f( htmlColorToColor( htmlColor ) ) );
    }

    public void setBackgroundColor( byte r, byte g, byte b )
    {
        setBackgroundColor( new Color3f( new Color( r, g, b ) ) );
    }

    public void setBackgroundColor( String htmlColor )
    {
        setBackgroundColor( new Color3f( htmlColorToColor( htmlColor ) ) );
    }

    public int loadJSurfFromURL( String url )
    {
        try
        {
            jsrp.loadFromFile( new java.net.URL( url ) );
            jsrp.scheduleSurfaceRepaint();
            return 1;
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return -1;
        }
    }

    public int loadJSurfFromString( String s )
    {
        try
        {
            jsrp.loadFromString( s );
            jsrp.scheduleSurfaceRepaint();
            return 1;
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return -1;
        }
    }

    public void drawCoordinateSystem( boolean draw )
    {
        jsrp.drawCoordinatenSystem( draw );
        jsrp.scheduleSurfaceRepaint();
    }

// protected methods

    protected void setFrontColor( Color3f c )
    {
        Material frontMaterial = jsrp.getAlgebraicSurfaceRenderer().getFrontMaterial();
        frontMaterial.setColor( c );
        jsrp.getAlgebraicSurfaceRenderer().setFrontMaterial(frontMaterial );
        jsrp.scheduleSurfaceRepaint();
    }

    protected void setBackColor( Color3f c )
    {
        Material backMaterial = jsrp.getAlgebraicSurfaceRenderer().getBackMaterial();
        backMaterial.setColor( c );
        jsrp.getAlgebraicSurfaceRenderer().setBackMaterial( backMaterial );
        jsrp.scheduleSurfaceRepaint();
    }

    protected void setBackgroundColor( Color3f c )
    {
        jsrp.getAlgebraicSurfaceRenderer().setBackgroundColor( c );
        jsrp.scheduleSurfaceRepaint();
    }

    protected Color htmlColorToColor( String htmlColor )
    {
        htmlColor = htmlColor.replaceAll( "\\p{Space}", "" );
        if( htmlColor.matches( "^#[0-9a-fA-F]{6}$" ) )
            return new Color( Integer.parseInt( htmlColor.replaceFirst( "#", "" ), 16 ) ); // convert to AWT color
        else
            throw new RuntimeException( "'" + htmlColor + "' is no HTML color (like e.g. #FFFFFF)" );
    }

}
