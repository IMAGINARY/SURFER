package de.mfo.surfer.util;

import javafx.beans.property.*;

import java.io.File;

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
            printTemplateFile = new SimpleObjectProperty<File>( );
        }

        private static SimpleIntegerProperty exportSize;
        public static IntegerProperty exportSizeProperty()
        {
            return exportSize;
        }

        private static SimpleIntegerProperty jpegQuality;
        public static IntegerProperty jpegQualityProperty()
        {
            return jpegQuality;
        }

        private static SimpleIntegerProperty initiallyOpenedTab;
        public static IntegerProperty initiallyOpenedTabProperty()
        {
            return initiallyOpenedTab;
        }

        private static SimpleObjectProperty<File> printTemplateFile;
        public static SimpleObjectProperty<File> printTemplateFileProperty()
        {
            return printTemplateFile;
        }
    }
}
