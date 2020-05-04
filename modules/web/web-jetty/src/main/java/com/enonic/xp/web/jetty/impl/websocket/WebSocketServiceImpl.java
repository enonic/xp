package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.jetty.impl.JettyConfig;
import com.enonic.xp.web.websocket.EndpointFactory;
import com.enonic.xp.web.websocket.WebSocketService;

@Component
public final class WebSocketServiceImpl
    implements WebSocketService
{
    private static final Logger LOG = LoggerFactory.getLogger( WebSocketServiceImpl.class );

    private static final String SERVLET_CONTEXT_TARGET =
        "(" + DispatchConstants.CONNECTOR_PROPERTY + "=" + DispatchConstants.XP_CONNECTOR + ")";

    private final WebSocketServerFactory serverFactory;

    @Activate
    public WebSocketServiceImpl( final JettyConfig config, @Reference(target = SERVLET_CONTEXT_TARGET) final ServletContext servletContext )
    {
        final WebSocketPolicy webSocketPolicy = WebSocketPolicy.newServerPolicy();
        webSocketPolicy.setIdleTimeout( config.websocket_idleTimeout() );
        this.serverFactory = new WebSocketServerFactory( servletContext, webSocketPolicy );
    }

    @SuppressWarnings("WeakerAccess")
    @Activate
    public void activate()
        throws Exception
    {
        final ServerContainerImpl serverContainer = new ServerContainerImpl( this.serverFactory );

        try
        {
            this.serverFactory.start();
            serverContainer.start();
        }
        catch ( ServletException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw new ServletException( e );
        }
    }

    @SuppressWarnings("WeakerAccess")
    @Deactivate
    public void deactivate()
    {
        try
        {
            this.serverFactory.stop();
        }
        catch ( Exception e )
        {
            LOG.warn( "failed to stop WebSocketServiceImpl", e );
        }
    }

    @Override
    public boolean isUpgradeRequest( final HttpServletRequest req, final HttpServletResponse res )
    {
        return this.serverFactory.isUpgradeRequest( req, res );
    }

    @Override
    public boolean acceptWebSocket( final HttpServletRequest req, final HttpServletResponse res, final EndpointFactory factory )
        throws IOException
    {
        return this.serverFactory.acceptWebSocket( newCreator( factory ), req, res );
    }

    private WebSocketCreator newCreator( final EndpointFactory factory )
    {
        final ServerEndpointConfig.Builder builder = newEndpointConfigBuilder();
        builder.configurator( newConfigurator( factory ) );
        builder.subprotocols( factory.getSubProtocols() );

        final ServerEndpointConfig config = builder.build();
        final SimpleServerEndpointMetadata meta = new SimpleServerEndpointMetadata( Endpoint.class, config );

        final WebSocketExtensionFactory ext = new WebSocketExtensionFactory( this.serverFactory );
        return new JsrCreator( this.serverFactory, meta, ext );
    }

    private ServerEndpointConfig.Configurator newConfigurator( final EndpointFactory factory )
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
                return endpointClass.cast( factory.newEndpoint() );
            }
        };
    }

    private ServerEndpointConfig.Builder newEndpointConfigBuilder()
    {
        return ServerEndpointConfig.Builder.create( Endpoint.class, "/" );
    }
}
