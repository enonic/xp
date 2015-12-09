package com.enonic.xp.launcher.ui.panel;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

public final class LoggingPanel
    extends JScrollPane
{
    public LoggingPanel()
    {
        final LoggingTextArea text = new LoggingTextArea();
        setViewportView( text );

        setOpaque( false );
        setBorder( BorderFactory.createMatteBorder( 1, 0, 0, 0, Color.DARK_GRAY ) );
        getViewport().setOpaque( false );

        System.setOut( new LoggingOutputStream( text ) );
    }
}
