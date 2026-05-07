package com.enonic.xp.portal.impl.sse;

import java.util.UUID;

import com.enonic.xp.web.sse.SseMessage;

interface SseEntry
{
    UUID getClientId();

    void addGroup( String group );

    void removeGroup( String group );

    void sendEvent( SseMessage message );

    void close();

    boolean isInGroup( String group );
}
