package com.enonic.wem.api.home;

import java.io.File;

public final class HomeDir
{
    private final static HomeDir CURRENT = new HomeDir();

    private final File dir;

    private HomeDir()
    {
        this.dir = resolveHome();
        if ( this.dir == null )
        {
            throw new IllegalArgumentException( "Home dir is not set. Set either wem.home or karaf.home." );
        }
    }

    public File toFile()
    {
        return this.dir;
    }

    public String toString()
    {
        return this.dir.toString();
    }

    public static HomeDir get()
    {
        return CURRENT;
    }

    private File resolveHome()
    {
        final File wemHome = resolveDir( "wem.home", null );
        if ( wemHome != null )
        {
            return wemHome;
        }

        return resolveDir( "karaf.home", "wem.home" );
    }

    private File resolveDir( final String propName, final String subPath )
    {
        final String propValue = System.getProperty( propName );
        if ( propValue == null )
        {
            return null;
        }

        if ( subPath != null )
        {
            return new File( propValue, subPath );
        }
        else
        {
            return new File( propValue );
        }

    }
}
