package com.enonic.xp.portal.impl.sse;

import java.util.function.Supplier;

import com.enonic.xp.portal.sse.SseEndpoint;
import com.enonic.xp.portal.sse.SseEvent;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.sse.SseConfig;

public final class SseApiEndpointImpl
    implements SseEndpoint
{
    private final Supplier<UniversalApiHandler> apiHandlerSupplier;

    private final SseConfig config;

    public SseApiEndpointImpl( final SseConfig config, final Supplier<UniversalApiHandler> apiHandlerSupplier )
    {
        this.apiHandlerSupplier = apiHandlerSupplier;
        this.config = config;
    }

    @Override
    public SseConfig getConfig()
    {
        return config;
    }

    @Override
    public void onEvent( final SseEvent event )
    {
        this.apiHandlerSupplier.get().onSseEvent( event );
    }
}
