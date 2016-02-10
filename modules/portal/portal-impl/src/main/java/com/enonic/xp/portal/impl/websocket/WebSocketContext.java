package com.enonic.xp.portal.impl.websocket;

import java.io.IOException;

import com.enonic.xp.portal.websocket.WebSocketEndpoint;

public interface WebSocketContext
{
    boolean apply( WebSocketEndpoint endpoint )
        throws IOException;
}
