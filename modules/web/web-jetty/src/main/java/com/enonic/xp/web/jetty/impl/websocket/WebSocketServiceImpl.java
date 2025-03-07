package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletContextRequest;
import org.eclipse.jetty.ee10.websocket.jakarta.server.JakartaWebSocketServerContainer;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.ContainerDefaultConfigurator;
import org.eclipse.jetty.websocket.core.server.WebSocketMappings;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
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

@Component
public final class WebSocketServiceImpl
    implements WebSocketService
{
    private static final Logger LOG = LoggerFactory.getLogger( WebSocketServiceImpl.class );

    @Activate
    public WebSocketServiceImpl( final JettyConfig config)
    {
    }

    @SuppressWarnings("WeakerAccess")
    @Activate
    public void activate()
        throws Exception
    {
    }

    @SuppressWarnings("WeakerAccess")
    @Deactivate
    public void deactivate()
    {
    }

    @Override
    public boolean isUpgradeRequest( final HttpServletRequest req, final HttpServletResponse res )
    {
        return WebSocketMappings.getMappings( ServletContextHandler.getServletContextHandler( req.getServletContext() ) )
            .getHandshaker()
            .isWebSocketUpgradeRequest( ServletContextRequest.getServletContextRequest( req ) );
    }

    @Override
    public boolean acceptWebSocket( final HttpServletRequest req, final HttpServletResponse res, final EndpointFactory factory )
        throws IOException
    {
        JakartaWebSocketServerContainer serverContainer = JakartaWebSocketServerContainer.getContainer( req.getServletContext() );

        final ServerEndpointConfig config = ServerEndpointConfig.Builder.create( Endpoint.class, "/" )
            .configurator( newConfigurator( factory ) )
            .subprotocols( factory.getSubProtocols() )
            .build();

        try
        {
            serverContainer.upgradeHttpToWebSocket( req, res, config, Map.of() );
            return true;
        }
        catch ( DeploymentException e )
        {
            return false;
        }
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
                // TODO: Check origin
                return defaultConfigurator.checkOrigin( originHeaderValue );
            }


            @Override
            public <T> T getEndpointInstance( final Class<T> endpointClass )
            {
                return endpointClass.cast( factory.newEndpoint() );
            }
        };
    }
}
