package com.enonic.xp.launcher.ui.util;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;

public final class Browser
{
    public static void launch( final String url )
    {
        try
        {
            launch( new URL( url ) );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public static void launch( final URL url )
    {
        try
        {
            launch( url.toURI() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public static void launch( final URI uri )
    {
        try
        {
            Desktop.getDesktop().browse( uri );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
