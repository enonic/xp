package com.enonic.wem.repo.internal.storage;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;

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
    public GetResult getById( final GetByIdRequest request )
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

        final SearchResultEntry.Builder builder = SearchResultEntry.create();

        for ( final ReturnField field : returnFields )
        {
            final StorageDataEntry storageDataEntry = data.get( field.getPath() );

            if ( storageDataEntry == null )
            {
                throw new RuntimeException( "Expected data with path '" + field.getPath() + " in storage" );
            }

            builder.addField( field.getPath(), SearchResultFieldValue.value( storageDataEntry.getValue() ) ).build();
        }

        return new GetResult( builder.build() );
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
