package com.enonic.xp.portal.impl.sse;

interface SseEntry
{
    String getId();

    void addGroup( String group );

    void removeGroup( String group );

    void sendEvent( String event, String data, String eventId );

    void close();

    boolean isInGroup( String group );
}
