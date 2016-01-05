package com.enonic.xp.launcher.ui.util;

import java.net.URL;

import javax.swing.ImageIcon;

public final class Icons
{
    public final static ImageIcon LOGO = loadIcon( "logo.png" );

    public final static ImageIcon LOGO_BIG = loadIcon( "logo_big.png" );

    private static ImageIcon loadIcon( final String iconName )
    {
        final URL url = Icons.class.getResource( iconName );
        if ( url != null )
        {
            return new ImageIcon( url );
        }
        else
        {
            throw new Error( "Failed to load icon [" + iconName + "]" );
        }
    }
}
