package de.mfo.surfer.control;

import de.mfo.jsurf.rendering.*;
import de.mfo.jsurf.rendering.cpu.*;
import de.mfo.jsurf.util.RotateSphericalDragger;
import de.mfo.jsurf.util.BasicIO;
import de.mfo.jsurf.util.FileFormat;
import static de.mfo.jsurf.rendering.cpu.CPUAlgebraicSurfaceRenderer.AntiAliasingMode;

import java.net.URL;
import java.util.*;
import java.io.*;
import javax.vecmath.*;

import de.mfo.surfer.Main;
import de.mfo.surfer.gallery.GalleryItem;
import de.mfo.surfer.util.FXUtils;
import de.mfo.surfer.util.Preferences;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.Point;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderArea extends Region
{
    private static final Logger logger = LoggerFactory.getLogger( RenderArea.class );

    ImageView imageView;
    SimpleBooleanProperty triggerRepaintOnChange;

    SimpleStringProperty formula;

    ReadOnlyBooleanWrapper isValid;
    BooleanBinding hasNullValues;
    SimpleBooleanProperty isFormulaValid;
    ReadOnlyObjectWrapper< Throwable > error;
    ReadOnlyStringWrapper errorMessage;

    SimpleMapProperty< String, Double > parameters;

    SimpleObjectProperty< Color > frontColor;
    SimpleObjectProperty< Color > backColor;

    CPUAlgebraicSurfaceRendererExt asr;

    SimpleIntegerProperty renderSize;

    ObjectBinding< BoundingBox > targetBoundsBinding;

    RotateSphericalDragger rsd;

    public RenderArea()
    {
        setPickOnBounds( false );

        imageView = new ImageView();
        imageView.setPreserveRatio( true );
        getChildren().add(imageView);

        asr = new CPUAlgebraicSurfaceRendererExt();

        triggerRepaintOnChange = new SimpleBooleanProperty( true );

        formula = new SimpleStringProperty();
        isValid = new ReadOnlyBooleanWrapper();
        error = new ReadOnlyObjectWrapper< Throwable >();
        errorMessage = new ReadOnlyStringWrapper();
        errorMessage.bind(
            Bindings.createStringBinding(
                () ->
                {
                    if( getError() == null )
                    {
                        return "";
                    }
                    else
                    {
                        StringWriter sw = new StringWriter();
                        getError().printStackTrace( new PrintWriter( sw ) );
                        return sw.toString();
                    }
                },
                error
            )
        );

        parameters = new SimpleMapProperty< String, Double >( FXCollections.< String, Double >observableHashMap() );

        frontColor = new SimpleObjectProperty< Color >();
        backColor = new SimpleObjectProperty< Color >();

        renderSize = new SimpleIntegerProperty( 1 );

        hasNullValues = Bindings.isNull( formula );
        hasNullValues = hasNullValues.or( Bindings.isNull( frontColor ) );
        hasNullValues = hasNullValues.or( Bindings.isNull( backColor ) );

        isFormulaValid = new SimpleBooleanProperty();

        isValid.bind( hasNullValues.not().and( isFormulaValid ) );

        imageView.effectProperty().bind( Bindings.createObjectBinding(
            () -> isValid.get() ? null : FXUtils.getEffectForDisabledNodes(),
            isValid
        ) );

        this.disableProperty().bind( isValid.not() );

        ChangeListener cl = ( observable, oldValue, newValue ) -> Platform.runLater( () -> triggerRepaint() );

        formula.addListener( cl );

        parameters.addListener(
            new MapChangeListener< String, Double >()
            {
                List< String > names = Arrays.asList( new String[]{ "a", "b", "c", "d", "scale_factor" } );

                @Override
                public void onChanged( Change<? extends String,? extends Double> change )
                {
                    names.forEach( n -> { if( change.wasAdded() && change.getKey().equals( n ) ) {
                        asr.setParameterValue( n, change.getValueAdded() );
                    } } );
                    Platform.runLater( () -> triggerRepaint() );
                }
            }
        );

        Function< Color, Color3f > c2c3f = c -> new Color3f( ( float ) c.getRed(), ( float ) c.getGreen(), ( float ) c.getBlue() );
        frontColor.addListener( ( p1, p2, newValue ) -> { asr.getFrontMaterial().setColor( c2c3f.apply( newValue ) ); triggerRepaint(); } );
        backColor.addListener( ( p1, p2, newValue ) -> { asr.getBackMaterial().setColor( c2c3f.apply( newValue ) ); triggerRepaint(); } );

        this.sceneProperty().addListener( cl );

        Node renderAreaPlaceholder = Main.< Node >fxmlLookup( "#Surfer_Rendering" );
        renderAreaPlaceholder.setVisible( false );

        renderAreaPlaceholder.localToSceneTransformProperty().addListener( cl );

        targetBoundsBinding = Bindings.createObjectBinding( () -> {
                Bounds boundsInScene = renderAreaPlaceholder.localToSceneTransformProperty().get().transform( renderAreaPlaceholder.boundsInLocalProperty().get() );
                BoundingBox boundsInSceneSnapped = new BoundingBox( Math.round( boundsInScene.getMinX() ),
                    Math.round( boundsInScene.getMinY() ),
                    Math.ceil( boundsInScene.getWidth() ),
                    Math.ceil( boundsInScene.getHeight() )
                );
                return boundsInSceneSnapped;
            },
            renderAreaPlaceholder.boundsInLocalProperty(),
            renderAreaPlaceholder.localToSceneTransformProperty()
        );

        ChangeListener<BoundingBox> l = (o, ov, nv ) -> {
            FXUtils.relocateTo( this, nv );
            this.imageView.setFitWidth( nv.getWidth() );
            this.imageView.setFitHeight( nv.getHeight() );
        };
        l.changed(targetBoundsBinding, targetBoundsBinding.getValue(), targetBoundsBinding.getValue());
        targetBoundsBinding.addListener( l );

        rsd = new RotateSphericalDragger();
        setOnMousePressed( e -> rsd.startDrag( new java.awt.Point( ( int ) e.getX(), ( int ) e.getY() ) ) );
        setOnMouseDragged( e -> { rsd.dragTo( new java.awt.Point( ( int ) e.getX(), ( int ) e.getY() ) ); triggerRepaint(); } );

        Consumer< Function< Double, Double > > changeScale = f ->
        {
            double newScale = f.apply( parameters.get( "scale_factor" ) );
            newScale = newScale < Preferences.Limits.getMinScaleFactor() ? Preferences.Limits.getMinScaleFactor() : ( newScale > Preferences.Limits.getMaxScaleFactor() ? Preferences.Limits.getMaxScaleFactor() : newScale );
            parameters.put( "scale_factor", newScale );
        };
        setOnScroll( e -> changeScale.accept( oldScaleFactor -> oldScaleFactor - ( e.getDeltaX() + e.getDeltaY() ) / imageView.getFitWidth() ) );
        setOnZoom( e -> changeScale.accept( oldScaleFactor -> oldScaleFactor - Math.log10( e.getZoomFactor() ) ) );
    }

    RenderingTask taskLowQuality;
    RenderingTask taskMediumQuality;
    RenderingTask taskHighQuality;
    RenderingTask taskUltraQuality;
    double secondsPerPixel = 0.0001;
    double targetFps = 30.0;
    int minRenderSize = 100;

    ExecutorService executor = Executors.newSingleThreadExecutor( r -> { Thread t = new Thread( r ); t.setDaemon( true ); return t; } );
    void triggerRepaint()
    {
        if( this.getScene() != null && this.getParent() != null && triggerRepaintOnChange.get() )
        {
            if( taskLowQuality != null && !taskLowQuality.isRunning() )
                taskLowQuality.cancel();
            if( taskMediumQuality != null )
                taskMediumQuality.cancel();
            if( taskHighQuality != null )
                taskHighQuality.cancel();
            if( taskUltraQuality != null )
                taskUltraQuality.cancel();

            // set up rendering environemnt
            passDataToASR();
            if( isValid.get() )
            {
                // calculate upper bound of the resolution
                Bounds b = this.localToScene( this.getBoundsInLocal(), true );
                int maxSize = (int) Math.round( Math.max( b.getWidth(), b.getHeight() ) );
                int lowResSize = ( int ) Math.max( Math.min( maxSize, Math.sqrt( 1.0 / ( targetFps * secondsPerPixel ) ) ), 100 );

                taskUltraQuality = new RenderingTask(
                    asr,
                    this.imageView,
                    maxSize,
                    renderSize,
                    AntiAliasingMode.SUPERSAMPLING,
                    AntiAliasingPattern.OG_4x4
                );

                taskHighQuality = new RenderingTask(
                    asr,
                    this.imageView,
                    maxSize,
                    renderSize,
                    AntiAliasingMode.ADAPTIVE_SUPERSAMPLING,
                    AntiAliasingPattern.OG_4x4
                );
                taskHighQuality.setOnSucceeded( e -> executor.submit( taskUltraQuality ) );

                if( lowResSize < maxSize / 2 )
                {
                    // add rendering step with intermediate resolution
                    taskMediumQuality = new RenderingTask(
                        asr,
                        this.imageView,
                        ( maxSize + lowResSize ) / 2,
                        renderSize,
                        AntiAliasingMode.ADAPTIVE_SUPERSAMPLING,
                        AntiAliasingPattern.QUINCUNX
                    );
                }
                else
                {
                    // add dummy rendering task, that does nothing
                    taskMediumQuality = new RenderingTask(
                        asr,
                        this.imageView,
                        ( maxSize + lowResSize ) / 2,
                        renderSize,
                        AntiAliasingMode.ADAPTIVE_SUPERSAMPLING,
                        AntiAliasingPattern.QUINCUNX
                    )
                    {
                        @Override protected void scheduled() {}
                        @Override public Double call() { return secondsPerPixel; }
                        @Override protected void succeeded() {}
                    };
                }
                taskMediumQuality.setOnSucceeded( e -> executor.submit( taskHighQuality ) );

                taskLowQuality = new RenderingTask(
                    asr,
                    this.imageView,
                    lowResSize,
                    renderSize,
                    AntiAliasingMode.ADAPTIVE_SUPERSAMPLING,
                    AntiAliasingPattern.QUINCUNX
                );
                taskLowQuality.setOnSucceeded( e ->
                    {
                        secondsPerPixel = ( double ) e.getSource().getValue();
                        executor.submit( taskMediumQuality );
                    }
                );

                executor.submit( taskLowQuality );
            }
        }
    }

    public void setPreviewImage( Image previewImage )
    {
        this.imageView.setImage( previewImage );
        this.imageView.setScaleY( 1.0 );
    }

    protected static void setOptimalCameraDistance( Camera c )
    {
        float cameraDistance;
        switch( c.getCameraType() )
        {
            case ORTHOGRAPHIC_CAMERA:
                cameraDistance = 1.0f;
                break;
            case PERSPECTIVE_CAMERA:
                cameraDistance = ( float ) ( 1.0 / Math.sin( ( Math.PI / 180.0 ) * ( c.getFoVY() / 2.0 ) ) );
                break;
            default:
                throw new RuntimeException();
        }
        c.lookAt( new Point3d( 0, 0, cameraDistance ), new Point3d( 0, 0, -1 ), new Vector3d( 0, 1, 0 ) );
    }

    void passDataToASR()
    {
        // avoid recursing into triggerRepaint()
        boolean troc = triggerRepaintOnChange.get();
        triggerRepaintOnChange.set( false );

        try
        {
            isFormulaValid.set( true );

            List< String > namesABCD = Arrays.asList( new String[]{ "a", "b", "c", "d" } );

            if( !formula.getValue().equals( asr.getSurfaceFamilyString() ) )
            {
                asr.setSurfaceFamily( formula.getValue() );

                Set< String > newParameterNames = new HashSet< String >( asr.getAllParameterNames() );
                newParameterNames.add( "scale_factor" );
                parameters.keySet().retainAll( newParameterNames );
                newParameterNames.removeAll( parameters.keySet() );
                newParameterNames.retainAll( namesABCD );
                newParameterNames.forEach( e -> { parameters.put( e, 0.0 ); asr.setParameterValue( e, 0.0 ); } );
            }

            asr.setTransform( rsd.getRotation() );
            double scaleFactor = parameters.get( "scale_factor" );
            asr.setSurfaceTransform( new Matrix4d(
                Math.pow( 10, scaleFactor), 0.0, 0.0, 0.0,
                0.0, Math.pow( 10, scaleFactor), 0.0, 0.0,
                0.0, 0.0, Math.pow( 10, scaleFactor), 0.0,
                0.0, 0.0, 0.0, 1.0
            ) );
            setOptimalCameraDistance( asr.getCamera() );

            namesABCD.forEach( n -> { if( parameters.containsKey( n ) ) asr.setParameterValue( n, parameters.get( n ) ); } );

            Set< String > unassignedParameterNames = new HashSet< String >( asr.getAllParameterNames() );
            asr.getAssignedParameters().forEach( e -> unassignedParameterNames.remove( e.getKey() ) );
            if( unassignedParameterNames.size() > 0 )
                throw new UnsupportedOperationException( "No value assigned to some parameters: " + unassignedParameterNames );

            Function< Color, Color3f > c2c3f = c -> new Color3f( ( float ) c.getRed(), ( float ) c.getGreen(), ( float ) c.getBlue() );

            asr.getFrontMaterial().setColor( c2c3f.apply( frontColor.getValue() ) );
            asr.getBackMaterial().setColor( c2c3f.apply( backColor.getValue() ) );
        }
        catch( Exception e )
        {

            error.setValue( e );
            isFormulaValid.set( false );
        }

        triggerRepaintOnChange.set( troc );
    }

    void retriveDataFromASR()
    {
        boolean troc = triggerRepaintOnChange.get();
        triggerRepaintOnChange.set( false );
        formula.setValue( asr.getSurfaceFamilyString() );

        asr.getAssignedParameters().forEach( e -> parameters.put( e.getKey(), e.getValue() ) );

        Function< Color3f, Color > c3f2c = c -> new Color( c.x, c.y, c.z, 1.0 );

        frontColor.setValue( c3f2c.apply( asr.getFrontMaterial().getColor() ) );
        backColor.setValue( c3f2c.apply( asr.getBackMaterial().getColor() ) );

        triggerRepaintOnChange.setValue( troc );
        triggerRepaint();
    }

    public void load( String s )
    {
        try
        {
            Properties props = new Properties();
            props.load( new ByteArrayInputStream( s.getBytes() ) );
            load( props );
        }
        catch( IOException ioe )
        {
            throw new RuntimeException( ioe );
        }
    }

    public void load( File file )
        throws IOException
    {
        load( file.toURI().toURL() );
    }

    public void load( URL url )
        throws IOException
    {
        Properties props = new Properties();
        props.load( url.openStream() );
        load( props );
    }

    public void load( Properties props )
    {
        isFormulaValid.setValue( true );
        triggerRepaintOnChange.setValue( false );
        try
        {
            asr.setSurfaceFamily( props.getProperty( "surface_equation" ) );

            Set< Map.Entry< Object, Object > > entries = props.entrySet();
            String parameter_prefix = "surface_parameter_";
            for( Map.Entry< Object, Object > entry : entries )
            {
                String name = (String) entry.getKey();
                if( name.startsWith( parameter_prefix ) )
                {
                    String parameterName = name.substring( parameter_prefix.length() );
                    float parameterValue = Float.parseFloat( ( String ) entry.getValue() );
                    asr.setParameterValue( parameterName, parameterValue );
                }
            }

            asr.getCamera().loadProperties( props, "camera_", "" );

            asr.getFrontMaterial().loadProperties(props, "front_material_", "");
            asr.getBackMaterial().loadProperties(props, "back_material_", "");

            for( int i = 0; i < asr.MAX_LIGHTS; i++ )
            {
                asr.getLightSource( i ).setStatus(LightSource.Status.OFF);
                asr.getLightSource( i ).loadProperties( props, "light_", "_" + i );
            }
            Function< String, Color3f > string2color = s ->
            {
                Scanner sc = new Scanner( s );
                sc.useLocale( Locale.US );
                return new Color3f( sc.nextFloat(), sc.nextFloat(), sc.nextFloat() );
            };

            asr.setBackgroundColor( string2color.apply( props.getProperty( "background_color" ) ) );
            parameters.put( "scale_factor", Double.parseDouble( props.getProperty( "scale_factor" ) ) );
            rsd.setRotation( BasicIO.fromMatrix4dString( props.getProperty( "rotation_matrix" ) ) );

            retriveDataFromASR();
        }
        catch( Exception e )
        {
            error.setValue( e );
            isFormulaValid.set( false );
        }
        triggerRepaintOnChange.setValue( true );
        triggerRepaint();
    }

    public void load( GalleryItem galleryItem )
        throws IOException
    {
        load( galleryItem.getJsurfURL() );
        setPreviewImage( galleryItem.getThumbnailImage() );
    }

    public void store( File file )
        throws IOException
    {
        passDataToASR();
        Properties jsurfProperties = FileFormat.save( asr );
        Matrix4d identity = new Matrix4d();
        identity.setIdentity();
        jsurfProperties.put( "transform", BasicIO.toString( identity ) );
        jsurfProperties.put( "surface_transform", BasicIO.toString( identity ) );
        jsurfProperties.put( "rotation_matrix", BasicIO.toString( rsd.getRotation() ) );
        jsurfProperties.put( "scale_factor", parameters.get( "scale_factor" ).toString() );
        jsurfProperties.store( new FileWriter( file ), "SURFER surface description" );
    }

    private static BufferedImage createBufferedImageFromRGB( int[] rgbBuffer, int w, int h )
    {
        DirectColorModel colormodel = new DirectColorModel( 24, 0xff0000, 0xff00, 0xff );
        SampleModel sampleModel = colormodel.createCompatibleSampleModel( w, h );
        DataBufferInt data = new DataBufferInt( rgbBuffer, w * h );
        WritableRaster raster = WritableRaster.createWritableRaster( sampleModel, data, new Point( 0, 0 ) );
        return new BufferedImage( colormodel, raster, false, null );
    }

    private static BufferedImage flipV( BufferedImage bi )
    {
        AffineTransform tx = AffineTransform.getScaleInstance( 1, -1 );
        tx.translate( 0, -bi.getHeight( null ) );
        AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
        return op.filter( bi, null );
    }

    public void export( File file )
    {
        export( file, Preferences.General.getExportSize() );
    }

    public void export( File file, int renderSize )
    {
        int colorBuffer[] = new int[ renderSize * renderSize ];

        passDataToASR();
        asr.setAntiAliasingMode( AntiAliasingMode.SUPERSAMPLING );
        asr.setAntiAliasingPattern( AntiAliasingPattern.OG_4x4 );
        asr.draw( colorBuffer, renderSize, renderSize );

        BufferedImage im = flipV( createBufferedImageFromRGB( colorBuffer, renderSize, renderSize ) );

        try
        {
            if( file.getName().endsWith( ".png" ) )
            {
                ImageIO.write( im, "png", file );
            }
            else if( file.getName().endsWith( ".jpg" ) || file.getName().endsWith( ".jpeg" ) )
            {
                ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName( "jpg" ).next();
                ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
                jpgWriteParam.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
                jpgWriteParam.setCompressionQuality( Preferences.General.getJpegQuality() / 100f );

                FileImageOutputStream outputStream = new FileImageOutputStream( file );
                jpgWriter.setOutput( outputStream );
                IIOImage outputImage = new IIOImage( im, null, null);
                jpgWriter.write( null, outputImage, jpgWriteParam );
                jpgWriter.dispose();
                outputStream.close();
            }
            else
                throw new IllegalArgumentException( "Unsupported file extension of " + file.getName() );
        }
        catch( Exception e )
        {
            if( RuntimeException.class.isAssignableFrom( e.getClass() ) )
                throw ( RuntimeException ) e;
            else
                throw new RuntimeException( e );
        }
    }

    // triggerRepaintOnChange
    public boolean getTriggerRepaintOnChange()
    {
        return triggerRepaintOnChange.getValue();
    }

    public void setTriggerRepaintOnChange( boolean value )
    {
        triggerRepaintOnChange.setValue( value );
    }

    public BooleanProperty triggerRepaintOnChangeProperty()
    {
        return triggerRepaintOnChange;
    }

    // formula
    public String getFormula()
    {
        return formula.getValue();
    }

    public void setFormula( String value )
    {
        formula.setValue( value );
    }

    public StringProperty formulaProperty()
    {
        return formula;
    }

    // isValid
    public boolean getIsValid()
    {
        return isValid.getValue();
    }

    public ReadOnlyBooleanProperty isValidProperty()
    {
        return isValid.getReadOnlyProperty();
    }

    // error
    public String getErrorMessage()
    {
        return errorMessage.getValue();
    }

    public ReadOnlyStringProperty errorMessageProperty()
    {
        return errorMessage.getReadOnlyProperty();
    }

    // errorMessage
    public Throwable getError()
    {
        return error.getValue();
    }

    public ReadOnlyObjectProperty< Throwable > errorProperty()
    {
        return error.getReadOnlyProperty();
    }

    // parameters
    public ObservableMap< String, Double > getParameters()
    {
        return parameters.get();
    }

    public void setParameters( ObservableMap< String, Double > value )
    {
        parameters.set( value );
    }

    public MapProperty< String, Double > parametersProperty()
    {
        return parameters;
    }

    // frontColor
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
        return frontColor;
    }

    // backColor
    public Color getBackColor()
    {
        return backColor.getValue();
    }

    public void setBackColor( Color value )
    {
        backColor.setValue( value );
    }

    public ObjectProperty< Color > backColorProperty()
    {
        return backColor;
    }
}

class RenderingTask extends Task< Double >
{
    protected CPUAlgebraicSurfaceRendererExt asr;
    protected CPUAlgebraicSurfaceRendererExt.DrawcallStaticDataExt dcsd;
    protected ImageView imageView;
    protected int renderSize;
    protected IntegerProperty renderSizeProperty;
    protected AntiAliasingMode aam;
    protected AntiAliasingPattern aap;

    protected Semaphore semaphore;


    public RenderingTask(
        CPUAlgebraicSurfaceRendererExt asr,
        ImageView imageView,
        int renderSize,
        IntegerProperty renderSizeProperty,
        AntiAliasingMode aam,
        AntiAliasingPattern aap
    )
    {
        this.asr = asr;
        this.imageView = imageView;
        this.renderSize = renderSize;
        this.renderSizeProperty = renderSizeProperty;
        this.aam = aam;
        this.aap = aap;

        semaphore = new Semaphore( 0 );
    }

    // automatically called on the JavaFX application thread
    @Override
    protected void scheduled()
    {
        super.scheduled();

        // apply anti-aliasing settings
        asr.setAntiAliasingMode( aam );
        asr.setAntiAliasingPattern( aap );

        // grab the current state of the asr
        // (to be used later on the worker thread)
        dcsd = asr.collectDrawCallStaticDataExt(
            new int[ renderSize * renderSize ],
            renderSize,
            renderSize
        );

        // permit execution of call() method on background thread
        semaphore.release();
    }

    @Override
    protected Double call() throws Exception {
        try
        {
            // wait until dcsd is initialized on JavaFX application thread
            semaphore.acquire();
            long t_start = System.nanoTime();
            asr.draw( dcsd );
            long t_end = System.nanoTime();

            // return time per pixel
            return ( ( t_end - t_start ) / 1000000000.0 ) / ( renderSize * renderSize );
        }
        catch( InterruptedException ie )
        {
            throw ie;
        }
        catch( RenderingInterruptedException rie )
        {
            throw rie;
        }
        catch( Throwable t )
        {
            // rethrow on JavaFX application thread as well for reporting
            Platform.runLater( () -> { throw new RuntimeException( "Uncaught exception in rendering thread", t ); } );
            throw t;
        }
    }

    // automatically called on the JavaFX application thread
    @Override
    protected void succeeded()
    {
        super.succeeded();

        renderSizeProperty.setValue( Math.max( dcsd.getWidth(), dcsd.getHeight() ) );
        imageView.setImage( createImageFromRGB( dcsd.getColorBuffer(), dcsd.getWidth(), dcsd.getHeight() ) );
        this.imageView.setScaleY( -1.0 ); // because jsurf writes pixel lines in opposite order
    }

    private static Image createImageFromRGB( int[] rgbBuffer, int w, int h )
    {
        WritableImage image = new WritableImage( w, h );
        image.getPixelWriter().setPixels(
            0, 0, w, h,
            PixelFormat.getIntArgbInstance(),
            rgbBuffer, 0, w
        );
        return image;
    }
};
