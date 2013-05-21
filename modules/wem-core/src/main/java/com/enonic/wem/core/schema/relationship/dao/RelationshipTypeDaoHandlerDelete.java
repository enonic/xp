package com.enonic.wem.core.schema.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.RelationshipTypeNotFoundException;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

final class RelationshipTypeDaoHandlerDelete
    extends AbstractRelationshipTypeDaoHandler

{
    private QualifiedRelationshipTypeName qualifiedRelationshipTypeName;

    RelationshipTypeDaoHandlerDelete qualifiedRelationshipTypeName( final QualifiedRelationshipTypeName qualifiedRelationshipTypeName )
    {
        this.qualifiedRelationshipTypeName = qualifiedRelationshipTypeName;
        return this;
    }

    RelationshipTypeDaoHandlerDelete( final Session session )
    {
        super( session );
    }

    protected void doHandle()
        throws RepositoryException
    {
        final Node relationshipTypeNode = getRelationshipTypeNode( qualifiedRelationshipTypeName );

        if ( relationshipTypeNode == null )
        {
            throw new RelationshipTypeNotFoundException( qualifiedRelationshipTypeName );
        }

        relationshipTypeNode.remove();
    }
}
