package com.enonic.xp.web.impl.websocket;

import java.util.stream.Stream;

interface WebSocketRegistry
{
    void add( WebSocketEntry entry );

    void remove( WebSocketEntry entry );

    WebSocketEntry getById( String id );

    Stream<WebSocketEntry> getByGroup( String group );
}
