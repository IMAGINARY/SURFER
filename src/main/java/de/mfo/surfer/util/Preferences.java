package de.mfo.surfer.util;

import javafx.beans.property.*;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;

// TODO: turn private static ..Property into public static final ..Property = ...
// TODO: replace ..Type() methods with new annotation on property field
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
            degreeLimit = new SimpleIntegerProperty(0);

        }

        private static SimpleDoubleProperty minScaleFactor;
        public static DoubleProperty minScaleFactorProperty()
        {
            return minScaleFactor;
        }

        private static SimpleDoubleProperty maxScaleFactor;
        public static DoubleProperty maxScaleFactorProperty()
        {
            return maxScaleFactor;
        }


        private static SimpleIntegerProperty degreeLimit;
        public static IntegerProperty degreeLimitProperty()
        {
            return degreeLimit;
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
            initialJSurfFile = new SimpleObjectProperty<URL>(Preferences.class.getResource("/de/mfo/surfer/gallery/default.jsurf"));
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
        public static Class<?> printTemplateFileType()
        {
            return File.class;
        }

        private static SimpleObjectProperty<URL> initialJSurfFile;
        public static SimpleObjectProperty<URL> initialJSurfFileProperty()
        {
            return initialJSurfFile;
        }
        public static Class<?> initialJSurfFileType()
        {
            return URL.class;
        }

    }

    public static class Kiosk
    {
        static
        {
            fullScreen = new SimpleBooleanProperty( false );
            hideCursor = new SimpleBooleanProperty( false );
            showPrintButton = new SimpleBooleanProperty( true );
            showLoadButton = new SimpleBooleanProperty( true );
            showSaveButton = new SimpleBooleanProperty( true );
            showExportButton = new SimpleBooleanProperty( true );
            showSettingsButton = new SimpleBooleanProperty( true );
            idleTimeOut = new SimpleObjectProperty<Duration>( Duration.INDEFINITE );
        }

        private static SimpleBooleanProperty fullScreen;
        public static BooleanProperty fullScreenProperty()
        {
            return fullScreen;
        }

        private static SimpleBooleanProperty hideCursor;
        public static BooleanProperty hideCursorProperty()
        {
            return hideCursor;
        }

        private static SimpleBooleanProperty showPrintButton;
        public static BooleanProperty showPrintButtonProperty()
        {
            return showPrintButton;
        }

        private static SimpleBooleanProperty showLoadButton;
        public static BooleanProperty showLoadButtonProperty()
        {
            return showLoadButton;
        }

        private static SimpleBooleanProperty showSaveButton;
        public static BooleanProperty showSaveButtonProperty()
        {
            return showSaveButton;
        }

        private static SimpleBooleanProperty showExportButton;
        public static BooleanProperty showExportButtonProperty()
        {
            return showExportButton;
        }

        private static SimpleBooleanProperty showSettingsButton;
        public static BooleanProperty showSettingsButtonProperty()
        {
            return showSettingsButton;
        }

        private static SimpleObjectProperty<Duration> idleTimeOut;
        public static SimpleObjectProperty<Duration> idleTimeOutProperty() { return idleTimeOut; }
        public static Class<?> idleTimeOutType()
        {
            return Duration.class;
        }
    }

    public static class Developer
    {
        static
        {
            logLevel = new SimpleObjectProperty<>( LogLevel.WARN );
        }

        private static SimpleObjectProperty<LogLevel> logLevel;
        public static SimpleObjectProperty<LogLevel> logLevelProperty()
        {
            return logLevel;
        }
        public static Class<?> logLevelType()
        {
            return LogLevel.class;
        }
    }
}
