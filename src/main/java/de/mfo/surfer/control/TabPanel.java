package de.mfo.surfer.control;

import de.mfo.surfer.Main;
import de.mfo.surfer.util.FXUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import de.mfo.surfer.util.Preferences;
import static de.mfo.surfer.util.L.lb;

public class TabPanel extends Region
{
    SimpleIntegerProperty activeTab;
    ToggleGroup toggleGroup;

    public TabPanel( Pane galleryPanel, Pane infoPanel, Pane colorPanel )
    {
        super();

        activeTab = new SimpleIntegerProperty( Preferences.General.initiallyOpenedTabProperty().get() );
        toggleGroup = new ToggleGroup();

        initTabLabel( "Tab_Text_Gallery", "start" );
        initTabLabel( "Tab_Text_Info", "info" );
        initTabLabel( "Tab_Text_Color", "colors" );

        initTabButton( "Gallery", 0 );
        initTabButton( "Info", 1 );
        initTabButton( "Color", 2 );

        initTabContent( galleryPanel, 0 );
        initTabContent( infoPanel, 1 );
        initTabContent( colorPanel, 2 );
    }

    private void initTabLabel( String placeholderId, String lbId )
    {
        Label label = new Label();
        FXUtils.resizeRelocateTo( label, FXUtils.setVisible( Main.fxmlLookup( "#" + placeholderId ), false ) );
        label.setMaxHeight( label.getMaxHeight() * 2.0 );
        label.textProperty().bind( lb( lbId ) );
        getChildren().add( label );
    }

    private void initTabButton( String suffix, int tabIndex )
    {
        SceneNodeToggleButton button = new SceneNodeToggleButton(
            Main.fxmlLookup( "#Button_" + suffix ),
            Main.fxmlLookup( "#Button_Over_" + suffix ),
            Main.fxmlLookup( "#Button_Pressed_" + suffix )
        );
        button.setToggleGroup( toggleGroup );
        button.setOnAction( e -> activeTab.set( tabIndex ) );
        activeTab.addListener( ( o, ov, nv ) -> button.setSelected( nv.equals( tabIndex ) ) );
        getChildren().add( button );
    }

    private void initTabContent( Pane tabContent, int index )
    {
        FXUtils.resizeRelocateTo( tabContent, FXUtils.setVisible( Main.fxmlLookup( "#Tab_Box" ), false ) );
        tabContent.visibleProperty().bind( activeTab.isEqualTo( index ) );
        tabContent.disableProperty().bind( activeTab.isNotEqualTo( index ) );
        getChildren().add( tabContent );
    }

    public int getActiveTabIndex()
    {
        return activeTab.get();
    }

    public void setActiveTabIndex( int value )
    {
        activeTab.set( value );
    }

    public IntegerProperty activeTabIndexProperty()
    {
        return activeTab;
    }

    @Override
    public String getUserAgentStylesheet()
    {
        return SceneNodeSliderWithNameAndValue.class.getResource( "/de/mfo/surfer/css/style.css" ).toExternalForm();
    }
}
