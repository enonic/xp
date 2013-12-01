package com.enonic.wem.core.schema.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.core.jcr.JcrHelper;
import com.enonic.wem.core.support.dao.AbstractDaoHandler;


abstract class AbstractRelationshipTypeDaoHandler<T>
    extends AbstractDaoHandler<T>
{
    protected final Session session;

    protected final RelationshipTypeJcrMapper relationshipTypeJcrMapper = new RelationshipTypeJcrMapper();

    AbstractRelationshipTypeDaoHandler( final Session session )
    {
        this.session = session;
    }

    protected boolean nodeExists( final RelationshipTypeName relationshipTypeName )
        throws RepositoryException
    {
        final Node node = this.getRelationshipTypeNode( relationshipTypeName );
        if ( node == null )
        {
            return false;
        }

        return true;
    }

    protected final Node getRelationshipTypeNode( final RelationshipTypeName relationshipTypeName )
        throws RepositoryException
    {
        final String path = getNodePath( relationshipTypeName );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
    }

    protected final String getNodePath( final RelationshipTypeName relationshipTypeName )
    {
        return RelationshipTypeDao.RELATIONSHIP_TYPES_PATH + relationshipTypeName.toString();
    }

    protected final boolean relationshipTypeExists( final RelationshipTypeName relationshipTypeName )
        throws RepositoryException
    {
        final String relationshipTypePath = getNodePath( relationshipTypeName );
        return session.getRootNode().hasNode( relationshipTypePath );
    }
}
