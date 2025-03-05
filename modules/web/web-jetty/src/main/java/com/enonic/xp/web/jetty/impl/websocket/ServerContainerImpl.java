package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.ee10.websocket.jakarta.server.JakartaWebSocketServerContainer;
import org.eclipse.jetty.websocket.server.NativeWebSocketConfiguration;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;

import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.Extension;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpointConfig;

final class ServerContainerImpl
    extends JakartaWebSocketServerContainer
{
    ServerContainerImpl( final WebSocketServerFactory serverFactory )
    {
        super( new NativeWebSocketConfiguration( serverFactory ), (HttpClient) null );
    }

    @Override
    protected void doStart()
        throws Exception
    {
        super.doStart();
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
}
