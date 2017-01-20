package com.enonic.xp.internal.blobstore;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.blob.BlobStoreProvider;

final class BlobStoreProviders
    implements Iterable<BlobStoreProvider>
{
    private final Map<String, BlobStoreProvider> providers;

    BlobStoreProviders()
    {
        this.providers = Maps.newHashMap();
    }

    void add( final BlobStoreProvider provider )
    {
        this.providers.put( provider.name(), provider );
    }

    void remove( final BlobStoreProvider provider )
    {
        this.providers.remove( provider.name() );
    }

    BlobStoreProvider get( final String name )
    {
        return this.providers.get( name );
    }

    @Override
    public Iterator<BlobStoreProvider> iterator()
    {
        return providers.values().iterator();
    }
}
