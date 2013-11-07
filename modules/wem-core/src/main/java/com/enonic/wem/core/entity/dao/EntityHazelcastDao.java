package com.enonic.wem.core.entity.dao;


import javax.inject.Inject;

import org.joda.time.DateTime;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import com.enonic.wem.api.entity.Entity;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFound;

public class EntityHazelcastDao
    implements EntityDao
{
    public static final String ENTITY_MAP_NAME = "Entity";

    private final IMap<EntityId, Entity> entityMap;

    @Inject
    public EntityHazelcastDao( final HazelcastInstance hazelcastInstance )
    {
        this.entityMap = hazelcastInstance.getMap( ENTITY_MAP_NAME );
    }

    @Override
    public Entity create( final CreateEntityArgs args )
    {
        final Entity.Builder entityBuilder = new Entity.Builder();
        entityBuilder.id( new EntityId() );
        entityBuilder.createdTime( DateTime.now() );
        entityBuilder.entityIndexConfig( args.entityIndexConfig );
        entityBuilder.rootDataSet( args.data );

        final Entity persistedEntity = entityBuilder.build();

        final Entity previous = entityMap.putIfAbsent( persistedEntity.id(), persistedEntity );
        if ( previous != null )
        {
            entityMap.remove( persistedEntity.id() );
            throw new IllegalStateException(
                "Entity with id [" + persistedEntity.id() + "] already existed: " + persistedEntity.toString() );
        }
        return persistedEntity;
    }

    @Override
    public void update( final UpdateEntityArgs args )
    {
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

    @Override
    public Entity getById( final EntityId id )
    {
        final Entity entity = entityMap.get( id );
        if ( entity == null )
        {
            throw new NoEntityWithIdFound( id );
        }
        return entity;
    }

    @Override
    public void deleteById( final EntityId id )
    {
        entityMap.delete( id );
    }
}
