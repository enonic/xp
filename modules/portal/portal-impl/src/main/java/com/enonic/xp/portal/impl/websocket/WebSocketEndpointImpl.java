package com.enonic.xp.portal.impl.websocket;

import java.util.function.Supplier;

import jakarta.websocket.Session;

import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketEvent;

public final class WebSocketEndpointImpl
    implements WebSocketEndpoint
{
    private final Supplier<ControllerScript> scriptSupplier;

    private final WebSocketConfig config;

    public WebSocketEndpointImpl( final WebSocketConfig config, final Supplier<ControllerScript> scriptSupplier )
    {
        this.config = config;
        this.scriptSupplier = scriptSupplier;
    }

    @Override
    public void onEvent( final WebSocketEvent event )
    {
        final ControllerScript script = this.scriptSupplier.get();
        final Session session = event.getSession();
        // all events of one connection execute with affinity to one script context
        ( session != null ? script.pinned( session.getId() ) : script ).onSocketEvent( event );
    }

    @Override
    public WebSocketConfig getConfig()
    {
        return this.config;
    }
}
