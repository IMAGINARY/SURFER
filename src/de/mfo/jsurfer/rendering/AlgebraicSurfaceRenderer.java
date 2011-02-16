/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.rendering;

import de.mfo.jsurfer.algebra.*;
import de.mfo.jsurfer.util.BasicIO;
import de.mfo.jsurfer.parser.*;
import javax.vecmath.*;
import java.util.*;

// input/output
import java.net.URL;
import java.util.Properties;
import java.io.*;



/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public abstract class AlgebraicSurfaceRenderer
{
    
    public static final int MAX_LIGHTS = 8;

    private String surfaceExpressionFamilyString;
    private PolynomialOperation surfaceExpressionFamily;

    private PolynomialOperation surfaceExpression;
    private PolynomialOperation gradientXExpression;
    private PolynomialOperation gradientYExpression;
    private PolynomialOperation gradientZExpression;

    private Simplificator parameterSubstitutor;
    
    private Camera camera;
    private Material frontMaterial;
    private Material backMaterial;
    private LightSource[] lightSources;
    private Matrix4f transform;
    private Matrix4f surfaceTransform;
    private Color3f backgroundColor;
    
    public AlgebraicSurfaceRenderer()
    {
        this.parameterSubstitutor = new Simplificator();
        this.camera = new Camera();
        this.frontMaterial = new Material();
        this.backMaterial = new Material();
        this.lightSources = new LightSource[ MAX_LIGHTS ];
        this.lightSources[ 0 ] = new LightSource();
        for( int i = 1; i < this.lightSources.length; i++ )
        {
            this.lightSources[ i ] = new LightSource();
            this.lightSources[ i ].setStatus(LightSource.Status.OFF);
        }
        this.transform = new Matrix4f();
        this.transform.setIdentity();
        this.surfaceTransform = new Matrix4f();
        this.surfaceTransform.setIdentity();
        this.backgroundColor = new Color3f( 1.0f, 1.0f, 1.0f );

        this.setSurfaceFamily( new PolynomialVariable( PolynomialVariable.Var.z ) );
    }
    
    public abstract void draw( int[] colorBuffer, int width, int height );
    
    @Deprecated
    public void setSurfaceExpression( PolynomialOperation expression )
    {
        this.setSurfaceFamily( expression );
    }

    private void setSurfaceFamily( PolynomialOperation expression, String expressionString )
    {
        this.surfaceExpressionFamily = expression;
        this.surfaceExpressionFamilyString = expressionString;
        this.clearExpressionCache();
        this.parameterSubstitutor = new Simplificator(); // forget about old values of parameters
    }

    public void setSurfaceFamily( PolynomialOperation expression )
    {
        setSurfaceFamily( expression, expression.accept( new ToStringVisitor(), (Void) null) );
    }

    public void setSurfaceFamily( String expression )
            throws Exception
    {
        setSurfaceFamily( AlgebraicExpressionParser.parse( expression ), expression );
    }
    
    private void clearExpressionCache()
    {
        this.surfaceExpression = null; // clear cache version of concrete surface expression, where all parameters have been set
        this.gradientXExpression = null;
        this.gradientYExpression = null;
        this.gradientZExpression = null;        
    }

    public PolynomialOperation getSurfaceFamily()
    {
        return this.surfaceExpressionFamily;
    }

    public String getSurfaceFamilyString()
    {
        return this.surfaceExpressionFamilyString;
    }

    public PolynomialOperation getSurfaceExpression()
    {
        if( this.surfaceExpression == null )
            this.surfaceExpression = this.surfaceExpressionFamily.accept( parameterSubstitutor, ( Void ) null );
        return this.surfaceExpression;
    }
    
    public PolynomialOperation getGradientXExpression()
    {
        if( this.gradientXExpression == null )
            this.gradientXExpression = getSurfaceExpression().accept( new Differentiator( PolynomialVariable.Var.x ), ( Void ) null );
        return this.gradientXExpression;
    }
    
    public PolynomialOperation getGradientYExpression()
    {
        if( this.gradientYExpression == null )
            this.gradientYExpression = getSurfaceExpression().accept( new Differentiator( PolynomialVariable.Var.y ), ( Void ) null );
        return this.gradientYExpression;
    }
    
    public PolynomialOperation getGradientZExpression()
    {
        if( this.gradientZExpression == null )
            this.gradientZExpression = getSurfaceExpression().accept( new Differentiator( PolynomialVariable.Var.z ), ( Void ) null );
        return this.gradientZExpression;
    }
    
    public void setParameterValue( String name, double value )
    {
        this.parameterSubstitutor.setParameterValue( name, value );
        clearExpressionCache();
    }
    
    public void unsetParameter( String name )
    {
        this.parameterSubstitutor.unsetParameterValue(name);
        clearExpressionCache();
    }
    
    public double getParameterValue( String name )
    {
        return this.parameterSubstitutor.getParameterValue( name );
    }
    
    public Set< Map.Entry< String, Double > > getAssignedParameters()
    {
        return this.parameterSubstitutor.getKnownParameters();
    }

    public Set< String > getAllParameterNames()
    {
        return this.surfaceExpressionFamily.accept( new DoubleVariableExtractor(), ( Void ) null );
    }
    
    public void setCamera( Camera camera )
            throws NullPointerException
    {
        if( camera == null )
            throw new NullPointerException();
        this.camera = camera;
    }
    
    public Camera getCamera()
    {
        return this.camera;
    }
    
    public void setTransform( Matrix4f m )
            throws NullPointerException
    {
        if( m == null )
            throw new NullPointerException();
        this.transform = new Matrix4f( m );
    }
    
    public Matrix4f getTransform()
    {
        return new Matrix4f( this.transform );
    }
   
    public void setSurfaceTransform( Matrix4f m )
            throws NullPointerException
    {
        if( m == null )
            throw new NullPointerException();
        this.surfaceTransform =  new Matrix4f( m );
    }
    
    public Matrix4f getSurfaceTransform()
    {
        return new Matrix4f( this.surfaceTransform );
    }
    
    /**
     * Set light source number @code{which}. If @code{which >= }@link{MAX_LIGHTS}
     * or @code{which < 0}, then nothing is done. Using @code{null} disables
     * a light source.
     * @param which
     * @param s
     */
    public void setLightSource( int which, LightSource s )
    {
        if( 0 <= which && which < MAX_LIGHTS )
            this.lightSources[ which ] = s;
    }

    /**
     * Returns the light source associated with index @code{which}.
     * The result may be @code{null}.
     * @param which
     * @return The light source associated with index @code{which}. May be @code{null}.
     */
    public LightSource getLightSource( int which )
    {
        if( 0 <= which && which < MAX_LIGHTS )
            return this.lightSources[ which ];
        else
            return null;
    }
    
    public void setFrontMaterial( Material m )
            throws NullPointerException
    {
        if( m == null )
            throw new NullPointerException();
        this.frontMaterial = m;
    }
    
    public Material getFrontMaterial()
    {
        return this.frontMaterial;
    }    
            
    public void setBackMaterial( Material m )
            throws NullPointerException
    {
        if( m == null )
            throw new NullPointerException();
        this.backMaterial = m;
    }
    
    public Material getBackMaterial()
    {
        return this.backMaterial;
    }
    
    public void setBackgroundColor( Color3f c )
            throws NullPointerException
    {
        if( c == null )
            throw new NullPointerException();
        this.backgroundColor = c;
    }
    
    public Color3f getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public void loadFromFile( URL url )
            throws IOException, Exception
    {
        Properties props = new Properties();
        props.load( url.openStream() );

        this.setSurfaceFamily( props.getProperty( "surface_equation" ) );

        Set< Map.Entry< Object, Object > > entries = props.entrySet();
        String parameter_prefix = "surface_parameter_";
        for( Map.Entry< Object, Object > entry : entries )
        {
            String name = (String) entry.getKey();
            if( name.startsWith( parameter_prefix ) )
            {
                String parameterName = name.substring( parameter_prefix.length() );
                this.setParameterValue( parameterName, Float.parseFloat( ( String ) entry.getValue() ) );
            }
        }

        this.getCamera().loadProperties( props, "camera_", "" );
        this.getFrontMaterial().loadProperties(props, "front_material_", "");
        this.getBackMaterial().loadProperties(props, "back_material_", "");
        for( int i = 0; i < this.MAX_LIGHTS; i++ )
        {
            this.getLightSource( i ).setStatus(LightSource.Status.OFF);
            this.getLightSource( i ).loadProperties( props, "light_", "_" + i );
        }
        this.setTransform( BasicIO.fromMatrix4fString( props.getProperty( "transform" ) ) );
        this.setTransform( BasicIO.fromMatrix4fString( props.getProperty( "surface_transform" ) ) );
        this.setBackgroundColor( BasicIO.fromColor3fString( props.getProperty( "background_color" ) ) );
    }

    public void saveToFile( URL url )
            throws IOException
    {
        Properties props = new Properties();
        props.setProperty( "surface_equation", getSurfaceFamilyString() );
        
        Set< String > paramNames = getAllParameterNames();
        for( String paramName : paramNames )
        {
            try
            {
                props.setProperty( "surface_parameter_" + paramName, "" + this.getParameterValue( paramName ) );
            }
            catch( Exception e ) {}
        }
        
        this.getCamera().saveProperties( props, "camera_", "" );
        this.getFrontMaterial().saveProperties(props, "front_material_", "");
        this.getBackMaterial().saveProperties(props, "back_material_", "");
        for( int i = 0; i < this.MAX_LIGHTS; i++ )
            this.getLightSource( i ).saveProperties( props, "light_", "_" + i );
        props.setProperty( "transform", BasicIO.toString( this.getTransform() ) );
        props.setProperty( "surface_transform", BasicIO.toString( this.getSurfaceTransform() ) );
        props.setProperty( "background_color", BasicIO.toString( this.getBackgroundColor() ) );

        File property_file = new File( url.getFile() );
        props.store( new FileOutputStream( property_file ), "jSurfer surface description" );
    }
}
