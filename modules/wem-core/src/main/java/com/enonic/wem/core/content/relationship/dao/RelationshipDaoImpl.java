package com.enonic.wem.core.content.relationship.dao;


import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipIds;
import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.api.content.relationship.Relationships;

/**
 * A Relationship is stored on the following path:
 * <path-to-fromContent>/relationships/<relationshipType-moduleName>/<relationshipType-localName>/[managingData/]<toContent-id>
 * Note: the managingData part is only included if set
 */

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
        handler.relationshipId( relationshipId );
        handler.handle();
    }

    @Override
    public void delete( final RelationshipKey relationshipKey, final Session session )
    {
        final RelationshipDaoHandlerDelete handler = new RelationshipDaoHandlerDelete( session );
        handler.relationshipKey( relationshipKey );
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
    public Relationship select( final RelationshipKey relationshipKey, final Session session )
    {
        final RelationshipDaoHandlerSelectByKey handler = new RelationshipDaoHandlerSelectByKey( session );
        handler.relationshipKey( relationshipKey );
        handler.handle();
        return handler.getResult();
    }

    @Override
    public Relationships selectFromContent( final ContentId fromContent, final Session session )
    {
        final RelationshipDaoHandlerSelectByFromContent handler = new RelationshipDaoHandlerSelectByFromContent( session );
        handler.fromContent( fromContent );
        handler.handle();
        return handler.getResult();
    }
}
