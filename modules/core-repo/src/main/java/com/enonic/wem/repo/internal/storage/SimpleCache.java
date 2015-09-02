package com.enonic.wem.repo.internal.storage;

import java.util.Collection;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.storage.result.ReturnValues;

@Component
public class SimpleCache
    implements StorageCache
{
    private final Map<StorageSettings, MemoryStore> memoryStoreMap = Maps.newConcurrentMap();

    @Override
    public String put( final StoreRequest request )
    {
        Preconditions.checkNotNull( request.getId(), "id must be provided for memoryDao" );

        final StorageSettings settings = request.getSettings();

        MemoryStore store = getStore( settings );

        store.put( request.getId(), request.getPath(), request.getData() );

        return request.getId();
    }

    @Override
    public void remove( final DeleteRequest request )
    {
        MemoryStore store = memoryStoreMap.get( request.getSettings() );

        store.remove( request.getId() );
    }

    @Override
    public GetResult getById( final GetByIdRequest request )
    {
        MemoryStore store = getStore( request.getStorageSettings() );

        final StorageData data = store.getById( request.getId() );

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

    private synchronized MemoryStore getStore( final StorageSettings storageSettings )
    {
        MemoryStore store = this.memoryStoreMap.get( storageSettings );

        if ( store == null )
        {
            store = new MemoryStore();
            this.memoryStoreMap.put( storageSettings, store );
        }
        return store;
    }

    @Override
    public GetResult getByPath( final GetByPathRequest query )
    {
        return null;
    }

    @Override
    public GetResult getByParent( final GetByParentRequest query )
    {
        return null;
    }
}
