package de.mfo.surfer.control;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ColorPickerPanel extends VBox
{
    CustomColorPicker frontColor;
    CustomColorPicker backColor;

    public ColorPickerPanel()
    {
        super();
        setAlignment( Pos.CENTER );

        frontColor = new CustomColorPicker();
        backColor = new CustomColorPicker();

        Region spring0 = new Region();
        Region spring1 = new Region();
        Region spring2 = new Region();

        VBox.setVgrow( spring0, Priority.ALWAYS );
        VBox.setVgrow( spring1, Priority.ALWAYS );
        VBox.setVgrow( spring2, Priority.ALWAYS );

        getChildren().addAll( spring0, frontColor, spring1, backColor, spring2 );
    }

    public Color getFrontColor()
    {
        return frontColor.getCustomColor();
    }

    public void setFrontColor( Color value )
    {
        frontColor.setCustomColor( value );
    }

    public ObjectProperty< Color > frontColorProperty()
    {
        return frontColor.customColorProperty();
    }

    public Color getbackColor()
    {
        return backColor.getCustomColor();
    }

    public void setBackColor( Color value )
    {
        backColor.setCustomColor( value );
    }

    public ObjectProperty< Color > backColorProperty()
    {
        return backColor.customColorProperty();
    }
}
