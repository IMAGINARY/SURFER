/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

/**
 * @author stussak
 */

public function getOption( name:String, default : String ): String
{
    var r:String = getOption( name );
    if( r == null )
        return default
    else
        return r;
}

public function getOption( name:String ): String
{
    try
    {
        return java.lang.System.getProperty( name );
    }
    catch( e: java.security.AccessControlException )
    {
    }
    return null;
}


public class FXOptions {}