package com.enonic.xp.portal.impl.websocket;

import java.util.function.Supplier;

import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketEvent;

public class WebSocketApiEndpointImpl
    implements WebSocketEndpoint
{
    private final Supplier<UniversalApiHandler> apiHandlerSupplier;

    private final WebSocketConfig config;

    public WebSocketApiEndpointImpl( final WebSocketConfig config, final Supplier<UniversalApiHandler> apiHandlerSupplier )
    {
        this.apiHandlerSupplier = apiHandlerSupplier;
        this.config = config;
    }

    @Override
    public WebSocketConfig getConfig()
    {
        return config;
    }

    @Override
    public void onEvent( final WebSocketEvent event )
    {
        this.apiHandlerSupplier.get().onSocketEvent( event );
    }
}
