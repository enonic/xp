package com.enonic.wem.core.schema.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.RelationshipTypeNotFoundException;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

final class RelationshipTypeDaoHandlerDelete
    extends AbstractRelationshipTypeDaoHandler

{
    private RelationshipTypeName relationshipTypeName;

    RelationshipTypeDaoHandlerDelete relationshipTypeName( final RelationshipTypeName relationshipTypeName )
    {
        this.relationshipTypeName = relationshipTypeName;
        return this;
    }

    RelationshipTypeDaoHandlerDelete( final Session session )
    {
        super( session );
    }

    protected void doHandle()
        throws RepositoryException
    {
        final Node relationshipTypeNode = getRelationshipTypeNode( relationshipTypeName );

        if ( relationshipTypeNode == null )
        {
            throw new RelationshipTypeNotFoundException( relationshipTypeName );
        }

        relationshipTypeNode.remove();
    }
}
