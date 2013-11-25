package com.enonic.wem.core.relationship.dao;


import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.relationship.RelationshipIds;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.core.index.IndexService;

/**
 * A Relationship is stored on the following path:
 * <path-to-fromContent>/relationships/<relationshipType-name>/[managingData/]<toContent-id>
 * Note: the managingData part is only included if set
 */

public class RelationshipDaoImpl
    implements RelationshipDao
{

    private IndexService indexService;

    public RelationshipId create( final Relationship relationship, final Session session )
    {
        final RelationshipDaoHandlerCreate handler = new RelationshipDaoHandlerCreate( session, this.indexService );
        handler.relationship( relationship );
        handler.handle();
        return handler.getResult();
    }

    @Override
    public void update( final Relationship relationship, final Session session )
    {
        final RelationshipDaoHandlerUpdate handler = new RelationshipDaoHandlerUpdate( session, this.indexService );
        handler.relationship( relationship );
        handler.handle();
    }

    @Override
    public void delete( final RelationshipId relationshipId, final Session session )
    {
        final RelationshipDaoHandlerDelete handler = new RelationshipDaoHandlerDelete( session, this.indexService );
        handler.relationshipId( relationshipId );
        handler.handle();
    }

    @Override
    public void delete( final RelationshipKey relationshipKey, final Session session )
    {
        final RelationshipDaoHandlerDelete handler = new RelationshipDaoHandlerDelete( session, this.indexService );
        handler.relationshipKey( relationshipKey );
        handler.handle();
    }

    @Override
    public RelationshipIds exists( final RelationshipIds relationshipIds, final Session session )
    {
        final RelationshipDaoHandlerExists handler = new RelationshipDaoHandlerExists( session, this.indexService );
        handler.relationshipIds( relationshipIds );
        handler.handle();
        return handler.getResult();
    }

    @Override
    public Relationship select( final RelationshipKey relationshipKey, final Session session )
    {
        final RelationshipDaoHandlerSelectByKey handler = new RelationshipDaoHandlerSelectByKey( session, this.indexService );
        handler.relationshipKey( relationshipKey );
        handler.handle();
        return handler.getResult();
    }

    @Override
    public Relationships selectFromContent( final ContentId fromContent, final Session session )
    {
        final RelationshipDaoHandlerSelectByFromContent handler =
            new RelationshipDaoHandlerSelectByFromContent( session, this.indexService );
        handler.fromContent( fromContent );
        handler.handle();
        return handler.getResult();
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
