package com.enonic.wem.api.content;

import com.google.common.base.Preconditions;

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
    public String toString()
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
        return id.equals( that.toString() );
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    public static ContentId from( final String id )
    {
        return new MockContentId( id );
    }
}
