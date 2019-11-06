package com.enonic.xp.launcher.ui.panel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JTextArea;

final class LoggingTextArea
    extends JTextArea
{
    public LoggingTextArea()
    {
        super( 16, 7 );
        setMargin( new Insets( 3, 3, 3, 3 ) );
        setEditable( false );
        setOpaque( false );
        setWrapStyleWord( true );
        setForeground( Color.WHITE );
        setFont( new Font( Font.MONOSPACED, Font.PLAIN, 11 ) );
        setTabSize( 2 );
    }

    @Override
    protected void paintComponent( final Graphics graphics )
    {
        final Graphics2D g2 = (Graphics2D) graphics;
        final Composite comp = g2.getComposite();
        final Color c = g2.getColor();

        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.7F ) );
        g2.setColor( Color.BLACK );
        g2.fillRect( 0, 0, getWidth(), getHeight() );
        g2.setComposite( comp );
        g2.setColor( c );

        super.paintComponent( graphics );
    }

    public void appendText( final String text )
    {
        append( text );
        setCaretPosition( getDocument().getLength() );

        final int size = 100000;
        final int maxOverflow = 500;

        final int overflow = getDocument().getLength() - size;
        if ( overflow >= maxOverflow )
        {
            replaceRange( "", 0, overflow );
        }
    }
}
