package com.enonic.xp.util;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class Link
{
    private final String path;

    private Link( final String path )
    {
        this.path = path;
    }

    public static Link from( final String path )
    {
        return new Link( path );
    }

    public String getPath()
    {
        return path;
    }

    @Override
    public String toString()
    {
        return path;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Link link = (Link) o;

        return path != null ? path.equals( link.path ) : link.path == null;
    }

    @Override
    public int hashCode()
    {
        return path != null ? path.hashCode() : 0;
    }
}
