package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.RelationshipId;


final class RelationshipDaoHandlerDelete
    extends AbstractRelationshipDaoHandler
{
    private RelationshipId relationshipId;

    RelationshipDaoHandlerDelete( final Session session )
    {
        super( session );
    }

    RelationshipDaoHandlerDelete relationship( RelationshipId relationship )
    {
        this.relationshipId = relationship;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        Node node = session.getNodeByIdentifier( relationshipId.toString() );
        node.remove();
    }
}
