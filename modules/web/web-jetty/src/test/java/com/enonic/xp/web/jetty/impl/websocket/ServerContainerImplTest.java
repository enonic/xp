package com.enonic.xp.web.jetty.impl.websocket;

import java.net.URI;

import javax.servlet.ServletContext;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerEndpointConfig;

import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class ServerContainerImplTest
{
    private ServerContainerImpl container;

    @BeforeEach
    public void setup()
    {
        final ServletContext context = Mockito.mock( ServletContext.class );

        this.container = new ServerContainerImpl( new WebSocketServerFactory( context ) );
    }

    @Test
    public void testConnectToServer1()
        throws Exception
    {
        assertThrows(DeploymentException.class, () -> this.container.connectToServer( getClass(), new URI( "/" ) ));
    }

    @Test
    public void testConnectToServer2()
        throws Exception
    {
        assertThrows(DeploymentException.class, () -> this.container.connectToServer( new Object(), new URI( "/" ) ));
    }

    @Test
    public void testConnectToServer3()
        throws Exception
    {
        assertThrows(DeploymentException.class, () -> this.container.connectToServer( Mockito.mock( Endpoint.class ), Mockito.mock( ClientEndpointConfig.class ), new URI( "/" ) ));
    }

    @Test
    public void testConnectToServer4()
        throws Exception
    {
        assertThrows(DeploymentException.class, () -> this.container.connectToServer( Endpoint.class, Mockito.mock( ClientEndpointConfig.class ), new URI( "/" ) ));
    }

    @Test
    public void testAddEndpoint1()
        throws Exception
    {
        assertThrows(DeploymentException.class, () -> this.container.addEndpoint( Endpoint.class ));
    }

    @Test
    public void testAddEndpoint2()
        throws Exception
    {
        assertThrows(DeploymentException.class, () -> this.container.addEndpoint( Object.class ));
    }

    @Test
    public void testAddEndpoint3()
        throws Exception
    {
        assertThrows(DeploymentException.class, () -> this.container.addEndpoint( Mockito.mock( ServerEndpointConfig.class ) ));
    }

    @Test
    public void testInstalledExtensions()
    {
        assertNotNull( this.container.getInstalledExtensions() );
        assertEquals( 0, this.container.getInstalledExtensions().size() );
    }
}
