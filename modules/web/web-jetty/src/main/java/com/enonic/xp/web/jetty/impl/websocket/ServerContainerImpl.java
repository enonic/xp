package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpointConfig;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.server.NativeWebSocketConfiguration;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;

final class ServerContainerImpl
    extends ServerContainer
{
    public ServerContainerImpl( final WebSocketServerFactory serverFactory )
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