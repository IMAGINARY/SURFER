package de.mfo.surfer.control;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ColorPickerPanel extends VBox
{
    ColorPicker frontColor;
    ColorPicker backColor;

    public ColorPickerPanel()
    {
        super();
        setAlignment( Pos.CENTER );

        frontColor = new ColorPicker();
        backColor = new ColorPicker();

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
        return frontColor.getValue();
    }

    public void setFrontColor( Color value )
    {
        frontColor.setValue( value );
    }

    public ObjectProperty< Color > frontColorProperty()
    {
        return frontColor.valueProperty();
    }

    public Color getbackColor()
    {
        return backColor.getValue();
    }

    public void setBackColor( Color value )
    {
        backColor.setValue( value );
    }

    public ObjectProperty< Color > backColorProperty()
    {
        return backColor.valueProperty();
    }
}
