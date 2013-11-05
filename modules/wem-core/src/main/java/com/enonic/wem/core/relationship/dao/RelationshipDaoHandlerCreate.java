package com.enonic.wem.core.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.core.jcr.JcrConstants;
import com.enonic.wem.core.jcr.JcrHelper;


final class RelationshipDaoHandlerCreate
    extends AbstractRelationshipDaoHandler<RelationshipId>
{
    private Relationship relationship;

    RelationshipDaoHandlerCreate( final Session session )
    {
        super( session );
    }

    RelationshipDaoHandlerCreate relationship( Relationship relationship )
    {
        this.relationship = relationship;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        final Node relationshipNode = createRelationshipNode();

        relationshipJcrMapper.toJcr( relationship, relationshipNode );
        setResult( RelationshipIdFactory.from( relationshipNode ) );
    }

    private Node createRelationshipNode()
        throws RepositoryException
    {
        final Node fromContentNode = session.getNodeByIdentifier( relationship.getFromContent().toString() );
        final Node relationshipsNode = JcrHelper.getOrAddNode( fromContentNode, RelationshipDao.RELATIONSHIPS_NODE );
        final Node relationshipTypeNameNode = JcrHelper.getOrAddNode( relationshipsNode, relationship.getType().toString() );
        if ( relationship.getManagingData() != null )
        {
            final Node managingDataNode = JcrHelper.getOrAddNode( relationshipTypeNameNode, RelationshipDao.MANAGING_DATA_NODE );
            final Node lastPathElementNode = createManagingDataNode( relationship.getManagingData(), managingDataNode );
            return lastPathElementNode.addNode( RelationshipDao.TO_CONTENT_NODE_PREFIX + relationship.getToContent().toString(),
                                                JcrConstants.RELATIONSHIP_NODETYPE );
        }
        else
        {
            return relationshipTypeNameNode.addNode( RelationshipDao.TO_CONTENT_NODE_PREFIX + relationship.getToContent().toString(),
                                                     JcrConstants.RELATIONSHIP_NODETYPE );
        }
    }

    private Node createManagingDataNode( final DataPath dataPath, final Node parentNode )
        throws RepositoryException
    {
        final DataPath.Element firstElement = dataPath.getFirstElement();
        Node childNode = JcrHelper.getOrAddNode( parentNode, firstElement.getName() );
        final int index = firstElement.hasIndex() ? firstElement.getIndex() : 0;
        childNode = JcrHelper.getOrAddNode( childNode, "__index-" + index );

        if ( dataPath.elementCount() == 1 )
        {
            return childNode;
        }
        else
        {
            return createManagingDataNode( dataPath.asNewWithoutFirstPathElement(), childNode );
        }
    }
}
