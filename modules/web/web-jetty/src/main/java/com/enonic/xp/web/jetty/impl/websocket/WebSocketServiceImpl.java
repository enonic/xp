package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletContextRequest;
import org.eclipse.jetty.session.SessionManager;
import org.eclipse.jetty.ee11.websocket.jakarta.server.JakartaWebSocketServerContainer;
import org.eclipse.jetty.ee11.websocket.jakarta.server.config.ContainerDefaultConfigurator;
import org.eclipse.jetty.websocket.core.server.WebSocketMappings;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

    private final boolean defaultOriginCheckEnabled;

    private final WebSocketSessionTracker sessionTracker;

    @Activate
    public WebSocketServiceImpl( final JettyConfig config, @Reference final WebSocketSessionTracker sessionTracker )
    {
        this.defaultOriginCheckEnabled = config.websocket_defaultOriginCheck();
        this.sessionTracker = sessionTracker;
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

        final boolean terminateOnSessionExit = factory.isTerminateOnSessionExit();
        final boolean sessionAccess = factory.isSessionAccess();

        // The HTTP session is only observable during the upgrade request, so capture what we need now: its id
        // (to close the socket when the session ends) and/or an Accessor (to refresh it on inbound messages).
        final String httpSessionId;
        final HttpSession.Accessor sessionAccessor;
        final HttpSession httpSession = terminateOnSessionExit || sessionAccess ? req.getSession( false ) : null;
        if ( httpSession == null )
        {
            // No HTTP session at upgrade time (or nothing to bind): the socket is left detached.
            httpSessionId = null;
            sessionAccessor = null;
        }
        else
        {
            httpSessionId = terminateOnSessionExit ? httpSession.getId() : null;
            sessionAccessor = sessionAccess ? httpSession.getAccessor() : null;
        }

        // The session must never be probed through the captured HttpSession object: with a NullSessionCache
        // (cluster session store) it goes non-resident as soon as the upgrade request completes, while the
        // session itself lives on. Liveness is checked by id through the SessionManager instead.
        final BooleanSupplier httpSessionAlive = httpSessionId == null
            ? null
            : newHttpSessionProbe( ServletContextHandler.getServletContextHandler( req.getServletContext() ).getSessionHandler(),
                                   httpSessionId );

        final ServerEndpointConfig config = ServerEndpointConfig.Builder.create( Endpoint.class, "/" )
            .configurator(
                newConfigurator( factory, expectedScheme, expectedHost, expectedPort, this.defaultOriginCheckEnabled, this.sessionTracker,
                                 httpSessionAlive, httpSessionId, sessionAccessor, factory.getSessionAccessThrottle() ) )
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

    private static BooleanSupplier newHttpSessionProbe( final SessionManager sessionManager, final String httpSessionId )
    {
        return () -> {
            try
            {
                // exists() consults the cache and store without entering the session, so the probe neither
                // marks the session as in-use nor resets its inactivity timer.
                return sessionManager.getSessionCache().exists( httpSessionId );
            }
            catch ( Exception e )
            {
                LOG.debug( "Could not determine HTTP session [{}] state; treating it as alive", httpSessionId, e );
                return true;
            }
        };
    }

    private static ServerEndpointConfig.Configurator newConfigurator( final EndpointFactory factory, final String expectedScheme,
                                                                      final String expectedHost, final int expectedPort,
                                                                      final boolean defaultOriginCheckEnabled,
                                                                      final WebSocketSessionTracker sessionTracker,
                                                                      final BooleanSupplier httpSessionAlive, final String httpSessionId,
                                                                      final HttpSession.Accessor sessionAccessor,
                                                                      final Duration sessionAccessThrottle )
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

                if ( !defaultOriginCheckEnabled )
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
                final Endpoint endpoint = factory.newEndpoint();
                if ( httpSessionId == null && sessionAccessor == null )
                {
                    return endpointClass.cast( endpoint );
                }
                return endpointClass.cast(
                    new SessionBoundEndpoint( endpoint, sessionTracker, httpSessionAlive, httpSessionId, sessionAccessor,
                                              sessionAccessThrottle ) );
            }
        };
    }
}
