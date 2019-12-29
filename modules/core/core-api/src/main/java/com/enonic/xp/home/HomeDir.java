package com.enonic.xp.home;


import java.io.File;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
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
        if ( isNullOrEmpty( str ) )
        {
            throw new IllegalArgumentException( "Home dir [xp.home] is not set." );
        }

        final File dir = new File( str );
        return new HomeDir( dir );
    }

    private static String getHomeProperty()
    {
        return System.getProperty( "xp.home" );
    }
}
