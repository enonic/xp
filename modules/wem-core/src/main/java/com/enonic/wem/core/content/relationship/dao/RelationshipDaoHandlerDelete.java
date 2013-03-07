package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.api.content.relationship.RelationshipNotFoundException;


final class RelationshipDaoHandlerDelete
    extends AbstractRelationshipDaoHandler
{
    private RelationshipId relationshipId;

    private RelationshipKey relationshipKey;

    RelationshipDaoHandlerDelete( final Session session )
    {
        super( session );
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
