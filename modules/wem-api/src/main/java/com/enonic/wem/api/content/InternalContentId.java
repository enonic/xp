package com.enonic.wem.api.content;

import com.google.common.base.Preconditions;

public final class InternalContentId
    implements ContentSelector
{
    private final String id;

    private InternalContentId( final String id )
    {
        Preconditions.checkNotNull( id );
        this.id = id;
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

        final InternalContentId that = (InternalContentId) o;
        return id.equals( that.id );
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

    public static InternalContentId from( final String id )
    {
        return new InternalContentId( id );
    }
}
