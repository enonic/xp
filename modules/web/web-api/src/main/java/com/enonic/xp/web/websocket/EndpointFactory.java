package com.enonic.xp.web.websocket;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import jakarta.websocket.Endpoint;

public interface EndpointFactory
{
    Endpoint newEndpoint();

    default List<String> getSubProtocols()
    {
        return Collections.emptyList();
    }

    default Predicate<String> getOriginValidator()
    {
        return null;
    }
}
