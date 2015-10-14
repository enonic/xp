package com.enonic.xp.web.websocket;

import java.util.function.Supplier;

import javax.websocket.Endpoint;

public interface EndpointProvider<T extends Endpoint>
    extends Supplier<T>
{
}
