package com.enonic.xp.web.websocket;

import java.util.List;

import javax.websocket.Endpoint;

public interface EndpointFactory
{
    Endpoint newEndpoint();

    List<String> getSubProtocols();
}
