package com.enonic.xp.portal.sse;

import java.util.UUID;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.sse.SseEndpoint;
import com.enonic.xp.web.sse.SseMessage;

@NullMarked
public interface SseManager
{
    UUID setupSse( WebRequest request, SseEndpoint endpoint );

    void send( UUID clientId, SseMessage message );

    void sendToGroup( String group, SseMessage message );

    void close( UUID clientId );

    boolean isOpen( UUID clientId );

    int getGroupSize( String group );

    void addToGroup( String group, UUID clientId );

    void removeFromGroup( String group, UUID clientId );
}
