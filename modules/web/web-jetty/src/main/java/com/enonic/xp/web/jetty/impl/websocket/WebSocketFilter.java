package com.enonic.xp.web.jetty.impl.websocket;

import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.jetty.impl.JettyController;
import com.enonic.xp.web.websocket.WebSocketHandler;

@Component(immediate = true, service = Filter.class,
    property = {"osgi.http.whiteboard.filter.pattern=/", "service.ranking:Integer=50", "osgi.http.whiteboard.filter.dispatcher=FORWARD",
        "osgi.http.whiteboard.filter.dispatcher=REQUEST"})
public final class WebSocketFilter
    extends OncePerRequestFilter
{
    private JettyController controller;

    private WebSocketServerFactory serverFactory;

    private final WebSocketEntryMap entries;

    public WebSocketFilter()
    {
        this.entries = new WebSocketEntryMap();
    }

    @Override
    public void init( final FilterConfig config )
        throws ServletException
    {
        super.init( config );

        final WebSocketPolicy policy = WebSocketPolicy.newServerPolicy();
        this.serverFactory = new WebSocketServerFactory( policy );
        this.serverFactory.init( this.controller.getServletContext() );

        new ServerContainerImpl( this.serverFactory );
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        if ( !this.serverFactory.isUpgradeRequest( req, res ) )
        {
            chain.doFilter( req, res );
            return;
        }

        final WebSocketEntry entry = this.entries.find( req );
        if ( entry == null )
        {
            chain.doFilter( req, res );
            return;
        }

        if ( !entry.handler.hasAccess( req ) )
        {
            res.sendError( HttpServletResponse.SC_FORBIDDEN );
            return;
        }

        final WebSocketCreator creator = getCreator( entry );
        this.serverFactory.acceptWebSocket( creator, req, res );
    }

    private WebSocketCreator getCreator( final WebSocketEntry entry )
    {
        if ( entry.creator != null )
        {
            return entry.creator;
        }

        entry.creator = newCreator( entry.handler );
        return entry.creator;
    }

    private WebSocketCreator newCreator( final WebSocketHandler handler )
    {
        final ServerEndpointConfig.Builder builder = newEndpointConfigBuilder();
        builder.configurator( newConfigurator( handler ) );
        builder.subprotocols( handler.getSubProtocols() );

        final ServerEndpointConfig config = builder.build();
        final SimpleServerEndpointMetadata meta = new SimpleServerEndpointMetadata( Endpoint.class, config );

        final WebSocketExtensionFactory ext = new WebSocketExtensionFactory( this.serverFactory );
        return new JsrCreator( this.serverFactory, meta, ext );
    }

    private ServerEndpointConfig.Configurator newConfigurator( final WebSocketHandler handler )
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

    @Reference
    public void setController( final JettyController controller )
    {
        this.controller = controller;
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addHandler( final WebSocketHandler handler )
    {
        this.entries.add( handler );
    }

    public void removeHandler( final WebSocketHandler handler )
    {
        this.entries.remove( handler );
    }
}
