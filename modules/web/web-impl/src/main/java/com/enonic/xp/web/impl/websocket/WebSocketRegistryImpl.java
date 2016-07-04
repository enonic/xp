package com.enonic.xp.web.impl.websocket;

import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Maps;

final class WebSocketRegistryImpl
    implements WebSocketRegistry
{
    private final Map<String, WebSocketEntry> map;

    public WebSocketRegistryImpl()
    {
        this.map = Maps.newConcurrentMap();
    }

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
