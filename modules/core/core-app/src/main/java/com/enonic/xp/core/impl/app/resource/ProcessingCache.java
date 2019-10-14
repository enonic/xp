package com.enonic.xp.core.impl.app.resource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.server.RunMode;

public final class ProcessingCache
{
    private final RunMode runMode;

    private final Function<ResourceKey, Resource> loader;

    private final ConcurrentMap<ProcessingKey, ProcessingEntry> map;

    public ProcessingCache( final Function<ResourceKey, Resource> loader, final RunMode runMode )
    {
        this.loader = loader;
        this.runMode = runMode;
        this.map = new ConcurrentHashMap<>();
    }

    public <K, V> V process( final ResourceProcessor<K, V> processor )
    {
        final ProcessingKey key = createKey( processor );
        removeIfNeeded( key );

        final ProcessingEntry entry = this.map.computeIfAbsent( key, processingKey -> doProcess( processor ) );
        if ( entry == null )
        {
            return null;
        }

        return typecast( entry.value );
    }

    @SuppressWarnings("unchecked")
    private <V> V typecast( final Object value )
    {
        return (V) value;
    }

    private ProcessingEntry doProcess( final ResourceProcessor<?, ?> processor )
    {
        final ResourceKey resourceKey = processor.toResourceKey();
        final Resource resource = this.loader.apply( resourceKey );
        final Object value = processor.process( resource );
        if ( value == null )
        {
            return null;
        }

        return new ProcessingEntry( resourceKey, value, resource.getTimestamp() );
    }

    public void invalidate( final ApplicationKey appKey )
    {
        this.map.forEach( ( key, entry ) -> {
            if ( entry.key.getApplicationKey().equals( appKey ) )
            {
                map.remove( key );
            }
        } );
    }

    private ProcessingKey createKey( final ResourceProcessor<?, ?> params )
    {
        return new ProcessingKey( params.getSegment(), params.getKey() );
    }

    private void removeIfNeeded( final ProcessingKey key )
    {
        if ( this.runMode != RunMode.DEV )
        {
            return;
        }

        final ProcessingEntry entry = this.map.get( key );
        if ( entry == null )
        {
            return;
        }

        if ( isModified( entry ) )
        {
            this.map.remove( key );
        }
    }

    public boolean isModified( final ProcessingEntry entry )
    {
        final Resource resource = this.loader.apply( entry.key );
        return !resource.exists() || resource.getTimestamp() > entry.timestamp;
    }
}