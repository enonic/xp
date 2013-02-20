package com.enonic.wem.core.content.schema.relationship.dao;

import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.schema.relationship.RelationshipType;
import com.enonic.wem.api.content.schema.relationship.RelationshipTypes;

@Component
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
    public void delete( final QualifiedRelationshipTypeName qualifiedName, final Session session )
    {
        new RelationshipTypeDaoHandlerDelete( session ).qualifiedRelationshipTypeName( qualifiedName ).handle();
    }

    @Override
    public QualifiedRelationshipTypeNames exists( final QualifiedRelationshipTypeNames qNames, final Session session )
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
    public RelationshipTypes select( final QualifiedRelationshipTypeNames selectors, final Session session )
    {
        final RelationshipTypeDaoHandlerSelect handler = new RelationshipTypeDaoHandlerSelect( session ).selectors( selectors );
        handler.handle();
        return handler.getResult();
    }

    @Override
    public RelationshipType select( final QualifiedRelationshipTypeName qualifiedName, final Session session )
    {
        final RelationshipTypeDaoHandlerSelect handler =
            new RelationshipTypeDaoHandlerSelect( session ).selectors( QualifiedRelationshipTypeNames.from( qualifiedName ) );
        handler.handle();
        return handler.getResult().first();
    }
}
