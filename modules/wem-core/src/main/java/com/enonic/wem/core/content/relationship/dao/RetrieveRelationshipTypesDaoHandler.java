package com.enonic.wem.core.content.relationship.dao;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationship.RelationshipType;
import com.enonic.wem.api.content.relationship.RelationshipTypeSelector;
import com.enonic.wem.api.content.relationship.RelationshipTypes;
import com.enonic.wem.core.jcr.JcrHelper;

final class RetrieveRelationshipTypesDaoHandler
    extends AbstractRelationshipTypeDaoHandler
{
    RetrieveRelationshipTypesDaoHandler( final Session session )
    {
        super( session );
    }

    RelationshipTypes handle()
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node relationshipTypesNode = JcrHelper.getNodeOrNull( rootNode, RelationshipTypeDao.RELATIONSHIP_TYPES_PATH );

        final List<RelationshipType> relationshipTypeList = Lists.newArrayList();
        final NodeIterator relationshipTypeModuleNodes = relationshipTypesNode.getNodes();
        while ( relationshipTypeModuleNodes.hasNext() )
        {
            final Node relationshipTypeModuleNode = relationshipTypeModuleNodes.nextNode();

            final NodeIterator relationshipTypeNodes = relationshipTypeModuleNode.getNodes();
            while ( relationshipTypeNodes.hasNext() )
            {
                final Node relationshipTypeNode = relationshipTypeNodes.nextNode();
                final RelationshipType relationshipType = this.relationshipTypeJcrMapper.toRelationshipType( relationshipTypeNode );
                relationshipTypeList.add( relationshipType );
            }
        }
        return RelationshipTypes.from( relationshipTypeList );
    }

    RelationshipTypes handle( final RelationshipTypeSelector selector )
        throws RepositoryException
    {
        if ( selector instanceof QualifiedRelationshipTypeNames )
        {
            return handle( (QualifiedRelationshipTypeNames) selector );
        }
        else
        {
            throw new UnsupportedOperationException( "selector [" + selector.getClass().getSimpleName() + " ] not supported" );
        }
    }

    RelationshipTypes handle( final QualifiedRelationshipTypeNames relationshipTypeNames )
        throws RepositoryException
    {
        final List<RelationshipType> relationshipTypeList = Lists.newArrayList();
        for ( QualifiedRelationshipTypeName relationshipTypeName : relationshipTypeNames )
        {
            final RelationshipType relationshipType = retrieveRelationshipType( relationshipTypeName );
            if ( relationshipType != null )
            {
                relationshipTypeList.add( relationshipType );
            }
        }
        return RelationshipTypes.from( relationshipTypeList );
    }

    private RelationshipType retrieveRelationshipType( final QualifiedRelationshipTypeName relationshipTypeName )
        throws RepositoryException
    {
        final Node relationshipTypeNode = this.getRelationshipTypeNode( relationshipTypeName );
        if ( relationshipTypeNode == null )
        {
            return null;
        }

        return this.relationshipTypeJcrMapper.toRelationshipType( relationshipTypeNode );
    }
}
