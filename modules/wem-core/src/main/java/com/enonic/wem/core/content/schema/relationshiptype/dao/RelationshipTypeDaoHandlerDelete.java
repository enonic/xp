package com.enonic.wem.core.content.schema.relationshiptype.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.exception.SystemException;

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
            throw new SystemException( "RelationshipType [{0}] was not found", qualifiedRelationshipTypeName );
        }

        relationshipTypeNode.remove();
    }
}
