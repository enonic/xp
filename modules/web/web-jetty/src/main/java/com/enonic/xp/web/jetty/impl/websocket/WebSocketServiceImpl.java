package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletContextRequest;
import org.eclipse.jetty.ee11.websocket.jakarta.server.JakartaWebSocketServerContainer;
import org.eclipse.jetty.ee11.websocket.jakarta.server.config.ContainerDefaultConfigurator;
import org.eclipse.jetty.websocket.core.server.WebSocketMappings;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.Extension;
import jakarta.websocket.server.ServerEndpointConfig;

import com.enonic.xp.web.jetty.impl.JettyConfig;
import com.enonic.xp.web.websocket.EndpointFactory;
import com.enonic.xp.web.websocket.WebSocketService;

@Component(configurationPid = "com.enonic.xp.web.jetty")
public final class WebSocketServiceImpl
    implements WebSocketService
{
    private static final Logger LOG = LoggerFactory.getLogger( WebSocketServiceImpl.class );

    private final boolean originCheckEnabled;

    @Activate
    public WebSocketServiceImpl( final JettyConfig config )
    {
        this.originCheckEnabled = config.websocket_originCheck();
    }

    @Override
    public boolean isUpgradeRequest( final HttpServletRequest req )
    {
        return WebSocketMappings.getMappings( ServletContextHandler.getServletContextHandler( req.getServletContext() ) )
            .getHandshaker()
            .isWebSocketUpgradeRequest( ServletContextRequest.getServletContextRequest( req ) );
    }

    @Override
    public boolean acceptWebSocket( final HttpServletRequest req, final HttpServletResponse res, final EndpointFactory factory )
        throws IOException
    {
        final String expectedScheme = req.getScheme();
        final String expectedHost = req.getServerName();
        final int expectedPort = req.getServerPort();

        final ServerEndpointConfig config = ServerEndpointConfig.Builder.create( Endpoint.class, "/" )
            .configurator( newConfigurator( factory, expectedScheme, expectedHost, expectedPort, this.originCheckEnabled ) )
            .subprotocols( factory.getSubProtocols() )
            .build();

        try
        {
            JakartaWebSocketServerContainer.getContainer( req.getServletContext() ).upgradeHttpToWebSocket( req, res, config, Map.of() );
            return true;
        }
        catch ( DeploymentException e )
        {
            return false;
        }
    }

    private static ServerEndpointConfig.Configurator newConfigurator( final EndpointFactory factory, final String expectedScheme,
                                                                      final String expectedHost, final int expectedPort,
                                                                      final boolean originCheckEnabled )
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
                final Predicate<String> validator = factory.getOriginValidator();
                if ( validator != null )
                {
                    final boolean accepted;
                    try
                    {
                        accepted = validator.test( originHeaderValue );
                    }
                    catch ( RuntimeException e )
                    {
                        LOG.warn( "WebSocket origin validator threw; rejecting upgrade (Origin={})", originHeaderValue, e );
                        return false;
                    }
                    if ( !accepted )
                    {
                        LOG.debug( "WebSocket upgrade rejected by checkOrigin function (Origin={})", originHeaderValue );
                    }
                    return accepted;
                }

                if ( !originCheckEnabled )
                {
                    return true;
                }

                final boolean accepted = SameOriginCheck.check( originHeaderValue, expectedScheme, expectedHost, expectedPort );
                if ( !accepted )
                {
                    LOG.debug( "WebSocket upgrade rejected by same-origin check (Origin={}, expected {}://{}:{})", originHeaderValue,
                               expectedScheme, expectedHost, expectedPort );
                }
                return accepted;
            }

            @Override
            public <T> T getEndpointInstance( final Class<T> endpointClass )
            {
                return endpointClass.cast( factory.newEndpoint() );
            }
        };
    }
}
