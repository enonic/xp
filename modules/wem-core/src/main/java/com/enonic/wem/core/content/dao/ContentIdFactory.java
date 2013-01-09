package com.enonic.wem.core.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;

public final class ContentIdFactory
    implements ContentId
{
    private final String id;

    private ContentIdFactory( final String id )
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

    static ContentId from( final Node node )
    {
        try
        {
            return new ContentIdFactory( node.getIdentifier() );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static ContentId from( final String id )
    {
        return new ContentIdFactory( id );
    }
}
