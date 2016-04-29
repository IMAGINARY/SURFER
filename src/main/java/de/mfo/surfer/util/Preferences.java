package de.mfo.surfer.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Preferences
{
    public static class PropertyTypeTest
    {
        static
        {
            doubleTest = new SimpleDoubleProperty();
            booleanTest = new SimpleBooleanProperty();
        }

        private static SimpleDoubleProperty doubleTest;
        public static DoubleProperty doubleTestProperty()
        {
            return doubleTest;
        }

        private static SimpleBooleanProperty booleanTest;
        public static BooleanProperty booleanTestProperty()
        {
            return booleanTest;
        }
    }
    
    public static class Limits
    {
        static
        {
            minScaleFactor = new SimpleDoubleProperty( -2.0 );
            maxScaleFactor = new SimpleDoubleProperty( 2.0 );
        }

        private static SimpleDoubleProperty minScaleFactor;
        public static double getMinScaleFactor()
        {
            return minScaleFactor.get();
        }
        public static void setMinScaleFactor( double value )
        {
            minScaleFactor.set( value );
        }
        public static DoubleProperty minScaleFactorProperty()
        {
            return minScaleFactor;
        }

        private static SimpleDoubleProperty maxScaleFactor;
        public static double getMaxScaleFactor()
        {
            return maxScaleFactor.get();
        }
        public static void setMaxScaleFactor( double value )
        {
            maxScaleFactor.set( value );
        }
        public static DoubleProperty maxScaleFactorProperty()
        {
            return maxScaleFactor;
        }
    }
}
