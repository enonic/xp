package com.enonic.wem.core.content.dao;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;

public final class ContentIdImpl
    implements ContentId
{
    private final String id;

    ContentIdImpl( final String id )
    {
        Preconditions.checkNotNull( id, "Content id cannot be null" );
        this.id = id;
    }

    @Override
    public String id()
    {
        return this.id;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentId ) )
        {
            return false;
        }

        final ContentId that = (ContentId) o;
        return id.equals( that.id() );
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public String toString()
    {
        return id;
    }
}
