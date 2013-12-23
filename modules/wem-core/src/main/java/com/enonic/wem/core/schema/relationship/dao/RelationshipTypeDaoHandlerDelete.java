package com.enonic.wem.core.schema.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNotFoundException;

final class RelationshipTypeDaoHandlerDelete
    extends AbstractRelationshipTypeDaoHandler<RelationshipType>

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
        final RelationshipType relationshipTypeToDelete =
            new RelationshipTypeDaoHandlerSelect( this.session ).getRelationshipType( relationshipTypeName );

        final Node relationshipTypeNode = getRelationshipTypeNode( relationshipTypeName );

        if ( relationshipTypeNode == null )
        {
            throw new RelationshipTypeNotFoundException( relationshipTypeName );
        }

        relationshipTypeNode.remove();

        setResult( relationshipTypeToDelete );
    }
}
