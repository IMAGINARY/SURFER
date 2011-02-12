/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mfo.jsurfer.rendering;

import javax.vecmath.*;

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
}
