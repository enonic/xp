package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.core.content.dao.AbstractContentDaoHandler;
import com.enonic.wem.core.jcr.JcrHelper;
import com.enonic.wem.core.support.dao.AbstractDaoHandler;


abstract class AbstractRelationshipDaoHandler<T>
    extends AbstractDaoHandler<T>
{
    protected final Session session;

    protected final RelationshipJcrMapper relationshipJcrMapper = new RelationshipJcrMapper();

    protected ContentDaoHandler contentDaoHandler;

    AbstractRelationshipDaoHandler( final Session session )
    {
        this.session = session;
        contentDaoHandler = new ContentDaoHandler( session );
    }

    protected final Node getRelationshipNode( final RelationshipId relationshipId )
        throws RepositoryException
    {
        return session.getNodeByIdentifier( relationshipId.toString() );
    }

    protected final boolean relationshipExists( final RelationshipId relationshipId )
        throws RepositoryException
    {
        return session.getNodeByIdentifier( relationshipId.toString() ) != null;
    }

    protected final Node getRelationshipNode( final RelationshipKey relationshipKey )
        throws RepositoryException
    {
        final Node fromContentNode = contentDaoHandler.getContentNode( relationshipKey.getFromContent() );
        if ( fromContentNode == null )
        {
            return null;
        }

        final Node relationshipsNode = JcrHelper.getNodeOrNull( fromContentNode, RelationshipDao.RELATIONSHIPS_NODE );
        if ( relationshipsNode == null )
        {
            return null;
        }

        final Node moduleNode = JcrHelper.getNodeOrNull( relationshipsNode, relationshipKey.getType().getModuleName().toString() );
        if ( moduleNode == null )
        {
            return null;
        }

        final Node relationshipTypeNameNode = JcrHelper.getNodeOrNull( moduleNode, relationshipKey.getType().getLocalName() );
        if ( relationshipTypeNameNode == null )
        {
            return null;
        }

        if ( relationshipKey.getManagingData() != null )
        {
            final Node entryPathNode = relationshipTypeNameNode.getNode( relationshipKey.getManagingData().toString() );
            return entryPathNode.getNode( RelationshipDao.TO_CONTENT_NODE_PREFIX + relationshipKey.getToContent().toString() );
        }
        else
        {
            return relationshipTypeNameNode.getNode( RelationshipDao.TO_CONTENT_NODE_PREFIX + relationshipKey.getToContent().toString() );
        }
    }

    private class ContentDaoHandler
        extends AbstractContentDaoHandler
    {
        ContentDaoHandler( final Session session )
        {
            super( session );
        }

        private Node getContentNode( ContentId contentId )
            throws RepositoryException
        {
            return doGetContentNode( contentId );
        }
    }
}
