/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.util;

import java.util.Locale;
import java.util.Scanner;
import javax.vecmath.*;

/**
 *
 * @author stussak
 */
public class BasicIO {

    public static String toString( Tuple3f t )
    {
        return t.x + " " + t.y + " " + t.z;
    }

    public static Color3f fromColor3fString( String s )
    {
        Scanner scanner = new Scanner( s );
        scanner.useLocale(Locale.US);
        Color3f c = new Color3f();
        c.x = scanner.nextFloat();
        c.y = scanner.nextFloat();
        c.z = scanner.nextFloat();
        return c;
    }

    public static Point3f fromPoint3fString( String s )
    {
        return new Point3f( fromColor3fString( s ) );        
    }

    public static Vector3f fromVector3fString( String s )
    {
        return new Vector3f( fromColor3fString( s ) );
    }    

    public static String toString( Matrix4f m )
    {
        return m.m00 + " " + m.m01 + " " + m.m02 + " " + m.m03 + " " +
                m.m10 + " " + m.m11 + " " + m.m12 + " " + m.m13 + " " +
                m.m20 + " " + m.m21 + " " + m.m22 + " " + m.m23 + " " +
                m.m30 + " " + m.m31 + " " + m.m32 + " " + m.m33;
    }

    public static Matrix4f fromMatrix4fString( String s )
    {
        Scanner scanner = new Scanner( s );
        scanner.useLocale(Locale.US);
        Matrix4f m = new Matrix4f();
        m.m00 = scanner.nextFloat();
        m.m01 = scanner.nextFloat();
        m.m02 = scanner.nextFloat();
        m.m03 = scanner.nextFloat();
        m.m10 = scanner.nextFloat();
        m.m11 = scanner.nextFloat();
        m.m12 = scanner.nextFloat();
        m.m13 = scanner.nextFloat();
        m.m20 = scanner.nextFloat();
        m.m21 = scanner.nextFloat();
        m.m22 = scanner.nextFloat();
        m.m23 = scanner.nextFloat();
        m.m30 = scanner.nextFloat();
        m.m31 = scanner.nextFloat();
        m.m32 = scanner.nextFloat();
        m.m33 = scanner.nextFloat();
        return m;
    }

}
