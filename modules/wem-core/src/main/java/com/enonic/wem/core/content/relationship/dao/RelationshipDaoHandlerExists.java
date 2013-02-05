package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipIds;


final class RelationshipDaoHandlerExists
    extends AbstractRelationshipDaoHandler<RelationshipIds>
{
    private RelationshipIds relationshipIds;

    RelationshipDaoHandlerExists( final Session session )
    {
        super( session );
    }

    RelationshipDaoHandlerExists relationshipIds( RelationshipIds ids )
    {
        this.relationshipIds = ids;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        final RelationshipIds.Builder builder = RelationshipIds.newRelationshipIds();
        for ( RelationshipId relationshipId : relationshipIds )
        {
            if ( relationshipExists( relationshipId ) )
            {
                builder.add( relationshipId );
            }
        }
        setResult( builder.build() );
    }
}
