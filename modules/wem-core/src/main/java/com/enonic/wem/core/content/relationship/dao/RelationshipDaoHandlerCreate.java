package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipId;
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
        final Node moduleNode = JcrHelper.getOrAddNode( relationshipsNode, relationship.getType().getModuleName().toString() );
        final Node relationshipTypeNameNode = JcrHelper.getOrAddNode( moduleNode, relationship.getType().getLocalName() );
        if ( relationship.getManagingData() != null )
        {
            final Node entryPathNode = JcrHelper.getOrAddNode( relationshipTypeNameNode, relationship.getManagingData().toString() );
            return entryPathNode.addNode( relationship.getToContent().toString(), JcrConstants.RELATIONSHIP_NODETYPE );
        }
        else
        {
            return relationshipTypeNameNode.addNode( relationship.getToContent().toString(), JcrConstants.RELATIONSHIP_NODETYPE );
        }
    }
}
