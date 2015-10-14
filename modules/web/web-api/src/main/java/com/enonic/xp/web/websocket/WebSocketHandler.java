package com.enonic.xp.web.websocket;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.WebSocketContainer;

public interface WebSocketHandler
    extends WebSocketContainer
{
    void init( ServletContext context )
        throws ServletException;

    void destroy();

    boolean isUpgradeRequest( HttpServletRequest req, HttpServletResponse res );

    boolean acceptWebSocket( HttpServletRequest req, HttpServletResponse res )
        throws IOException;

    void setEndpoint( Endpoint endpoint );

    void addDecoder( Class<Decoder> decoder );

    void addEncoder( Class<Encoder> encoder );

    void addSubProtocol( final String protocol );
}
