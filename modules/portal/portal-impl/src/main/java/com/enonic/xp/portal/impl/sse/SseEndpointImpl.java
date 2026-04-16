package com.enonic.xp.portal.impl.sse;

import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.web.sse.SseEndpoint;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseConfig;

@NullMarked
public final class SseEndpointImpl
    implements SseEndpoint
{
    private final SseConfig config;

    private final Supplier<ControllerScript> scriptSupplier;

    public SseEndpointImpl( final SseConfig config, final Supplier<ControllerScript> scriptSupplier )
    {
        this.config = config;
        this.scriptSupplier = scriptSupplier;
    }

    @Override
    public void onEvent( final SseEvent event )
    {
        this.scriptSupplier.get().onSseEvent( event );
    }

    @Override
    public SseConfig getConfig()
    {
        return this.config;
    }
}
