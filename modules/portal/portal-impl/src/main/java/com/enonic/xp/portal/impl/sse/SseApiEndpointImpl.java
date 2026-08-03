package com.enonic.xp.portal.impl.sse;

import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.web.sse.SseEndpoint;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.sse.SseConfig;

@NullMarked
public final class SseApiEndpointImpl
    implements SseEndpoint
{
    private final Supplier<UniversalApiHandler> apiHandlerSupplier;

    private final SseConfig config;

    private final @Nullable ApplicationKey application;

    public SseApiEndpointImpl( final SseConfig config, final Supplier<UniversalApiHandler> apiHandlerSupplier,
                               final @Nullable ApplicationKey application )
    {
        this.apiHandlerSupplier = apiHandlerSupplier;
        this.config = config;
        this.application = application;
    }

    @Override
    public SseConfig getConfig()
    {
        return config;
    }

    @Override
    public @Nullable ApplicationKey getApplication()
    {
        return this.application;
    }

    @Override
    public void onEvent( final SseEvent event )
    {
        this.apiHandlerSupplier.get().onSseEvent( event );
    }
}
