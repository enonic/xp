package com.enonic.xp.home;


import java.io.File;

import com.google.common.annotations.Beta;
import com.google.common.base.Strings;

@Beta
public final class HomeDir
{
    private final File dir;

    private HomeDir( final File dir )
    {
        this.dir = dir;
    }

    public File toFile()
    {
        return this.dir;
    }

    @Override
    public String toString()
    {
        return this.dir.toString();
    }

    public static HomeDir get()
    {
        final String str = getHomeProperty();
        if ( Strings.isNullOrEmpty( str ) )
        {
            throw new IllegalArgumentException( "Home dir [xp.home] is not set." );
        }

        final File dir = new File( str );
        return new HomeDir( dir );
    }

    private static String getHomeProperty()
    {
        final String str = System.getProperty( "xp.home" );
        if ( Strings.isNullOrEmpty( str ) )
        {
            return System.getProperty( "wem.home" );
        }

        return str;
    }
}
