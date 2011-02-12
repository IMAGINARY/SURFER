/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public final class Helper
{
    public static double pow( double base, int exp )
    {
        double result = 1.0;
        while( exp > 0 )
        {
            if( ( exp & 1 ) == 1 )
            {
                result = result * base;
                exp--;
            }
            base = base * base;
            exp /= 2;
        }
        return result;
    }
}
