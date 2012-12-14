package com.enonic.wem.core.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;

final class ContentIdImpl
    implements ContentId
{
    private final String id;

    private ContentIdImpl( final String id )
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
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ContentIdImpl that = (ContentIdImpl) o;
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

    static ContentId from( final Node node )
    {
        try
        {
            return new ContentIdImpl( node.getIdentifier() );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }
}
