package com.enonic.wem.core.content.schema.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationship.RelationshipType;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.jcr.JcrHelper;


final class RelationshipTypeDaoHandlerCreate
    extends AbstractRelationshipTypeDaoHandler
{
    private RelationshipType relationshipType;

    RelationshipTypeDaoHandlerCreate( final Session session )
    {
        super( session );
    }

    RelationshipTypeDaoHandlerCreate relationshipType( final RelationshipType relationshipType )
    {
        this.relationshipType = relationshipType;
        return this;
    }

    protected final void doHandle()
        throws RepositoryException
    {
        final QualifiedRelationshipTypeName relationshipTypeName = relationshipType.getQualifiedName();
        if ( relationshipTypeExists( relationshipTypeName ) )
        {
            throw new SystemException( "RelationshipType already exists: {0}", relationshipTypeName.toString() );
        }

        final Node relationshipTypeNode = createRelationshipTypeNode( relationshipTypeName );
        this.relationshipTypeJcrMapper.toJcr( relationshipType, relationshipTypeNode );
    }

    private Node createRelationshipTypeNode( final QualifiedRelationshipTypeName relationshipTypeName )
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node relationshipTypesNode = rootNode.getNode( RelationshipTypeDao.RELATIONSHIP_TYPES_PATH );
        final Node moduleNode = JcrHelper.getOrAddNode( relationshipTypesNode, relationshipTypeName.getModuleName().toString() );
        return JcrHelper.getOrAddNode( moduleNode, relationshipTypeName.getLocalName() );
    }
}
