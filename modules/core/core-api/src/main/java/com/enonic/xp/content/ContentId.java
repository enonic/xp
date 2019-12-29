package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.Reference;

@PublicApi
public final class ContentId
{
    private final String id;

    private ContentId( final String id )
    {
        this.id = id;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ContentId ) && ( (ContentId) o ).id.equals( this.id );
    }

    @Override
    public int hashCode()
    {
        return this.id.hashCode();
    }

    @Override
    public String toString()
    {
        return this.id;
    }

    public static ContentId from( final String id )
    {
        Preconditions.checkNotNull( id, "ContentId cannot be null" );
        Preconditions.checkArgument( !id.trim().isEmpty(), "ContentId cannot be blank" );
        return new ContentId( id );
    }

    public static ContentId from( final Reference reference )
    {
        return new ContentId( reference.getNodeId().toString() );
    }
}
