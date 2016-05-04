package de.mfo.surfer.control;

import javafx.scene.control.Label;
import javafx.scene.Node;

public abstract class GalleryIcon extends Label
{
    // to be implemented in subclasses in the gallery package
    public GalleryIcon() { super(); }
    public GalleryIcon( String text ) { super( text ); }
    public GalleryIcon( String text, Node graphic ) { super( text, graphic ); }
}
