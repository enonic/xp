package com.enonic.wem.core.schema.relationship.dao;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.jcr.JcrHelper;

final class RelationshipTypeDaoHandlerSelect
    extends AbstractRelationshipTypeDaoHandler<RelationshipTypes>
{
    private RelationshipTypeNames relationshipTypeNames;

    RelationshipTypeDaoHandlerSelect( final Session session )
    {
        super( session );
    }

    public RelationshipTypeDaoHandlerSelect selectors( final RelationshipTypeNames names )
    {
        this.relationshipTypeNames = names;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        if ( relationshipTypeNames != null )
        {
            setResult( select( relationshipTypeNames ) );
        }
        else
        {
            setResult( selectAll() );
        }
    }

    private RelationshipTypes selectAll()
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node relationshipTypesNode = JcrHelper.getNodeOrNull( rootNode, RelationshipTypeDao.RELATIONSHIP_TYPES_PATH );

        final List<RelationshipType> relationshipTypeList = Lists.newArrayList();
        final NodeIterator relationshipTypeNodes = relationshipTypesNode.getNodes();
        while ( relationshipTypeNodes.hasNext() )
        {
            final Node relationshipTypeNode = relationshipTypeNodes.nextNode();
            final RelationshipType relationshipType = this.relationshipTypeJcrMapper.toRelationshipType( relationshipTypeNode );
            relationshipTypeList.add( relationshipType );
        }
        return RelationshipTypes.from( relationshipTypeList );
    }

    private RelationshipTypes select( final RelationshipTypeNames names )
        throws RepositoryException
    {
        final List<RelationshipType> relationshipTypeList = Lists.newArrayList();
        for ( RelationshipTypeName relationshipTypeName : names )
        {
            final RelationshipType relationshipType = getRelationshipType( relationshipTypeName );
            if ( relationshipType != null )
            {
                relationshipTypeList.add( relationshipType );
            }
        }
        return RelationshipTypes.from( relationshipTypeList );
    }

    private RelationshipType getRelationshipType( final RelationshipTypeName name )
        throws RepositoryException
    {
        final Node relationshipTypeNode = getRelationshipTypeNode( name );
        if ( relationshipTypeNode == null )
        {
            return null;
        }

        return this.relationshipTypeJcrMapper.toRelationshipType( relationshipTypeNode );
    }
}
