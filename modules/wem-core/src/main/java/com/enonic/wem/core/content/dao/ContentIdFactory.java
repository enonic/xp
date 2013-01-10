package com.enonic.wem.core.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.ContentId;

public final class ContentIdFactory
{
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

    public static ContentId from( final String id )
    {
        return new ContentIdImpl( id );
    }
}
