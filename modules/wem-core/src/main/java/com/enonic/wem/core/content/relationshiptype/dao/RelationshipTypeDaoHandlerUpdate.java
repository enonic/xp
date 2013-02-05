package com.enonic.wem.core.content.relationshiptype.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.exception.RelationshipTypeNotFoundException;

final class RelationshipTypeDaoHandlerUpdate
    extends AbstractRelationshipTypeDaoHandler
{
    private RelationshipType relationshipType;

    RelationshipTypeDaoHandlerUpdate( final Session session )
    {
        super( session );
    }

    RelationshipTypeDaoHandlerUpdate relationshipType( final RelationshipType relationshipType )
    {
        this.relationshipType = relationshipType;
        return this;
    }

    protected final void doHandle()
        throws RepositoryException
    {
        final QualifiedRelationshipTypeName qualifiedName = relationshipType.getQualifiedName();
        final Node relationshipTypeNode = getRelationshipTypeNode( qualifiedName );
        if ( relationshipTypeNode == null )
        {
            throw new RelationshipTypeNotFoundException( qualifiedName );
        }

        relationshipTypeJcrMapper.toJcr( relationshipType, relationshipTypeNode );
    }

}
