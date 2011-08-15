/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.rendering.cpu;

import de.mfo.jsurfer.algebra.*;
import de.mfo.jsurfer.rendering.*;
import javax.vecmath.*;
import de.mfo.jsurfer.rendering.cpu.clipping.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
class DrawcallStaticData
{
    int[] colorBuffer;
    int width;
    int height;
    
    CoefficientCalculator coefficientCalculator;
    RowSubstitutor surfaceRowSubstitutor;
    RowSubstitutorForGradient gradientRowSubstitutor;
    RealRootFinder realRootFinder;
    
    LightSource[] lightSources;
    Color3f frontAmbientColor;
    Color3f backAmbientColor;
    LightProducts[] frontLightProducts;
    LightProducts[] backLightProducts;
    Color3f backgroundColor;
    
    AntiAliasingPattern antiAliasingPattern;
    float antiAliasingThreshold;
    
    RayCreator rayCreator;
    Clipper rayClipper;
}
