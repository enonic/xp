package com.enonic.xp.web.websocket;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;

public interface WebSocketEndpoint
{
    WebSocketConfig getConfig();

    void onEvent( WebSocketEvent event );

    /**
     * The application this connection serves, when known. Connections of a stopped or redeployed
     * application are closed by the server.
     *
     * @return the application, or {@code null} when the connection is not tied to one.
     */
    default @Nullable ApplicationKey getApplication()
    {
        return null;
    }
}
