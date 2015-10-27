package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.common.extensions.WebSocketExtensionFactory;
import org.eclipse.jetty.websocket.jsr356.server.ContainerDefaultConfigurator;
import org.eclipse.jetty.websocket.jsr356.server.JsrCreator;
import org.eclipse.jetty.websocket.jsr356.server.SimpleServerEndpointMetadata;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import com.enonic.xp.web.websocket.WebSocketHandler;

final class WebSocketServlet
    extends HttpServlet
{
    private final WebSocketHandler handler;

    protected ServletContext realContext;

    private WebSocketServerFactory serverFactory;

    private WebSocketCreator creator;

    public WebSocketServlet( final WebSocketHandler handler )
    {
        this.handler = handler;
    }

    @Override
    public void init()
        throws ServletException
    {
        final WebSocketPolicy policy = WebSocketPolicy.newServerPolicy();
        this.serverFactory = new WebSocketServerFactory( policy );
        this.serverFactory.init( this.realContext );

        new ServerContainerImpl( this.serverFactory );
        this.creator = newCreator();
    }

    @Override
    public void destroy()
    {
        this.serverFactory.cleanup();
    }

    @Override
    protected void service( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( !this.handler.hasAccess( req ) )
        {
            res.setStatus( HttpServletResponse.SC_FORBIDDEN );
            return;
        }

        if ( !this.serverFactory.isUpgradeRequest( req, res ) )
        {
            super.service( req, res );
            return;
        }

        if ( this.serverFactory.acceptWebSocket( this.creator, req, res ) )
        {
            return;
        }

        if ( res.isCommitted() )
        {
            return;
        }

        super.service( req, res );
    }

    private WebSocketCreator newCreator()
    {
        final ServerEndpointConfig.Builder builder = newEndpointConfigBuilder();
        builder.configurator( newConfigurator() );
        builder.subprotocols( this.handler.getSubProtocols() );

        final ServerEndpointConfig config = builder.build();
        final SimpleServerEndpointMetadata meta = new SimpleServerEndpointMetadata( Endpoint.class, config );

        final WebSocketExtensionFactory ext = new WebSocketExtensionFactory( this.serverFactory );
        return new JsrCreator( this.serverFactory, meta, ext );
    }

    private ServerEndpointConfig.Configurator newConfigurator()
    {
        final ContainerDefaultConfigurator defaultConfigurator = new ContainerDefaultConfigurator();

        return new ServerEndpointConfig.Configurator()
        {
            @Override
            public String getNegotiatedSubprotocol( final List<String> supported, final List<String> requested )
            {
                return defaultConfigurator.getNegotiatedSubprotocol( supported, requested );
            }

            @Override
            public List<Extension> getNegotiatedExtensions( final List<Extension> installed, final List<Extension> requested )
            {
                return defaultConfigurator.getNegotiatedExtensions( installed, requested );
            }

            @Override
            public boolean checkOrigin( final String originHeaderValue )
            {
                return defaultConfigurator.checkOrigin( originHeaderValue );
            }

            @Override
            public void modifyHandshake( final ServerEndpointConfig sec, final HandshakeRequest request, final HandshakeResponse response )
            {
                defaultConfigurator.modifyHandshake( sec, request, response );
            }

            @Override
            public <T> T getEndpointInstance( final Class<T> endpointClass )
            {
                return endpointClass.cast( handler.newEndpoint() );
            }
        };
    }

    private ServerEndpointConfig.Builder newEndpointConfigBuilder()
    {
        return ServerEndpointConfig.Builder.create( Endpoint.class, "/" );
    }
}
