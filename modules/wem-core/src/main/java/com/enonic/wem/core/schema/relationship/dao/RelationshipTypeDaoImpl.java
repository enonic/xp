package com.enonic.wem.core.schema.relationship.dao;

import javax.jcr.Session;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;


public final class RelationshipTypeDaoImpl
    implements RelationshipTypeDao
{

    @Override
    public void create( final RelationshipType relationshipType, final Session session )
    {
        new RelationshipTypeDaoHandlerCreate( session ).relationshipType( relationshipType ).handle();
    }

    @Override
    public void update( final RelationshipType relationshipType, final Session session )
    {
        new RelationshipTypeDaoHandlerUpdate( session ).relationshipType( relationshipType ).handle();
    }

    @Override
    public void delete( final RelationshipTypeName qualifiedName, final Session session )
    {
        new RelationshipTypeDaoHandlerDelete( session ).qualifiedRelationshipTypeName( qualifiedName ).handle();
    }

    @Override
    public RelationshipTypeNames exists( final RelationshipTypeNames qNames, final Session session )
    {
        final RelationshipTypeDaoHandlerExists handler = new RelationshipTypeDaoHandlerExists( session ).selectors( qNames );
        handler.handle();
        return handler.getResult();
    }

    @Override
    public RelationshipTypes selectAll( final Session session )
    {
        final RelationshipTypeDaoHandlerSelect handler = new RelationshipTypeDaoHandlerSelect( session );
        handler.handle();
        return handler.getResult();
    }

    @Override
    public RelationshipTypes select( final RelationshipTypeNames selectors, final Session session )
    {
        final RelationshipTypeDaoHandlerSelect handler = new RelationshipTypeDaoHandlerSelect( session ).selectors( selectors );
        handler.handle();
        return handler.getResult();
    }

    @Override
    public RelationshipType select( final RelationshipTypeName qualifiedName, final Session session )
    {
        final RelationshipTypeDaoHandlerSelect handler =
            new RelationshipTypeDaoHandlerSelect( session ).selectors( RelationshipTypeNames.from( qualifiedName ) );
        handler.handle();
        return handler.getResult().first();
    }
}
