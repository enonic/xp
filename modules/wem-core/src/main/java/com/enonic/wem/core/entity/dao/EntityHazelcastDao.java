package com.enonic.wem.core.entity.dao;


import javax.inject.Inject;

import org.joda.time.DateTime;

import com.hazelcast.core.IMap;

import com.enonic.wem.api.entity.Entity;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFound;
import com.enonic.wem.core.hazelcast.HazelcastProvider;

public class EntityHazelcastDao
    implements EntityDao
{
    public static final String ENTITY_MAP_NAME = "Entity";

    private HazelcastProvider hazelCastProvider;

    @Override
    public void create( final CreateEntityArgs args )
    {
        final Entity.Builder entityBuilder = new Entity.Builder();
        entityBuilder.id( new EntityId() );
        entityBuilder.createdTime( DateTime.now() );
        entityBuilder.entityIndexConfig( args.entityIndexConfig );
        entityBuilder.rootDataSet( args.data );

        final Entity entity = entityBuilder.build();

        final IMap<EntityId, Entity> entityMap = hazelCastProvider.get().getMap( ENTITY_MAP_NAME );
        final Entity previous = entityMap.putIfAbsent( entity.id(), entity );
        if ( previous != null )
        {
            entityMap.remove( entity.id() );
            throw new IllegalStateException( "Entity with id [" + entity.id() + "] already existed: " + entity.toString() );
        }
    }

    @Override
    public void update( final UpdateEntityArgs args )
    {
        final IMap<EntityId, Entity> entityMap = hazelCastProvider.get().getMap( ENTITY_MAP_NAME );
        final Entity persisted = entityMap.get( args.entityToUpdate );
        if ( persisted == null )
        {
            throw new NoEntityWithIdFound( args.entityToUpdate );
        }

        final Entity.Builder entityBuilder = new Entity.Builder( persisted );
        entityBuilder.modifiedTime( DateTime.now() );
        entityBuilder.entityIndexConfig( args.entityIndexConfig );
        entityBuilder.rootDataSet( args.data );

        final Entity entity = entityBuilder.build();
        entityMap.put( entity.id(), entity );
    }

    @Inject
    public void setHazelCastProvider( final HazelcastProvider hazelCastProvider )
    {
        this.hazelCastProvider = hazelCastProvider;
    }
}
