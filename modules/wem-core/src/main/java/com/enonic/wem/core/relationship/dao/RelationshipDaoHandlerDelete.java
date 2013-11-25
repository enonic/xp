package com.enonic.wem.core.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.relationship.RelationshipNotFoundException;
import com.enonic.wem.core.index.IndexService;


final class RelationshipDaoHandlerDelete
    extends AbstractRelationshipDaoHandler
{
    private RelationshipId relationshipId;

    private RelationshipKey relationshipKey;

    RelationshipDaoHandlerDelete( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    RelationshipDaoHandlerDelete relationshipKey( final RelationshipKey relationship )
    {
        this.relationshipKey = relationship;
        return this;
    }

    RelationshipDaoHandlerDelete relationshipId( final RelationshipId relationship )
    {
        this.relationshipId = relationship;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        if ( relationshipId != null )
        {
            final Node node = getRelationshipNode( relationshipId );
            if ( node != null )
            {
                node.remove();
            }
            else
            {
                throw new RelationshipNotFoundException( relationshipId );
            }
        }
        else if ( relationshipKey != null )
        {
            final Node node = getRelationshipNode( relationshipKey );
            if ( node != null )
            {
                node.remove();
            }
            else
            {
                throw new RelationshipNotFoundException( relationshipKey );
            }
        }
        else
        {
            throw new IllegalArgumentException( "Neither relationshipId or relationshipKey was specified!" );
        }


    }
}
