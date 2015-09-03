package com.enonic.wem.repo.internal.storage;


import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.storage.result.ReturnValues;

public class AnotherCache
{
    private final ConcurrentMap<String, StorageData> idCache = Maps.newConcurrentMap();

    private final ConcurrentMap<CacheKey, String> valueCache = Maps.newConcurrentMap();

    public void store( final CacheStoreRequest cacheStoreRequest )
    {
        if ( cacheStoreRequest.getStorageData() == null )
        {
            return;
        }

        final String id = cacheStoreRequest.getId();

        idCache.put( id, cacheStoreRequest.getStorageData() );

        for ( final CacheKey key : cacheStoreRequest.getCacheKeys() )
        {
            valueCache.put( key, id );
        }
    }

    public CacheResult get( final String id )
    {
        return new CacheResult( this.idCache.get( id ), id );
    }

    public CacheResult get( final CacheKey cacheKey )
    {

        final String id = valueCache.get( cacheKey );

        if ( id == null )
        {
            return null;
        }

        return new CacheResult( this.idCache.get( id ), id );
    }

    public GetResult get( final GetByIdRequest request )
    {
        final StorageData data = idCache.get( request.getId() );

        if ( data == null )
        {
            return null;
        }

        final ReturnFields returnFields = request.getReturnFields();

        final ReturnValues.Builder builder = ReturnValues.create();

        for ( final ReturnField field : returnFields )
        {
            final Collection<Object> values = data.get( field.getPath() );

            if ( values == null || values.isEmpty() )
            {
                throw new RuntimeException( "Expected data with path '" + field.getPath() + " in storage" );
            }

            builder.add( field.getPath(), values ).build();
        }

        return GetResult.create().
            id( request.getId() ).
            resultFieldValues( builder.build() ).
            build();
    }

    public void delete( final CacheDeleteRequest request )
    {
        idCache.remove( request.getId() );

        for ( final CacheKey key : request.getCacheKeys() )
        {
            valueCache.remove( key );
        }
    }

}
