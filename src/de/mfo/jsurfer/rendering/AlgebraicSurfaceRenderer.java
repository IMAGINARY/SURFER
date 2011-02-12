/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.rendering;

import de.mfo.jsurfer.algebra.*;
import javax.vecmath.*;
import java.util.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public abstract class AlgebraicSurfaceRenderer
{
    
    public static final int MAX_LIGHTS = 8;

    private PolynomialOperation surfaceExpression;
    private PolynomialOperation gradientXExpression;
    private PolynomialOperation gradientYExpression;
    private PolynomialOperation gradientZExpression;
    private Map< String, Float > parameters;
    
    private Camera camera;
    private Material frontMaterial;
    private Material backMaterial;
    private LightSource[] lightSources;
    private Matrix4f transform;
    private Matrix4f surfaceTransform;
    private Color3f backgroundColor;
    
    public AlgebraicSurfaceRenderer()
    {
        this.surfaceExpression = new PolynomialVariable( PolynomialVariable.Var.z );
        calculateGradientExpression();
        this.parameters = new HashMap< String, Float >();
        this.camera = new Camera();
        this.frontMaterial = new Material();
        this.backMaterial = new Material();
        this.lightSources = new LightSource[ MAX_LIGHTS ];
        this.lightSources[ 0 ] = new LightSource();
        this.transform = new Matrix4f();
        this.transform.setIdentity();
        this.surfaceTransform = new Matrix4f();
        this.surfaceTransform.setIdentity();
        this.backgroundColor = new Color3f( 1.0f, 1.0f, 1.0f );
    }
    
    public abstract void draw( int[] colorBuffer, int width, int height );
    
    public void setSurfaceExpression( PolynomialOperation expression )
    {
        Simplificator simplificator = new Simplificator();

        // assign simplified expression
        this.surfaceExpression = expression.accept( simplificator, ( Void ) null );
        
        // calculate expressions of gradient 
        calculateGradientExpression();
    }
    
    private void calculateGradientExpression()
    {
        Simplificator simplificator = new Simplificator();
        
        // calculate grad_x and simplify
        this.gradientXExpression = this.surfaceExpression.accept( new Differentiator( PolynomialVariable.Var.x ), ( Void ) null );
        this.gradientXExpression = this.gradientXExpression.accept( simplificator, ( Void ) null );

        // calculate grad_y and simplify
        this.gradientYExpression = this.surfaceExpression.accept( new Differentiator( PolynomialVariable.Var.y ), ( Void ) null );
        this.gradientYExpression = this.gradientYExpression.accept( simplificator, ( Void ) null );

        // calculate grad_z and simplify
        this.gradientZExpression = this.surfaceExpression.accept( new Differentiator( PolynomialVariable.Var.z ), ( Void ) null );
        this.gradientZExpression = this.gradientZExpression.accept( simplificator, ( Void ) null );
    }
    
    public PolynomialOperation getSurfaceExpression()
    {
        return this.surfaceExpression;
    }
    
    public PolynomialOperation getGradientXExpression()
    {
        return this.gradientXExpression;
    }
    
    public PolynomialOperation getGradientYExpression()
    {
        return this.gradientYExpression;
    }
    
    public PolynomialOperation getGradientZExpression()
    {
        return this.gradientZExpression;
    }
    
    public void setParameter( String name, float value )
    {
        this.parameters.put( name, value );
    }
    
    public void unsetParameter( String name )
    {
        this.parameters.remove( name );
    }
    
    public void getParameter( String name )
    {
        this.parameters.get( name );
    }
    
    public Set< Map.Entry< String, Float > > getParameters()
    {
        return this.parameters.entrySet();
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
}
