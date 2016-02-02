package com.enonic.xp.web.jetty.impl.websocket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Maps;

import com.enonic.xp.web.websocket.WebSocketHandler;

final class WebSocketEntryMap
{
    private final Map<WebSocketHandler, WebSocketEntry> map;

    public WebSocketEntryMap()
    {
        this.map = Maps.newConcurrentMap();
    }

    public WebSocketEntry find( final HttpServletRequest req )
    {
        for ( final WebSocketEntry entry : this.map.values() )
        {
            if ( entry.handler.canHandle( req ) )
            {
                return entry;
            }
        }

        return null;
    }

    public WebSocketEntry add( final WebSocketHandler handler )
    {
        final WebSocketEntry entry = new WebSocketEntry( handler );
        this.map.put( handler, entry );
        return entry;
    }

    public void remove( final WebSocketHandler handler )
    {
        this.map.remove( handler );
    }
}
