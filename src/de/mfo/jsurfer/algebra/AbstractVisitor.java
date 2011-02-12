/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public abstract class AbstractVisitor< RETURN_TYPE, PARAM_TYPE > implements Visitor< RETURN_TYPE, PARAM_TYPE >
{
    public RETURN_TYPE visit( PolynomialOperation pop, PARAM_TYPE param ) { throw new UnsupportedOperationException(); }
    public RETURN_TYPE visit( DoubleOperation dop, PARAM_TYPE param ) { throw new UnsupportedOperationException(); }
}
