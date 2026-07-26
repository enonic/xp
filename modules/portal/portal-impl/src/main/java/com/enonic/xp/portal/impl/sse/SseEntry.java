package com.enonic.xp.portal.impl.sse;

import java.util.UUID;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.web.sse.SseMessage;

interface SseEntry
{
    UUID getClientId();

    @Nullable ApplicationKey getApplication();

    void addGroup( String group );

    void removeGroup( String group );

    void sendEvent( SseMessage message );

    void close();

    boolean isInGroup( String group );
}
