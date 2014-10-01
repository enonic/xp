package com.enonic.wem.core.home;

import java.io.File;

public final class HomeResolver
{
    public HomeDir resolve()
    {
        final String karafHome = System.getProperty( "karaf.home" );
        return new HomeDir( new File( karafHome, "wem.home" ) );
    }
}
