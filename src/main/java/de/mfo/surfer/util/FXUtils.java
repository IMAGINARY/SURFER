package de.mfo.surfer.util;

import javafx.geometry.Bounds;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Region;
import javafx.scene.Node;

public class FXUtils
{
    private static Effect grayscaleAndBlur;
    static {
        ColorAdjust grayscale = new ColorAdjust();
        grayscale.setSaturation( -0.75 );
        grayscale.setInput( new GaussianBlur() );
        grayscaleAndBlur = grayscale;
    }

    public static void resizeTo( Region target, Node source )
    {
        resizeTo( target, source.getBoundsInParent() );
    }
    public static void relocateTo( Region target, Node source )
    {
        relocateTo( target, source.getBoundsInParent() );
    }

    public static void resizeRelocateTo( Region target, Node source )
    {
        resizeRelocateTo( target, source.getBoundsInParent() );
    }

    public static void resizeTo( Region target, Bounds source )
    {
        target.setMinWidth( source.getWidth() );
        target.setMaxWidth( source.getWidth() );
        target.setMinHeight( source.getHeight() );
        target.setMaxHeight( source.getHeight() );
    }

    public static void relocateTo( Region target, Bounds source )
    {
        target.relocate( source.getMinX(), source.getMinY() );
    }

    public static void resizeRelocateTo( Region target, Bounds source )
    {
        resizeTo( target, source );
        relocateTo( target, source );
    }

    public static < N extends Node > N setVisible( N node, boolean visible )
    {
        node.setVisible( visible );
        return node;
    }

    public static Effect getEffectForDisabledNodes()
    {
        return grayscaleAndBlur;
    }
}
