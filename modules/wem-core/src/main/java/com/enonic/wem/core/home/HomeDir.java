package com.enonic.wem.core.home;

import java.io.File;
import java.net.URI;
import java.util.Properties;

public final class HomeDir
{
    private static HomeDir CURRENT;

    private final File dir;

    public HomeDir( final File dir )
    {
        this.dir = dir.getAbsoluteFile();
        CURRENT = this;
    }

    public File toFile()
    {
        return this.dir;
    }

    public URI toUri()
    {
        return this.dir.toURI();
    }

    public Properties toProperties()
    {
        final Properties props = new Properties();
        props.setProperty( "cms.home", toFile().toString() );
        props.setProperty( "cms.home.uri", toUri().toString() );
        return props;
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
