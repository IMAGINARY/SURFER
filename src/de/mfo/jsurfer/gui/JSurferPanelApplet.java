/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

import javax.swing.JApplet;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class JSurferPanelApplet extends JApplet
{
    JSurferPanel jsp;
    
    public void init() {
        super.init();
        javax.swing.SwingUtilities.invokeLater( new Runnable()
                                            {
                                                public void run()
                                                {
                                                    jsp = new JSurferPanel( JSurferPanel.Layout.SECOND );
                                                    getContentPane().add( jsp );

                                                }
                                            } );
    }
    
    public void setSurfaceExpression( String s )
    {
        jsp.setSurfaceExpression( s );
    }
}
