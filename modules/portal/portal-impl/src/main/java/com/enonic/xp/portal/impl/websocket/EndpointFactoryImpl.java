package com.enonic.xp.portal.impl.websocket;

import java.util.List;

import javax.websocket.Endpoint;

import com.enonic.xp.web.websocket.EndpointFactory;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

final class EndpointFactoryImpl
    implements EndpointFactory
{
    protected WebSocketEndpoint endpoint;

    protected WebSocketRegistry registry;

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
}
