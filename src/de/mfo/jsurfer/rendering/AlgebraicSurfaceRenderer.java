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


/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public abstract class AlgebraicSurfaceRenderer
{

    public static final int MAX_LIGHTS = 8;

    private String surfaceExpressionFamilyString;
    private PolynomialOperation surfaceExpressionFamily;
    private int surfaceTotalDegree;

    private PolynomialOperation surfaceExpression;
    private PolynomialOperation gradientXExpression;
    private PolynomialOperation gradientYExpression;
    private PolynomialOperation gradientZExpression;

    private Simplificator parameterSubstitutor;

    private Camera camera;
    private Material frontMaterial;
    private Material backMaterial;
    private LightSource[] lightSources;
    private Matrix4d transform;
    private Matrix4d surfaceTransform;
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
        this.transform = new Matrix4d();
        this.transform.setIdentity();
        this.surfaceTransform = new Matrix4d();
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
        this.surfaceTotalDegree = this.surfaceExpressionFamily.accept( new DegreeCalculator(), ( Void ) null );
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

    public int getSurfaceTotalDegree()
    {
        return this.surfaceTotalDegree;
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

    public Set< Map.Entry< String, java.lang.Double > > getAssignedParameters()
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

    public void setTransform( Matrix4d m )
            throws NullPointerException
    {
        if( m == null )
            throw new NullPointerException();
        this.transform = new Matrix4d( m );
    }

    public Matrix4d getTransform()
    {
        return new Matrix4d( this.transform );
    }

    public void setSurfaceTransform( Matrix4d m )
            throws NullPointerException
    {
        if( m == null )
            throw new NullPointerException();
        this.surfaceTransform =  new Matrix4d( m );
    }

    public Matrix4d getSurfaceTransform()
    {
        return new Matrix4d( this.surfaceTransform );
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
}
