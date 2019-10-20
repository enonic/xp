package com.enonic.xp.launcher.impl.provision;

import java.io.File;
import java.net.URI;
import java.util.Objects;

final class BundleInfo
    implements Comparable<BundleInfo>
{
    private final File location;

    private final int level;

    BundleInfo( final File location, final int level )
    {
        this.location = location;
        this.level = level;
    }

    String getLocation()
    {
        return this.location.toURI().toString();
    }

    int getLevel()
    {
        return this.level;
    }

    URI getUri()
    {
        return this.location.toURI();
    }

    @Override
    public int compareTo( final BundleInfo o )
    {
        if ( this.level < o.level )
        {
            return -1;
        }
        else if ( this.level > o.level )
        {
            return 1;
        }

        return this.location.compareTo( o.location );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof BundleInfo ) )
        {
            return false;
        }
        final BundleInfo that = (BundleInfo) o;
        return level == that.level && location.equals( that.location );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( location, level );
    }

    @Override
    public String toString()
    {
        return this.location.getName() + "@" + this.level;
    }
}
