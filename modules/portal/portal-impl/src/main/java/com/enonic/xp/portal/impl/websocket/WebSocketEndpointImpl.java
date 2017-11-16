package com.enonic.xp.portal.impl.websocket;

import java.util.function.Supplier;

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
        this.scriptSupplier.get().onSocketEvent( event );
    }

    @Override
    public WebSocketConfig getConfig()
    {
        return this.config;
    }
}
