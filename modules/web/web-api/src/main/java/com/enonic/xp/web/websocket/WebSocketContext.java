package com.enonic.xp.web.websocket;

import java.io.IOException;

public interface WebSocketContext
{
    boolean apply( WebSocketEndpoint endpoint )
        throws IOException;
}
