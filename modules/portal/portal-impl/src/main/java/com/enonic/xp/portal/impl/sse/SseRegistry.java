package com.enonic.xp.portal.impl.sse;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

final class SseRegistry
{
    private final ConcurrentMap<UUID, SseEntry> map = new ConcurrentHashMap<>();

    public void add( final SseEntry entry )
    {
        this.map.put( entry.getClientId(), entry );
    }

    public void remove( final SseEntry entry )
    {
        this.map.remove( entry.getClientId() );
    }

    public SseEntry getById( final UUID id )
    {
        return this.map.get( id );
    }

    public Stream<SseEntry> getByGroup( final String group )
    {
        return this.map.values().stream().filter( e -> e.isInGroup( group ) );
    }
}
