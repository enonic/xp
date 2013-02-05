package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipIds;
import com.enonic.wem.api.content.relationship.RelationshipSelectors;
import com.enonic.wem.api.content.relationship.Relationships;


final class RelationshipDaoHandlerSelect
    extends AbstractRelationshipDaoHandler<Relationships>
{
    private RelationshipSelectors selectors;

    RelationshipDaoHandlerSelect( final Session session )
    {
        super( session );
    }

    RelationshipDaoHandlerSelect selectors( RelationshipSelectors selectors )
    {
        this.selectors = selectors;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        if ( selectors instanceof RelationshipIds )
        {
            handleSelectByRelationshipIds();
        }
        else
        {
            throw new UnsupportedOperationException( "selector [" + selectors.getClass().getSimpleName() + " ] not supported" );
        }
    }

    private void handleSelectByRelationshipIds()
        throws RepositoryException
    {
        final Relationships.Builder relationshipsBuilder = Relationships.newRelationships();
        final RelationshipIds relationshipIds = (RelationshipIds) selectors;
        for ( RelationshipId relationshipId : relationshipIds )
        {
            final Node node = getRelationshipNode( relationshipId );
            final Relationship relationship = relationshipJcrMapper.toRelationship( node );
            relationshipsBuilder.add( relationship );
        }
        setResult( relationshipsBuilder.build() );
    }
}
