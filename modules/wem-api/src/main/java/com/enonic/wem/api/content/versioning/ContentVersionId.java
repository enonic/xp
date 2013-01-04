package com.enonic.wem.api.content.versioning;

import com.google.common.primitives.Longs;

public final class ContentVersionId
{
    private static final ContentVersionId INITIAL_VERSION_ID = new ContentVersionId( 0 );

    private final long id;

    private ContentVersionId( final long id )
    {
        this.id = id;
    }

    public long id()
    {
        return id;
    }

    public static ContentVersionId initial()
    {
        return INITIAL_VERSION_ID;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ContentVersionId ) && ( (ContentVersionId) o ).id == this.id;
    }

    @Override
    public int hashCode()
    {
        return Longs.hashCode( id );
    }

    @Override
    public String toString()
    {
        return Long.toString( id );
    }

    public static ContentVersionId of( final long id )
    {
        return new ContentVersionId( id );
    }
}
