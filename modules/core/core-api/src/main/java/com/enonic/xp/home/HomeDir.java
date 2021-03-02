package com.enonic.xp.home;


import java.io.File;
import java.nio.file.Path;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class HomeDir
{
    private final Path dir;

    private HomeDir( final Path dir )
    {
        this.dir = dir;
    }

    public File toFile()
    {
        return this.dir.toFile();
    }

    public Path toPath()
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

        return new HomeDir( Path.of( str ).toAbsolutePath().normalize() );
    }

    private static String getHomeProperty()
    {
        return System.getProperty( "xp.home" );
    }
}
