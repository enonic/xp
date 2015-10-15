package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.eclipse.jetty.websocket.common.extensions.WebSocketExtensionFactory;
import org.eclipse.jetty.websocket.jsr356.server.ContainerDefaultConfigurator;
import org.eclipse.jetty.websocket.jsr356.server.JsrCreator;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.SimpleServerEndpointMetadata;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import com.google.common.collect.Lists;

import com.enonic.xp.web.websocket.EndpointProvider;
import com.enonic.xp.web.websocket.WebSocketHandler;

final class WebSocketHandlerImpl
    extends ServerContainer
    implements WebSocketHandler
{
    private final WebSocketServerFactory serverFactory;

    private WebSocketCreator jsrCreator;

    private EndpointProvider<?> endpointProvider;

    private final List<Class<? extends Decoder>> decoders;

    private final List<Class<? extends Encoder>> encoders;

    private final List<String> subProtocols;

    public WebSocketHandlerImpl( final WebSocketServerFactory serverFactory )
    {
        super( null, serverFactory, null );
        this.serverFactory = serverFactory;
        this.decoders = Lists.newArrayList();
        this.encoders = Lists.newArrayList();
        this.subProtocols = Lists.newArrayList();
    }

    @Override
    public void addEndpoint( final Class<?> clz )
        throws DeploymentException
    {
        throw new DeploymentException( "Not implemented" );
    }

    @Override
    public void addEndpoint( final ServerEndpointConfig config )
        throws DeploymentException
    {
        throw new DeploymentException( "Not implemented" );
    }

    @Override
    public Session connectToServer( final Object instance, final URI path )
        throws DeploymentException, IOException
    {
        throw new DeploymentException( "Not implemented" );
    }

    @Override
    public Session connectToServer( final Class<?> clz, final URI path )
        throws DeploymentException, IOException
    {
        throw new DeploymentException( "Not implemented" );
    }

    @Override
    public Session connectToServer( final Endpoint instance, final ClientEndpointConfig config, final URI path )
        throws DeploymentException, IOException
    {
        throw new DeploymentException( "Not implemented" );
    }

    @Override
    public Session connectToServer( final Class<? extends Endpoint> clz, final ClientEndpointConfig config, final URI path )
        throws DeploymentException, IOException
    {
        throw new DeploymentException( "Not implemented" );
    }

    @Override
    public Set<Extension> getInstalledExtensions()
    {
        return Collections.emptySet();
    }

    public void init( final ServletContext context )
        throws ServletException
    {
        this.serverFactory.init( context );
    }

    @Override
    public boolean isUpgradeRequest( final HttpServletRequest req, final HttpServletResponse res )
    {
        return this.serverFactory.isUpgradeRequest( req, res );
    }

    @Override
    public boolean acceptWebSocket( final HttpServletRequest req, final HttpServletResponse res )
        throws IOException
    {
        if ( this.jsrCreator == null )
        {
            throw new IllegalStateException( "EndpointProvider is not set" );
        }

        return this.serverFactory.acceptWebSocket( this.jsrCreator, req, res );
    }

    @Override
    public void setEndpointProvider( final EndpointProvider<?> provider )
    {
        this.endpointProvider = provider;
        this.jsrCreator = newCreator();
    }

    private WebSocketCreator newCreator()
    {
        final ServerEndpointConfig.Builder builder = newEndpointConfigBuilder();
        builder.configurator( newConfigurator() );
        builder.subprotocols( this.subProtocols );
        builder.decoders( this.decoders );
        builder.encoders( this.encoders );

        final ServerEndpointConfig config = builder.build();
        final SimpleServerEndpointMetadata meta = new SimpleServerEndpointMetadata( Endpoint.class, config );

        final WebSocketExtensionFactory ext = new WebSocketExtensionFactory( this );
        return new JsrCreator( this, meta, ext );
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
                return endpointClass.cast( endpointProvider.get() );
            }
        };
    }

    private ServerEndpointConfig.Builder newEndpointConfigBuilder()
    {
        return ServerEndpointConfig.Builder.create( Endpoint.class, "/" );
    }

    @Override
    public void destroy()
    {
        this.serverFactory.cleanup();
        super.destroy();
    }

    @Override
    public void addDecoder( final Class<Decoder> decoder )
    {
        this.decoders.add( decoder );
    }

    @Override
    public void addEncoder( final Class<Encoder> encoder )
    {
        this.encoders.add( encoder );
    }

    @Override
    public void addSubProtocol( final String protocol )
    {
        this.subProtocols.add( protocol );
    }
}
