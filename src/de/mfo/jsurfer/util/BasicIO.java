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

    public static String toString( Tuple3d t )
    {
        return t.x + " " + t.y + " " + t.z;
    }

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

    public static Point3d fromPoint3dString( String s )
    {
        Scanner scanner = new Scanner( s );
        scanner.useLocale(Locale.US);
        Point3d c = new Point3d();
        c.x = scanner.nextDouble();
        c.y = scanner.nextDouble();
        c.z = scanner.nextDouble();
        return c;
    }

    public static Vector3d fromVector3dString( String s )
    {
        return new Vector3d( fromPoint3dString( s ) );
    }    

    public static String toString( Matrix4d m )
    {
        return m.m00 + " " + m.m01 + " " + m.m02 + " " + m.m03 + " " +
                m.m10 + " " + m.m11 + " " + m.m12 + " " + m.m13 + " " +
                m.m20 + " " + m.m21 + " " + m.m22 + " " + m.m23 + " " +
                m.m30 + " " + m.m31 + " " + m.m32 + " " + m.m33;
    }

    public static Matrix4d fromMatrix4dString( String s )
    {
        Scanner scanner = new Scanner( s );
        scanner.useLocale(Locale.US);
        Matrix4d m = new Matrix4d();
        m.m00 = scanner.nextDouble();
        m.m01 = scanner.nextDouble();
        m.m02 = scanner.nextDouble();
        m.m03 = scanner.nextDouble();
        m.m10 = scanner.nextDouble();
        m.m11 = scanner.nextDouble();
        m.m12 = scanner.nextDouble();
        m.m13 = scanner.nextDouble();
        m.m20 = scanner.nextDouble();
        m.m21 = scanner.nextDouble();
        m.m22 = scanner.nextDouble();
        m.m23 = scanner.nextDouble();
        m.m30 = scanner.nextDouble();
        m.m31 = scanner.nextDouble();
        m.m32 = scanner.nextDouble();
        m.m33 = scanner.nextDouble();
        return m;
    }

}
