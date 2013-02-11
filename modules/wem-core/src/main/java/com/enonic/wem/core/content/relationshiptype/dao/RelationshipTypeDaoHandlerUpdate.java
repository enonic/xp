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
        final Node node = getRelationshipTypeNode( qualifiedName );
        if ( node == null )
        {
            throw new RelationshipTypeNotFoundException( qualifiedName );
        }

        final RelationshipType existing = relationshipTypeJcrMapper.toRelationshipType( node );
        checkIllegalChanges( existing );

        relationshipTypeJcrMapper.toJcr( relationshipType, node );
    }

    private void checkIllegalChanges( final RelationshipType existing )
    {
        checkIllegalChange( "createdTime", existing.getCreatedTime(), relationshipType.getCreatedTime() );

        // Cannot be changes since they are a part of a Relationship's storage path in JCR.
        checkIllegalChange( "name", existing.getName(), relationshipType.getName() );
        checkIllegalChange( "moduleName", existing.getModuleName(), relationshipType.getModuleName() );
    }
}
