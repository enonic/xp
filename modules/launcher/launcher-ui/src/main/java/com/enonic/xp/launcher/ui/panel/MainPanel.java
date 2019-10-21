package com.enonic.xp.launcher.ui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public final class MainPanel
    extends JPanel
{
    private final BufferedImage image;

    private final InfoPanel infoPanel;

    public MainPanel()
        throws Exception
    {
        setOpaque( false );
        setLayout( new BorderLayout() );
        this.image = ImageIO.read( getClass().getResource( "background.png" ) );

        final LoggingPanel loggingPanel = new LoggingPanel();
        this.infoPanel = new InfoPanel();

        final JPanel panel = new JPanel();
        panel.setOpaque( false );
        panel.setLayout( new FlowLayout( FlowLayout.RIGHT, 30, 30 ) );
        panel.add( this.infoPanel );

        add( panel, BorderLayout.NORTH );
        add( loggingPanel, BorderLayout.CENTER );
    }

    @Override
    protected void paintComponent( final Graphics graphics )
    {
        graphics.drawImage( this.image, 0, 0, null );
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension( this.image.getWidth(), this.image.getHeight() );
    }

    public InfoPanel getInfoPanel()
    {
        return this.infoPanel;
    }
}
