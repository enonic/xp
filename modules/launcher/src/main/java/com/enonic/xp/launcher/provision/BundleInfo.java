package com.enonic.xp.launcher.provision;

import java.io.File;
import java.net.URI;

final class BundleInfo
{
    private final String location;

    private final int level;

    public BundleInfo( final String location, final int level )
    {
        this.location = location;
        this.level = level;
    }

    public String getLocation()
    {
        return this.location;
    }

    public int getLevel()
    {
        return this.level;
    }

    public URI getUri( final File baseDir )
    {
        final URI uri = URI.create( this.location );
        if ( uri.getScheme() == null )
        {
            return URI.create( baseDir.toURI().toString() + this.location );
        }

        return uri;
    }
}
