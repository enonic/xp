package com.enonic.xp.portal.impl.websocket;

import java.util.List;

import javax.websocket.Endpoint;

import com.enonic.xp.portal.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.EndpointFactory;

final class EndpointFactoryImpl
    implements EndpointFactory
{
    protected WebSocketEndpoint endpoint;

    protected WebSocketRegistry registry;

    @Override
    public Endpoint newEndpoint()
    {
        final WebSocketEntryImpl entry = new WebSocketEntryImpl();
        entry.endpoint = this.endpoint;
        entry.registry = this.registry;
        return entry;
    }

    @Override
    public List<String> getSubProtocols()
    {
        return this.endpoint.getConfig().getSubProtocols();
    }
}
