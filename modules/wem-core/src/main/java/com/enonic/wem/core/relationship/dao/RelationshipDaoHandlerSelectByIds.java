package com.enonic.wem.core.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.relationship.RelationshipIds;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.core.index.IndexService;


final class RelationshipDaoHandlerSelectByIds
    extends AbstractRelationshipDaoHandler<Relationships>
{
    private RelationshipIds relationshipIds;

    RelationshipDaoHandlerSelectByIds( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    RelationshipDaoHandlerSelectByIds relationshipIds( RelationshipIds relationshipIds )
    {
        this.relationshipIds = relationshipIds;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        final Relationships.Builder relationshipsBuilder = Relationships.newRelationships();
        for ( RelationshipId relationshipId : relationshipIds )
        {
            final Node node = getRelationshipNode( relationshipId );
            final Relationship relationship = relationshipJcrMapper.toRelationship( node );
            relationshipsBuilder.add( relationship );
        }
        setResult( relationshipsBuilder.build() );
    }
}
