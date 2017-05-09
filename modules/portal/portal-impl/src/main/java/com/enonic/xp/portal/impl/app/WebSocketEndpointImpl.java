package com.enonic.xp.portal.impl.app;

import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketEvent;

final class WebSocketEndpointImpl
    implements WebSocketEndpoint
{
    private final ControllerScript script;

    private final WebSocketConfig config;

    WebSocketEndpointImpl( final WebSocketConfig config, final ControllerScript script )
    {
        this.config = config;
        this.script = script;
    }

    @Override
    public void onEvent( final WebSocketEvent event )
    {
        this.script.onSocketEvent( event );
    }

    @Override
    public WebSocketConfig getConfig()
    {
        return this.config;
    }
}
