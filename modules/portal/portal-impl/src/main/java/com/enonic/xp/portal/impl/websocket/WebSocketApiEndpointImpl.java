package com.enonic.xp.portal.impl.websocket;

import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketEvent;

public class WebSocketApiEndpointImpl
    implements WebSocketEndpoint
{
    private final Supplier<UniversalApiHandler> apiHandlerSupplier;

    private final WebSocketConfig config;

    private final @Nullable ApplicationKey application;

    public WebSocketApiEndpointImpl( final WebSocketConfig config, final Supplier<UniversalApiHandler> apiHandlerSupplier,
                                     final @Nullable ApplicationKey application )
    {
        this.apiHandlerSupplier = apiHandlerSupplier;
        this.config = config;
        this.application = application;
    }

    @Override
    public WebSocketConfig getConfig()
    {
        return config;
    }

    @Override
    public @Nullable ApplicationKey getApplication()
    {
        return this.application;
    }

    @Override
    public void onEvent( final WebSocketEvent event )
    {
        this.apiHandlerSupplier.get().onSocketEvent( event );
    }
}
