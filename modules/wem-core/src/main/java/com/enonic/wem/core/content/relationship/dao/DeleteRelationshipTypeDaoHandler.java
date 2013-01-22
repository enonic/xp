package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.exception.SystemException;

final class DeleteRelationshipTypeDaoHandler
    extends AbstractRelationshipTypeDaoHandler

{
    DeleteRelationshipTypeDaoHandler( final Session session )
    {
        super( session );
    }

    void handle( final QualifiedRelationshipTypeName qName )
        throws RepositoryException
    {
        final Node relationshipTypeNode = getRelationshipTypeNode( qName );

        if ( relationshipTypeNode == null )
        {
            throw new SystemException( "RelationshipType [{0}] was not found", qName );
        }

        relationshipTypeNode.remove();
    }
}
