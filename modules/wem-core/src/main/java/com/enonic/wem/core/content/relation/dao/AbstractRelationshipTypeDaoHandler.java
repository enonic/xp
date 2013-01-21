package com.enonic.wem.core.content.relation.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.core.jcr.JcrHelper;


abstract class AbstractRelationshipTypeDaoHandler
{
    protected final Session session;

    protected final RelationshipTypeJcrMapper relationshipTypeJcrMapper = new RelationshipTypeJcrMapper();

    AbstractRelationshipTypeDaoHandler( final Session session )
    {
        this.session = session;
    }

    protected final Node getRelationshipTypeNode( final QualifiedRelationshipTypeName relationshipTypeName )
        throws RepositoryException
    {
        final String path = getNodePath( relationshipTypeName );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
    }

    protected final String getNodePath( final QualifiedRelationshipTypeName relationshipTypeName )
    {
        return RelationshipTypeDao.RELATIONSHIP_TYPES_PATH + relationshipTypeName.getModuleName() + "/" +
            relationshipTypeName.getLocalName();
    }

    protected final boolean relationshipTypeExists( final QualifiedRelationshipTypeName relationshipTypeName )
        throws RepositoryException
    {
        final String relationshipTypePath = getNodePath( relationshipTypeName );
        return session.getRootNode().hasNode( relationshipTypePath );
    }
}
