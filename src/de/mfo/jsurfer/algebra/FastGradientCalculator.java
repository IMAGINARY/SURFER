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
public class FastGradientCalculator implements GradientCalculator
{
    private XYZPolynomial gradientXPoly;
    private XYZPolynomial gradientYPoly;
    private XYZPolynomial gradientZPoly;
    
    public FastGradientCalculator( PolynomialOperation gradientXExpression, PolynomialOperation gradientYExpression, PolynomialOperation gradientZExpression )
    {
        Expand e = new Expand();
        
        this.gradientXPoly = gradientXExpression.accept( e, ( Void ) null );
        this.gradientYPoly = gradientYExpression.accept( e, ( Void ) null );
        this.gradientZPoly = gradientZExpression.accept( e, ( Void ) null );
    }
    
    public Vector3d calculateGradient( Point3d p )
    {
        double x = gradientXPoly.evaluateAt( p.x, p.y, p.z );
        double y = gradientYPoly.evaluateAt( p.x, p.y, p.z );
        double z = gradientZPoly.evaluateAt( p.x, p.y, p.z );

        return new Vector3d( x, y, z );
    }
    
    public Vector3f calculateGradient( Point3f p )
    {
        Vector3d g = calculateGradient( new Point3d( p.x, p.y, p.z ) );
        return new Vector3f( ( float ) g.x, ( float ) g.y, ( float ) g.z );
    }
}
