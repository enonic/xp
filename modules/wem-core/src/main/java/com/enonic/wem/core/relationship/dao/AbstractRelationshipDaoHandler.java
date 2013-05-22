package com.enonic.wem.core.relationship.dao;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.util.TraversingItemVisitor;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.DataPath;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.relationship.Relationships;
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
            final Node managingDataNode = JcrHelper.getNodeOrNull( relationshipTypeNameNode, RelationshipDao.MANAGING_DATA_NODE );
            final Node lastPathElementNode = getManagingDataNode( relationshipKey.getManagingData(), managingDataNode );
            return lastPathElementNode.getNode( RelationshipDao.TO_CONTENT_NODE_PREFIX + relationshipKey.getToContent().toString() );
        }
        else
        {
            return relationshipTypeNameNode.getNode( RelationshipDao.TO_CONTENT_NODE_PREFIX + relationshipKey.getToContent().toString() );
        }
    }

    protected final Relationships getRelationships( final ContentId fromContent )
        throws RepositoryException
    {
        final Relationships.Builder relationships = Relationships.newRelationships();
        final Node fromContentNode = contentDaoHandler.getContentNode( fromContent );
        if ( fromContentNode == null )
        {
            return relationships.build();
        }

        final Node relationshipsNode = JcrHelper.getNodeOrNull( fromContentNode, RelationshipDao.RELATIONSHIPS_NODE );
        if ( relationshipsNode == null )
        {
            return relationships.build();
        }

        final NodeIterator moduleNodeIterator = relationshipsNode.getNodes();
        while ( moduleNodeIterator.hasNext() )
        {
            final Node moduleNode = moduleNodeIterator.nextNode();

            final NodeIterator relationshipTypeNameIterator = moduleNode.getNodes();
            while ( relationshipTypeNameIterator.hasNext() )
            {
                final Node relationshipTypeNameNode = relationshipTypeNameIterator.nextNode();

                final NodeIterator nodeIterator = relationshipTypeNameNode.getNodes();
                while ( nodeIterator.hasNext() )
                {
                    final Node node = nodeIterator.nextNode();
                    if ( node.getName().startsWith( RelationshipDao.TO_CONTENT_NODE_PREFIX ) )
                    {
                        final Relationship relationship = relationshipJcrMapper.toRelationship( node );
                        relationships.add( relationship );
                    }
                    else if ( node.getName().equals( RelationshipDao.MANAGING_DATA_NODE ) )
                    {
                        TraversingItemVisitor visitor = new TraversingItemVisitor.Default()
                        {
                            @Override
                            protected void entering( final Node node, final int level )
                                throws RepositoryException
                            {

                                if ( node.getName().startsWith( RelationshipDao.TO_CONTENT_NODE_PREFIX ) )
                                {
                                    final Relationship relationship = relationshipJcrMapper.toRelationship( node );
                                    relationships.add( relationship );
                                }
                            }
                        };
                        visitor.visit( node );

                    }
                }
            }
        }

        return relationships.build();
    }

    private Node getManagingDataNode( final DataPath dataPath, final Node parentNode )
        throws RepositoryException
    {
        final DataPath.Element firstElement = dataPath.getFirstElement();
        Node pathElementNameNode = JcrHelper.getNodeOrNull( parentNode, firstElement.getName() );
        if ( pathElementNameNode == null )
        {
            return null;
        }
        final int index = firstElement.hasIndex() ? firstElement.getIndex() : 0;
        Node elementIndexNode = JcrHelper.getNodeOrNull( pathElementNameNode, "__index-" + index );

        if ( elementIndexNode == null )
        {
            return null;
        }

        if ( dataPath.elementCount() == 1 )
        {
            return elementIndexNode;
        }
        else
        {
            return getManagingDataNode( dataPath.asNewWithoutFirstPathElement(), elementIndexNode );
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
