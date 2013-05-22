package com.enonic.wem.core.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.relationship.RelationshipId;

public final class RelationshipIdFactory
{
    static RelationshipId from( final Node node )
    {
        try
        {
            return new RelationshipIdImpl( node.getIdentifier() );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static RelationshipId from( final String id )
    {
        return new RelationshipIdImpl( id );
    }
}
