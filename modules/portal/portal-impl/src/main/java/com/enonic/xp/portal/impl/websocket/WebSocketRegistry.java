package com.enonic.xp.portal.impl.websocket;

import java.util.stream.Stream;

import com.enonic.xp.app.ApplicationKey;

interface WebSocketRegistry
{
    void add( WebSocketEntry entry );

    void remove( WebSocketEntry entry );

    WebSocketEntry getById( String id );

    Stream<WebSocketEntry> getByGroup( String group );

    Stream<WebSocketEntry> getByApplication( ApplicationKey application );
}
