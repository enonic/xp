package com.enonic.xp.launcher.ui.util;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;

public final class DesktopHelper
{
    public static void launch( final String url )
    {
        try
        {
            doLaunch( new URL( url ) );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public static void launch( final URL url )
    {
        try
        {
            doLaunch( url );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private static void doLaunch( final URL url )
        throws Exception
    {
        Desktop.getDesktop().browse( url.toURI() );
    }

    public static void open( final File file )
    {
        try
        {
            Desktop.getDesktop().open( file );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
