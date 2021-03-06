package com.enonic.xp.portal.impl.websocket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

final class WebSocketRegistryImpl
    implements WebSocketRegistry
{
    private final ConcurrentMap<String, WebSocketEntry> map = new ConcurrentHashMap<>();

    @Override
    public void add( final WebSocketEntry entry )
    {
        this.map.put( entry.getId(), entry );
    }

    @Override
    public void remove( final WebSocketEntry entry )
    {
        this.map.remove( entry.getId() );
    }

    @Override
    public WebSocketEntry getById( final String id )
    {
        return this.map.get( id );
    }

    @Override
    public Stream<WebSocketEntry> getByGroup( final String group )
    {
        return this.map.values().stream().filter( e -> e.isInGroup( group ) );
    }
}
