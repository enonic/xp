package com.enonic.xp.index;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;

@PublicApi
public final class IndexPath
{
    private final String path;

    private IndexPath( final String path )
    {
        this.path = path.toLowerCase().trim();
    }

    public static IndexPath from( final String path )
    {
        return new IndexPath( path );
    }

    public static IndexPath from( final Property property )
    {
        return IndexPath.from( property.getPath().resetAllIndexesTo( 0 ).toString() );
    }

    public String getPath()
    {
        return path;
    }

    @Override
    public String toString()
    {
        return this.path;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof IndexPath && this.path.equals( ( (IndexPath) o ).path );
    }

    @Override
    public int hashCode()
    {
        return path.hashCode();
    }
}
