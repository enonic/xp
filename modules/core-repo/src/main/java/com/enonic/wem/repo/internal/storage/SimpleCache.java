package com.enonic.wem.repo.internal.storage;

import java.util.Collection;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import com.enonic.wem.repo.internal.index.result.GetResultNew;
import com.enonic.wem.repo.internal.index.result.ResultFieldValues;

@Component
public class SimpleCache
    implements StorageCache
{
    private Map<StorageSettings, MemoryStore> memoryStoreMap = Maps.newConcurrentMap();

    @Override
    public String put( final StoreRequest request )
    {
        Preconditions.checkNotNull( request.getId(), "id must be provided for memoryDao" );

        final StorageSettings settings = request.getSettings();

        MemoryStore store = memoryStoreMap.get( settings );

        if ( store == null )
        {
            store = new MemoryStore();
            memoryStoreMap.put( settings, store );
        }

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
    public GetResultNew getById( final GetByIdRequest request )
    {
        MemoryStore store = this.memoryStoreMap.get( request.getStorageSettings() );

        if ( store == null )
        {
            store = new MemoryStore();
            this.memoryStoreMap.put( request.getStorageSettings(), store );
        }

        final StorageData data = store.getById( request.getId() );

        if ( data == null )
        {
            return null;
        }

        final ReturnFields returnFields = request.getReturnFields();

        final ResultFieldValues.Builder builder = ResultFieldValues.create();

        for ( final ReturnField field : returnFields )
        {
            final Collection<Object> values = data.get( field.getPath() );

            if ( values == null != values.isEmpty() )
            {
                throw new RuntimeException( "Expected data with path '" + field.getPath() + " in storage" );
            }

            builder.add( field.getPath(), values ).build();
        }

        return GetResultNew.create().
            id( request.getId() ).
            resultFieldValues( builder.build() ).
            build();
    }

    @Override
    public GetResultNew getByPath( final GetByPathRequest query )
    {
        return null;
    }

    @Override
    public GetResultNew getByParent( final GetByParentRequest query )
    {
        return null;
    }
}
