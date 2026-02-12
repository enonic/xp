package com.enonic.xp.portal.impl.sse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

final class SseRegistryImpl
    implements SseRegistry
{
    private final ConcurrentMap<String, SseEntry> map = new ConcurrentHashMap<>();

    @Override
    public void add( final SseEntry entry )
    {
        this.map.put( entry.getId(), entry );
    }

    @Override
    public void remove( final SseEntry entry )
    {
        this.map.remove( entry.getId() );
    }

    @Override
    public SseEntry getById( final String id )
    {
        return this.map.get( id );
    }

    @Override
    public Stream<SseEntry> getByGroup( final String group )
    {
        return this.map.values().stream().filter( e -> e.isInGroup( group ) );
    }
}
