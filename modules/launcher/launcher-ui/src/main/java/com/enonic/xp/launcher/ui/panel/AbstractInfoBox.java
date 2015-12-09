package com.enonic.xp.launcher.ui.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.enonic.xp.launcher.ui.util.Browser;

abstract class AbstractInfoBox
    extends JPanel
    implements HyperlinkListener
{
    private final JEditorPane htmlPanel;

    public AbstractInfoBox( final String resource )
    {
        setOpaque( false );
        setLayout( new BorderLayout( 10, 10 ) );

        this.htmlPanel = new JEditorPane();
        this.htmlPanel.setEditable( false );
        this.htmlPanel.addHyperlinkListener( this );
        this.htmlPanel.setOpaque( false );

        displayPage( resource );

        add( this.htmlPanel, BorderLayout.CENTER );
    }

    private void displayPage( final String name )
    {
        try
        {
            this.htmlPanel.setPage( getClass().getResource( name ) );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( "Failed to load html [" + name + "]", e );
        }
    }

    protected final void addSouth( final Component comp )
    {
        add( comp, BorderLayout.SOUTH );
    }

    @Override
    public void hyperlinkUpdate( final HyperlinkEvent event )
    {
        if ( event.getEventType() != HyperlinkEvent.EventType.ACTIVATED )
        {
            return;
        }

        final URL url = event.getURL();
        if ( url != null )
        {
            Browser.launch( url );
        }
    }
}
