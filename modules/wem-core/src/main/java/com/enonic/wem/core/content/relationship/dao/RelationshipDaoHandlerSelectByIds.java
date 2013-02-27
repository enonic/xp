package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipIds;
import com.enonic.wem.api.content.relationship.Relationships;


final class RelationshipDaoHandlerSelectByIds
    extends AbstractRelationshipDaoHandler<Relationships>
{
    private RelationshipIds relationshipIds;

    RelationshipDaoHandlerSelectByIds( final Session session )
    {
        super( session );
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
