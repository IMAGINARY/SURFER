/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public interface ColumnSubstitutor {
    public UnivariatePolynomial setU( double u );
    public double getV();
}
