package com.enonic.wem.core.content.relationship.dao;


import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipIds;
import com.enonic.wem.api.content.relationship.Relationships;

/**
 * A Relationship is stored on the following path:
 * <path-to-fromContent>/relationships/<module-name>/<relationship-type-name>/<to-content-id>
 */
@Component
public class RelationshipDaoImpl
    implements RelationshipDao
{

    public RelationshipId create( final Relationship relationship, final Session session )
    {
        final RelationshipDaoHandlerCreate handler = new RelationshipDaoHandlerCreate( session );
        handler.relationship( relationship );
        handler.handle();
        return handler.getResult();
    }

    @Override
    public void update( final Relationship relationship, final Session session )
    {
        final RelationshipDaoHandlerUpdate handler = new RelationshipDaoHandlerUpdate( session );
        handler.relationship( relationship );
        handler.handle();
    }

    @Override
    public void delete( final RelationshipId relationshipId, final Session session )
    {
        final RelationshipDaoHandlerDelete handler = new RelationshipDaoHandlerDelete( session );
        handler.relationship( relationshipId );
        handler.handle();
    }

    @Override
    public RelationshipIds exists( final RelationshipIds relationshipIds, final Session session )
    {
        final RelationshipDaoHandlerExists handler = new RelationshipDaoHandlerExists( session );
        handler.relationshipIds( relationshipIds );
        handler.handle();
        return handler.getResult();
    }

    @Override
    public Relationships select( final RelationshipIds relationshipIds, final Session session )
    {
        final RelationshipDaoHandlerSelect handler = new RelationshipDaoHandlerSelect( session );
        handler.selectors( relationshipIds );
        return handler.getResult();
    }
}
