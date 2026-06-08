package com.enonic.xp.portal.impl.websocket;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

import jakarta.websocket.Endpoint;

import com.enonic.xp.web.websocket.EndpointFactory;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

final class EndpointFactoryImpl
    implements EndpointFactory
{
    WebSocketEndpoint endpoint;

    WebSocketRegistry registry;

    @Override
    public Endpoint newEndpoint()
    {
        return new WebSocketEntryImpl( this.endpoint, this.registry );
    }

    @Override
    public List<String> getSubProtocols()
    {
        return this.endpoint.getConfig().getSubProtocols();
    }

    @Override
    public Predicate<String> getOriginValidator()
    {
        return this.endpoint.getConfig().getOriginValidator();
    }

    @Override
    public boolean isTerminateOnSessionExit()
    {
        return this.endpoint.getConfig().isTerminateOnSessionExit();
    }

    @Override
    public boolean isSessionAccess()
    {
        return this.endpoint.getConfig().isSessionAccess();
    }

    @Override
    public Duration getSessionAccessThrottle()
    {
        return this.endpoint.getConfig().getSessionAccessThrottle();
    }
}
