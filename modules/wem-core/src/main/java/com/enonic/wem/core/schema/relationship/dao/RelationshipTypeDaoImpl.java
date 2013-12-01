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
    public void delete( final RelationshipTypeName relationshipTypeName, final Session session )
    {
        new RelationshipTypeDaoHandlerDelete( session ).relationshipTypeName( relationshipTypeName ).handle();
    }

    @Override
    public RelationshipTypeNames exists( final RelationshipTypeNames relationshipTypeNames, final Session session )
    {
        final RelationshipTypeDaoHandlerExists handler = new RelationshipTypeDaoHandlerExists( session ).selectors( relationshipTypeNames );
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
    public RelationshipTypes select( final RelationshipTypeNames relationshipTypeNames, final Session session )
    {
        final RelationshipTypeDaoHandlerSelect handler = new RelationshipTypeDaoHandlerSelect( session ).selectors( relationshipTypeNames );
        handler.handle();
        return handler.getResult();
    }

    @Override
    public RelationshipType select( final RelationshipTypeName relationshipTypeName, final Session session )
    {
        final RelationshipTypeDaoHandlerSelect handler =
            new RelationshipTypeDaoHandlerSelect( session ).selectors( RelationshipTypeNames.from( relationshipTypeName ) );
        handler.handle();
        return handler.getResult().first();
    }
}
