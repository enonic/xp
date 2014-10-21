package com.enonic.wem.core.home;

import java.io.File;

public final class HomeDir
{
    private final static HomeDir CURRENT = new HomeDir();

    private final File dir;

    private HomeDir()
    {
        final File file = new File( System.getProperty( "karaf.home" ) );
        this.dir = new File( file, "wem.home" );
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
}
