package com.enonic.wem.launcher.home;

import java.io.File;
import java.net.URI;
import java.util.Properties;

public final class HomeDir
    implements HomeConstants
{
    private final File dir;

    public HomeDir( final File dir )
    {
        this.dir = dir.getAbsoluteFile();
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
        props.setProperty( HOME_DIR_PROP, toFile().toString() );
        props.setProperty( HOME_DIR_URI_PROP, toUri().toString() );
        return props;
    }

    public String toString()
    {
        return this.dir.toString();
    }
}
