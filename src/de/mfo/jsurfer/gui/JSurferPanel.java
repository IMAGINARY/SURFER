/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.gui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import com.bric.swing.ColorPicker;
import javax.vecmath.*;

import de.mfo.jsurfer.rendering.*;

import de.mfo.jsurfer.algebra.*;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class JSurferPanel extends JPanel
{
    public enum Layout
    {
        FIRST { void layoutComponents( JSurferPanel jsp ) { layoutComponentsSecond( jsp ); } },
        SECOND { void layoutComponents( JSurferPanel jsp ) { layoutComponentsSecond( jsp ); } },
        FIRST_WITH_SAVE( true ) { void layoutComponents( JSurferPanel jsp ) { layoutComponentsSecond( jsp ); } },
        SECOND_WITH_SAVE( true ) { void layoutComponents( JSurferPanel jsp ) { layoutComponentsSecond( jsp ); } };

        Layout() { this.showSaveButton = false; }
        Layout( boolean showSaveButton ) { this.showSaveButton = showSaveButton; }

        abstract void layoutComponents( JSurferPanel jsp );
        void layoutComponentsFirst( JSurferPanel jsp )
        {
            jsp.setBackground( Color.WHITE );
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setHgap( 7 );
            borderLayout.setVgap( 7 );
            jsp.setLayout( borderLayout );

            // init text field panel for surface expression
            JPanel surfaceExpressionPanel = new JPanel();
            surfaceExpressionPanel.setOpaque( false );
            surfaceExpressionPanel.setLayout( new BorderLayout() );
            JLabel surfaceExpressionLabel = new JLabel( "0 = " );
            surfaceExpressionLabel.setFont( jsp.surfaceExpression.getFont() );
            surfaceExpressionPanel.add( surfaceExpressionLabel, BorderLayout.WEST );
            surfaceExpressionPanel.add( jsp.surfaceExpression, BorderLayout.CENTER );
            if( showSaveButton )
                surfaceExpressionPanel.add( jsp.saveButton, BorderLayout.EAST );

            // init front color picker panel
            JPanel frontColorPickerPanel = new JPanel();
            frontColorPickerPanel.setLayout( new BorderLayout() );
            frontColorPickerPanel.setOpaque( false );
            frontColorPickerPanel.add( jsp.frontColorPicker, BorderLayout.SOUTH );

            // init back color picker panel
            JPanel backColorPickerPanel = new JPanel();
            backColorPickerPanel.setLayout( new BorderLayout() );
            backColorPickerPanel.setOpaque( false );
            backColorPickerPanel.add( jsp.backColorPicker, BorderLayout.SOUTH );

            jsp.add( surfaceExpressionPanel, BorderLayout.SOUTH );
            jsp.add( frontColorPickerPanel, BorderLayout.WEST );
            jsp.add( backColorPickerPanel, BorderLayout.EAST );
            jsp.add( jsp.renderer, BorderLayout.CENTER );
            jsp.validate();
        }
        void layoutComponentsSecond( JSurferPanel jsp )
        {
            jsp.setBackground( Color.WHITE );
            jsp.setLayout( new BorderLayout( 7, 7 ) );

            // init text field panel for surface expression
            JPanel surfaceExpressionPanel = new JPanel( new BorderLayout() );
            surfaceExpressionPanel.setOpaque( false );
            JLabel surfaceExpressionLabel = new JLabel( "0 = " );
            surfaceExpressionLabel.setFont( jsp.surfaceExpression.getFont() );
            surfaceExpressionPanel.add( surfaceExpressionLabel, BorderLayout.WEST );
            surfaceExpressionPanel.add( jsp.surfaceExpression, BorderLayout.CENTER );
            if( showSaveButton )
                surfaceExpressionPanel.add( jsp.saveButton, BorderLayout.EAST );

            // init front color picker panel
            JPanel colorPickerPanel = new JPanel();
            colorPickerPanel.setLayout( new BoxLayout( colorPickerPanel, BoxLayout.Y_AXIS ) );
            colorPickerPanel.setOpaque( false );
            colorPickerPanel.add( jsp.frontColorPicker );
            colorPickerPanel.add( jsp.backColorPicker );
            JPanel colorPickerPanel2 = new JPanel( new BorderLayout() );
            colorPickerPanel2.setOpaque( false );
            JPanel dummyPanel = new JPanel();
            dummyPanel.setOpaque( false );
            colorPickerPanel2.add( dummyPanel, BorderLayout.CENTER );
            colorPickerPanel2.add( colorPickerPanel, BorderLayout.SOUTH );

            jsp.add( surfaceExpressionPanel, BorderLayout.SOUTH );
            jsp.add( colorPickerPanel2, BorderLayout.EAST );
            jsp.add( jsp.renderer, BorderLayout.CENTER );

            jsp.validate();
        }

        boolean showSaveButton;

    }
    
    private JTextField surfaceExpression;
    private ColorPicker frontColorPicker;
    private ColorPicker backColorPicker;
    private JSurferRenderPanel renderer;
    private ResourceBundle strings;
    private JButton saveButton;

    
    public JSurferPanel()
    {
        this( Layout.FIRST );
    }
    
    public JSurferPanel( Layout l )
    {
        super();
        strings = ResourceBundle.getBundle( "de.mfo.jsurfer.jsurfer" );
        
        initComponents();
        l.layoutComponents( this );
        initMaterials();
        initLights();
        frontColorChanged();
        backColorChanged();
        surfaceExpressionChanged();
    }
    
    private void initComponents()
    {
        // init text field for surface expression
        surfaceExpression = new JTextField();
        surfaceExpression.setText( "x^2+y^2+z^2+2*x*y*z-1" );
        surfaceExpression.getDocument().addDocumentListener( new DocumentListener()
                                                                 {
                                                                     public void changedUpdate( DocumentEvent e )
                                                                     {
                                                                         surfaceExpressionChanged();
                                                                     }

                                                                     public void insertUpdate( DocumentEvent e )
                                                                     {
                                                                         surfaceExpressionChanged();
                                                                     }

                                                                     public void removeUpdate( DocumentEvent e )
                                                                     {
                                                                         surfaceExpressionChanged();
                                                                     }
                                                                 } );
                                                                 
        // init front color picker panel
        frontColorPicker = new ColorPicker( false );
        frontColorPicker.setOpaque( false );
        frontColorPicker.setForeground( Color.WHITE );
        frontColorPicker.setPreferredSize( new Dimension( 150, 150 ) );
        for( Component c : frontColorPicker.getComponents() )
            if( c instanceof JComponent )
                ( ( JComponent ) c ).setOpaque( false );
        frontColorPicker.getColorPanel().addChangeListener( new ChangeListener()
                                                        {
                                                            public void stateChanged( ChangeEvent ce )
                                                            {
                                                                frontColorChanged();
                                                            }
                                                        } );
        TitledBorder ftb = BorderFactory.createTitledBorder( strings.getString( "frontColor" ) );
        ftb.setTitleJustification( TitledBorder.CENTER );
        ftb.setBorder( BorderFactory.createEmptyBorder() );
        frontColorPicker.setBorder( ftb );
        
        // init back color picker panel
        backColorPicker = new ColorPicker( false );
        backColorPicker.setOpaque( false );
        backColorPicker.setForeground( Color.WHITE );
        backColorPicker.setPreferredSize( new Dimension( 150, 150 ) );
        for( Component c : backColorPicker.getComponents() )
            if( c instanceof JComponent )
                ( ( JComponent ) c ).setOpaque( false );
        backColorPicker.getColorPanel().addChangeListener( new ChangeListener()
                                                       {
                                                           public void stateChanged( ChangeEvent ce )
                                                           {
                                                               backColorChanged();
                                                           }
                                                       } );
        TitledBorder btb = BorderFactory.createTitledBorder( strings.getString( "backColor" ) );
        btb.setTitleJustification( TitledBorder.CENTER );
        btb.setBorder( BorderFactory.createEmptyBorder() );
        backColorPicker.setBorder( btb );

        saveButton = new JButton( "Save image" );
        saveButton.setToolTipText( "Save the currently displayed image to a PNG file." );
        saveButton.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent e ) { saveToPNG(); } } );
        
        renderer = new JSurferRenderPanel();
        renderer.setScale( 1f/0.25f );
        //renderer.setResizeImageWithComponent( true );
    }
    
    private void initMaterials()
    {
        // init front material
        Material frontMaterial = new Material();
        frontMaterial = new Material();
        frontMaterial.setColor( new Color3f( 0.70588f, 0.22745f, 0.14117f ) );
        frontMaterial.setAmbientIntensity( 0.4f );
        frontMaterial.setDiffuseIntensity( 0.8f );
        frontMaterial.setSpecularIntensity( 0.5f );
        frontMaterial.setShininess( 30 );
        renderer.getAlgebraicSurfaceRenderer().setFrontMaterial( frontMaterial );
        
        // init front color picker start value
        Color cf = frontMaterial.getColor().get();
        frontColorPicker.setRGB( cf.getRed(), cf.getGreen(), cf.getBlue() );

        // init back material
        Material backMaterial = new Material();
        backMaterial = new Material();
        backMaterial.setColor( new Color3f( 1.0f, 0.8f, 0.4f ) );
        backMaterial.setAmbientIntensity( 0.4f );
        backMaterial.setDiffuseIntensity( 0.8f );
        backMaterial.setSpecularIntensity( 0.5f );
        backMaterial.setShininess( 30 );
        renderer.getAlgebraicSurfaceRenderer().setBackMaterial( backMaterial );
        
        // init back color picker start value
        Color cb = backMaterial.getColor().get();
        backColorPicker.setRGB( cb.getRed(), cb.getGreen(), cb.getBlue() );
    }
    
    private void initLights()
    {
        LightSource[] lights = new LightSource[ AlgebraicSurfaceRenderer.MAX_LIGHTS ];

        lights[ 0 ] = new LightSource();
        lights[ 0 ].setPosition( new Point3d( -100f, 100f, 100f ) );
        lights[ 0 ].setIntensity( 0.5f );
        lights[ 0 ].setColor( new Color3f( 1f, 1f, 1f ) );

        lights[ 1 ] = new LightSource();
        lights[ 1 ].setPosition( new Point3d( 100f, 100f, 100f ) );
        lights[ 1 ].setIntensity( 0.7f );
        lights[ 1 ].setColor( new Color3f( 1f, 1f, 1f ) );

        lights[ 2 ] = new LightSource();
        lights[ 2 ].setPosition( new Point3d( 0f, -100f, 100f ) );
        lights[ 2 ].setIntensity( 0.3f );
        lights[ 2 ].setColor( new Color3f( 1f, 1f, 1f ) );

        for( int i = 0; i < lights.length; i++ )
            renderer.getAlgebraicSurfaceRenderer().setLightSource( i, lights[ i ] );
    }
    
    private void frontColorChanged()
    {
        Material m = renderer.getAlgebraicSurfaceRenderer().getFrontMaterial();
        m.setColor( new Color3f( frontColorPicker.getColor() ) );
        renderer.getAlgebraicSurfaceRenderer().setFrontMaterial( m );
        renderer.repaintImage();
    }
    
    private void backColorChanged()
    {
        Material m = renderer.getAlgebraicSurfaceRenderer().getBackMaterial();
        m.setColor( new Color3f( backColorPicker.getColor() ) );
        renderer.getAlgebraicSurfaceRenderer().setBackMaterial( m );
        renderer.repaintImage();
    }
    
    private void surfaceExpressionChanged()
    {
        try
        {
           //PolynomialOperation p = AlgebraicExpressionParser.parse( surfaceExpression.getText() );
           
           // current version does not support surface parameters
          // if( p.accept( new DoubleVariableChecker(), ( Void ) null ) )
         //      throw new Exception();
           PolynomialOperation p;
           {
            PolynomialOperation t1=new PolynomialPower(new PolynomialVariable( PolynomialVariable.Var.valueOf( "x" ) ), 2 );
    PolynomialOperation t2=new PolynomialPower(new PolynomialVariable( PolynomialVariable.Var.valueOf( "y" ) ), 2 );
    PolynomialOperation t3=new PolynomialPower(new PolynomialVariable( PolynomialVariable.Var.valueOf( "z" ) ), 2 );
    PolynomialOperation t4=new PolynomialAddition(new PolynomialAddition(t1,t2),t3);
    PolynomialOperation t5=new PolynomialMultiplication(new PolynomialMultiplication(new DoubleValue( 2.0 ),new PolynomialVariable( PolynomialVariable.Var.valueOf( "x" ) )),
                                                        new PolynomialMultiplication(new PolynomialVariable( PolynomialVariable.Var.valueOf( "y" ) ),new PolynomialVariable( PolynomialVariable.Var.valueOf( "z" ) )));
    PolynomialOperation t6=new PolynomialSubtraction(t5,new DoubleValue( 1.0 ));
    PolynomialOperation t7=new PolynomialAddition(t4,t6);
            //p.getAlgebraicSurfaceRenderer().setSurfaceExpression( t7 );
    p=t7;
        }
           renderer.getAlgebraicSurfaceRenderer().setSurfaceExpression( p );
           renderer.repaintImage();
           surfaceExpression.setBackground( Color.WHITE );
            
            
        }
                
        catch( Exception e )
        {
            surfaceExpression.setBackground( new Color( 255, 90, 90 ).brighter() );
        }
    }
    
    public void setSurfaceExpression( String s )
    {
        surfaceExpression.setText( s );
    }

    public void saveToPNG()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
        fc.setAcceptAllFileFilterUsed( false );
        PNGFilter pngFilter = new PNGFilter();
        fc.addChoosableFileFilter( pngFilter );

        int returnVal = fc.showSaveDialog( this );
        if( returnVal == JFileChooser.APPROVE_OPTION )
        {
            java.io.File f = fc.getSelectedFile();
            f = PNGFilter.ensureExtension( f );
            try
            {
                renderer.saveToPNG( f, 1024, 1024 );
            }
            catch( java.lang.Exception e )
            {
                String message = "Could not save to file \"" + f.getName() + "\".";
                if( e.getMessage() != null )
                    message += "\n\nMessage: " + e.getMessage();
                JOptionPane.showMessageDialog( null, message, "Error", JOptionPane.OK_OPTION );
            }
        }
    }

    public static void main( String[] args )
    {
        JFrame f = new JFrame( "jSurfer - www.imaginary-exhibition.com" );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JSurferPanel jsp = new JSurferPanel( JSurferPanel.Layout.SECOND_WITH_SAVE );
        jsp.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        f.getContentPane().add( jsp );
        f.pack();
        f.setSize( 488, 357 );
        f.setVisible( true );
//        System.out.println( f.getContentPane().getComponent(0).getSize() );
    }
}

class DoubleVariableChecker extends AbstractVisitor<Boolean, Void>
{
    public Boolean visit( PolynomialAddition pa, Void param )
    {
        return pa.firstOperand.accept( this, param ) || pa.secondOperand.accept( this, param );
    }

    public Boolean visit( PolynomialSubtraction ps, Void param )
    {
        return ps.firstOperand.accept( this, param ) || ps.secondOperand.accept( this, param );
    }

    public Boolean visit( PolynomialMultiplication pm, Void param )
    {
        return pm.firstOperand.accept( this, param ) || pm.secondOperand.accept( this, param );
    }

    public Boolean visit( PolynomialPower pp, Void param )
    {
        return pp.base.accept( this, param );
    }

    public Boolean visit( PolynomialNegation pn, Void param )
    {
        return pn.operand.accept( this, param );
    }

    public Boolean visit( PolynomialDoubleDivision pdd, Void param )
    {
        return pdd.dividend.accept( this, param ) || pdd.divisor.accept( this, param );
    }

    public Boolean visit( PolynomialVariable pv, Void param )
    {
        return false;
    }

    public Boolean visit( DoubleBinaryOperation dbop, Void param )
    {
        return dbop.firstOperand.accept( this, param ) || dbop.secondOperand.accept( this, param );
    }

    public Boolean visit( DoubleUnaryOperation duop, Void param )
    {
        return duop.operand.accept( this, param );
    }

    public Boolean visit( DoubleValue dv, Void param )
    {
        return false;
    }

    public Boolean visit( DoubleVariable dv, Void param )
    {
        return true;
    }
}