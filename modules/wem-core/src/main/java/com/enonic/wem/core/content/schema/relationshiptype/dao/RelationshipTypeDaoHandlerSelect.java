package com.enonic.wem.core.content.schema.relationshiptype.dao;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.schema.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.schema.relationshiptype.RelationshipTypes;
import com.enonic.wem.core.jcr.JcrHelper;

final class RelationshipTypeDaoHandlerSelect
    extends AbstractRelationshipTypeDaoHandler<RelationshipTypes>
{
    private QualifiedRelationshipTypeNames qualifiedNames;

    RelationshipTypeDaoHandlerSelect( final Session session )
    {
        super( session );
    }

    public RelationshipTypeDaoHandlerSelect selectors( final QualifiedRelationshipTypeNames qualifiedNames )
    {
        this.qualifiedNames = qualifiedNames;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        if ( qualifiedNames != null )
        {
            setResult( select( qualifiedNames ) );
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

    private RelationshipTypes select( final QualifiedRelationshipTypeNames qualifiedNames )
        throws RepositoryException
    {
        final List<RelationshipType> relationshipTypeList = Lists.newArrayList();
        for ( QualifiedRelationshipTypeName relationshipTypeName : qualifiedNames )
        {
            final RelationshipType relationshipType = getRelationshipType( relationshipTypeName );
            if ( relationshipType != null )
            {
                relationshipTypeList.add( relationshipType );
            }
        }
        return RelationshipTypes.from( relationshipTypeList );
    }

    private RelationshipType getRelationshipType( final QualifiedRelationshipTypeName qualifiedName )
        throws RepositoryException
    {
        final Node relationshipTypeNode = getRelationshipTypeNode( qualifiedName );
        if ( relationshipTypeNode == null )
        {
            return null;
        }

        return this.relationshipTypeJcrMapper.toRelationshipType( relationshipTypeNode );
    }
}
