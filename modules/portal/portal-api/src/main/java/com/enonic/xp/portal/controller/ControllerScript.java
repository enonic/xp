package com.enonic.xp.portal.controller;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.websocket.WebSocketEvent;

public interface ControllerScript
{
    PortalResponse execute( PortalRequest portalRequest );

    void onSocketEvent( WebSocketEvent event );
}
