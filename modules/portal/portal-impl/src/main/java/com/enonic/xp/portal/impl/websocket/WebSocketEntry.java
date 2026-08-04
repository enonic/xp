package com.enonic.xp.portal.impl.websocket;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;

interface WebSocketEntry
{
    String getId();

    @Nullable ApplicationKey getApplication();

    void addGroup( String group );

    void removeGroup( String group );

    void sendMessage( String message );

    void close();

    boolean isInGroup( String group );
}
