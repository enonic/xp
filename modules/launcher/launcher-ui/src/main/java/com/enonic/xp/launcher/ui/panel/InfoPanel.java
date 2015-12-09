package com.enonic.xp.launcher.ui.panel;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

public final class InfoPanel
    extends JPanel
{
    private final static String STARTING = "starting";

    private final static String STARTED = "started";

    public InfoPanel()
    {
        setOpaque( false );
        setLayout( new CardLayout( 20, 20 ) );
        setPreferredSize( new Dimension( 300, 150 ) );

        add( new StartedInfoBox(), STARTED );
        add( new StartingInfoBox(), STARTING );

        showCard( STARTED );
    }

    private void showCard( final String id )
    {
        ( (CardLayout) getLayout() ).show( this, id );
    }

    public void showStarting()
    {
        showCard( STARTING );
    }

    public void showStarted()
    {
        showCard( STARTED );
    }
}
