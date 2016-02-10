package com.enonic.xp.blob;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

public class BlobStoreProviders
    implements Iterable<BlobStoreProvider>
{
    private final Map<String, BlobStoreProvider> providers;

    public BlobStoreProviders()
    {
        this.providers = Maps.newHashMap();
    }

    public void add( final BlobStoreProvider provider )
    {
        this.providers.put( provider.name(), provider );
    }

    public void remove( final BlobStoreProvider provider )
    {
        this.providers.remove( provider.name() );
    }

    public BlobStoreProvider get( final String name )
    {
        return this.providers.get( name );
    }

    @Override
    public Iterator<BlobStoreProvider> iterator()
    {
        return providers.values().iterator();
    }
}
