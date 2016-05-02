package de.mfo.surfer.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Preferences
{
    public static class PropertyTypeTest
    {
        static
        {
            doubleTest = new SimpleDoubleProperty();
            booleanTest = new SimpleBooleanProperty();
            integerTest = new SimpleIntegerProperty();
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

        private static SimpleIntegerProperty integerTest;
        public static IntegerProperty integerTestProperty()
        {
            return integerTest;
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

    public static class General
    {
        static
        {
            exportSize = new SimpleIntegerProperty( 512 );
            jpegQuality = new SimpleIntegerProperty( 85 );
            initiallyOpenedTab = new SimpleIntegerProperty( 2 );
        }

        private static SimpleIntegerProperty exportSize;
        public static int getExportSize()
        {
            return exportSize.get();
        }
        public static void setExportSize( int value )
        {
            exportSize.set( value );
        }
        public static IntegerProperty exportSizeProperty()
        {
            return exportSize;
        }

        private static SimpleIntegerProperty jpegQuality;
        public static int getJpegQuality()
        {
            return jpegQuality.get();
        }
        public static void setJpegQuality( int value )
        {
            jpegQuality.set( value );
        }
        public static IntegerProperty jpegQualityProperty()
        {
            return jpegQuality;
        }

        private static SimpleIntegerProperty initiallyOpenedTab;
        public static int getInitiallyOpenedTab()
        {
            return initiallyOpenedTab.get();
        }
        public static void setiInitiallyOpenedTab( int value )
        {
            initiallyOpenedTab.set( value );
        }
        public static IntegerProperty initiallyOpenedTabProperty()
        {
            return initiallyOpenedTab;
        }
    }
}
