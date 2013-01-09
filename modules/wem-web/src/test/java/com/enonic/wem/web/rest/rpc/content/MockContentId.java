package com.enonic.wem.web.rest.rpc.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;

public final class MockContentId
    implements ContentId
{
    private final String id;

    private MockContentId( final String id )
    {
        Preconditions.checkNotNull( id );
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

    static ContentId from( final String id )
    {
        return new MockContentId( id );
    }
}
