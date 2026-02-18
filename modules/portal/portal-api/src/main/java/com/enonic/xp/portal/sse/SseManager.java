package com.enonic.xp.portal.sse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface SseManager
{
    String setupSse( HttpServletRequest req, HttpServletResponse res, SseEndpoint endpoint );

    void send( String id, String event, String data, String eventId );

    void sendToGroup( String group, String event, String data );

    void close( String id );

    long getGroupSize( String group );

    void addToGroup( String group, String id );

    void removeFromGroup( String group, String id );
}
