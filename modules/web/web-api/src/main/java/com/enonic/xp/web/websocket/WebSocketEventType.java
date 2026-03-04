package com.enonic.xp.web.websocket;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public enum WebSocketEventType
{
    OPEN,
    CLOSE,
    ERROR,
    MESSAGE
}
