/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

import javax.vecmath.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
@Deprecated
public class SimpleGradientCalculator implements GradientCalculator
{
    private PolynomialOperation gradientXExpression;
    private PolynomialOperation gradientYExpression;
    private PolynomialOperation gradientZExpression;
    
    public SimpleGradientCalculator( PolynomialOperation gradientXExpression, PolynomialOperation gradientYExpression, PolynomialOperation gradientZExpression )
    {
        this.gradientXExpression = gradientXExpression;
        this.gradientYExpression = gradientYExpression;
        this.gradientZExpression = gradientZExpression;
    }
    
    public Vector3d calculateGradient( Point3d p )
    {
        ValueCalculator vc = new ValueCalculator( p.x, p.y, p.z );

        double x = gradientXExpression.accept( vc, ( Void ) null );
        double y = gradientYExpression.accept( vc, ( Void ) null );
        double z = gradientZExpression.accept( vc, ( Void ) null );

        return new Vector3d( x, y, z );
    }
    
    public Vector3f calculateGradient( Point3f p )
    {
        Vector3d g = calculateGradient( new Point3d( p.x, p.y, p.z ) );
        return new Vector3f( ( float ) g.x, ( float ) g.y, ( float ) g.z );
    }
}
