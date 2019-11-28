package com.enonic.xp.launcher.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;

import com.enonic.xp.launcher.Launcher;
import com.enonic.xp.launcher.LauncherListener;
import com.enonic.xp.launcher.ui.panel.MainPanel;
import com.enonic.xp.launcher.ui.util.Icons;

public final class MainWindow
    extends JFrame
    implements LauncherListener
{
    private static MainWindow INSTANCE;

    private final Launcher launcher;

    private final MainPanel mainPanel;

    public MainWindow( final Launcher launcher )
        throws Exception
    {
        INSTANCE = this;

        this.launcher = launcher;
        this.launcher.setListener( this );

        setTitle( "Enonic XP" );
        setBackground( Color.WHITE );
        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        setResizable( false );
        setIconImage( Icons.LOGO.getImage() );

        this.mainPanel = new MainPanel();
        getContentPane().add( this.mainPanel );

        System.setProperty( "com.apple.mrj.application.apple.menu.about.name", "Enonic CMS" );

        addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( final WindowEvent windowEvent )
            {
                exit();
            }
        } );
    }

    public void showFrame()
    {
        pack();

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation( (int) screenSize.getWidth() / 2 - getWidth() / 2, (int) screenSize.getHeight() / 2 - getHeight() / 2 );

        setVisible( true );

        startServer();
    }

    public void exit()
    {
        this.launcher.stop();
        dispose();
        System.exit( 0 );
    }

    private void startServer()
    {
        try
        {
            setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
            this.mainPanel.getInfoPanel().showStarting();
            this.launcher.start();
        }
        catch ( final Exception e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public void serverStarted()
    {
        setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
        this.mainPanel.getInfoPanel().showStarted();
    }

    public String getHttpUrl()
    {
        return this.launcher.getHttpUrl();
    }

    public File getHomeDir()
    {
        return this.launcher.getHomeDir();
    }

    public static MainWindow get()
    {
        return INSTANCE;
    }
}
