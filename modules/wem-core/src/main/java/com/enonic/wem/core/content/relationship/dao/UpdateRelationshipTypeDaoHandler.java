package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.api.exception.SystemException;

final class UpdateRelationshipTypeDaoHandler
    extends AbstractRelationshipTypeDaoHandler
{
    UpdateRelationshipTypeDaoHandler( final Session session )
    {
        super( session );
    }

    void handle( final RelationshipType relationshipType )
        throws RepositoryException
    {
        final QualifiedRelationshipTypeName relationshipTypeName = relationshipType.getQualifiedName();
        final Node relationshipTypeNode = getRelationshipTypeNode( relationshipTypeName );
        if ( relationshipTypeNode == null )
        {
            throw new SystemException( "Relationship type not found: {0}", relationshipTypeName.toString() );
        }

        this.relationshipTypeJcrMapper.toJcr( relationshipType, relationshipTypeNode );
    }

}
