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
import com.enonic.wem.api.content.relationship.RelationshipTypeSelectors;
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

    RelationshipTypes handle( final RelationshipTypeSelectors selectors )
        throws RepositoryException
    {
        if ( selectors instanceof QualifiedRelationshipTypeNames )
        {
            return handle( (QualifiedRelationshipTypeNames) selectors );
        }
        else
        {
            throw new UnsupportedOperationException( "selector [" + selectors.getClass().getSimpleName() + " ] not supported" );
        }
    }

    RelationshipTypes handle( final QualifiedRelationshipTypeNames qualifiedNames )
        throws RepositoryException
    {
        final List<RelationshipType> relationshipTypeList = Lists.newArrayList();
        for ( QualifiedRelationshipTypeName relationshipTypeName : qualifiedNames )
        {
            final RelationshipType relationshipType = retrieveRelationshipType( relationshipTypeName );
            if ( relationshipType != null )
            {
                relationshipTypeList.add( relationshipType );
            }
        }
        return RelationshipTypes.from( relationshipTypeList );
    }

    private RelationshipType retrieveRelationshipType( final QualifiedRelationshipTypeName qualifiedName )
        throws RepositoryException
    {
        final Node relationshipTypeNode = this.getRelationshipTypeNode( qualifiedName );
        if ( relationshipTypeNode == null )
        {
            return null;
        }

        return this.relationshipTypeJcrMapper.toRelationshipType( relationshipTypeNode );
    }
}
