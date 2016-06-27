package com.enonic.xp.web.impl.websocket;

import java.io.IOException;

import com.enonic.xp.web.websocket.WebSocketEndpoint;

public interface WebSocketContext
{
    boolean apply( WebSocketEndpoint endpoint )
        throws IOException;
}
