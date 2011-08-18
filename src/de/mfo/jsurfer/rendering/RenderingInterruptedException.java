/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.rendering;

/**
 *
 * @author stussak
 */
public class RenderingInterruptedException extends RuntimeException
{
    public RenderingInterruptedException() { super(); }
    public RenderingInterruptedException( String message ) { super( message ); }
    public RenderingInterruptedException( String message, Throwable cause ) { super( message, cause ); }
    public RenderingInterruptedException( Throwable cause ) { super( cause ); }
}
