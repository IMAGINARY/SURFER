/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mfo.jsurfer.rendering;

import javax.vecmath.*;
import java.util.Properties;
import de.mfo.jsurfer.util.BasicIO;

public class Material {

    private Color3f color;
    private float ambientIntensity;
    private float diffuseIntensity;
    private float specularIntensity;
    private float shininess;

    public Material() {
        this.color = new Color3f( 0.5f, 0.5f, 0.5f );
        this.ambientIntensity = 0.1f;
        this.diffuseIntensity = 0.23232f;
        this.specularIntensity = 0.9f;    
        this.shininess = 1.0f;
    }

    public Color3f getColor() {
        return color;
    }

    public void setColor(Color3f color)
            throws NullPointerException {
        if (color == null) {
            throw new NullPointerException();
        }
        this.color = color;
    }

    public float getAmbientIntensity() {
        return this.ambientIntensity;
    }

    public void setAmbientIntensity(float intensity) {
        this.ambientIntensity = intensity;
    }

    public float getDiffuseIntensity() {
        return this.diffuseIntensity;
    }

    public void setDiffuseIntensity(float intensity) {
        this.diffuseIntensity = intensity;
    }

    public float getSpecularIntensity() {
        return specularIntensity;
    }

    public void setSpecularIntensity(float specularIntensity) {
        this.specularIntensity = specularIntensity;
    }
    
    public float getShininess() {
        return this.shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public Properties saveProperties( Properties props, String prefix, String suffix )
    {
        props.setProperty( prefix + "color" + suffix, BasicIO.toString( color ) );
        props.setProperty( prefix + "ambient_intensity" + suffix, "" + ambientIntensity );
        props.setProperty( prefix + "diffuse_intensity" + suffix, "" + diffuseIntensity );
        props.setProperty( prefix + "specular_intensity" + suffix, "" + specularIntensity );
        props.setProperty( prefix + "shininess" + suffix, "" + shininess );
        return props;
    }

    public void loadProperties( Properties props, String prefix, String suffix )
    {
        String color_key = prefix + "color" + suffix;
        if( props.containsKey( color_key ) )
            color = BasicIO.fromColor3fString( props.getProperty( color_key ) );

        String ambient_intensity_key = prefix + "ambient_intensity" + suffix;
        if( props.containsKey( ambient_intensity_key ) )
            ambientIntensity = Float.parseFloat( props.getProperty( ambient_intensity_key ) );

        String diffuse_intensity_key = prefix + "diffuse_intensity" + suffix;
        if( props.containsKey( diffuse_intensity_key ) )
            diffuseIntensity = Float.parseFloat( props.getProperty( diffuse_intensity_key ) );

        String specular_intensity_key = prefix + "specular_intensity" + suffix;
        if( props.containsKey( specular_intensity_key ) )
            specularIntensity = Float.parseFloat( props.getProperty( specular_intensity_key ) );

        String shininess_key = prefix + "shininess" + suffix;
        if( props.containsKey( shininess_key ) )
            shininess = Float.parseFloat( props.getProperty( shininess_key ) );
    }
}
