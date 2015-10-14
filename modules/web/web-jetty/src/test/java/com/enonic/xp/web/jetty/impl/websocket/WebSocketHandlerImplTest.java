package com.enonic.xp.web.jetty.impl.websocket;

import java.net.URI;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerEndpointConfig;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.web.websocket.WebSocketHandler;

import static org.junit.Assert.*;

public class WebSocketHandlerImplTest
{
    private WebSocketHandler handler;

    @Before
    public void setup()
    {
        this.handler = new WebSocketHandlerFactoryImpl().create();
    }

    @Test(expected = DeploymentException.class)
    public void testConnectToServer1()
        throws Exception
    {
        this.handler.connectToServer( getClass(), new URI( "/" ) );
    }

    @Test(expected = DeploymentException.class)
    public void testConnectToServer2()
        throws Exception
    {
        this.handler.connectToServer( new Object(), new URI( "/" ) );
    }

    @Test(expected = DeploymentException.class)
    public void testConnectToServer3()
        throws Exception
    {
        this.handler.connectToServer( Mockito.mock( Endpoint.class ), Mockito.mock( ClientEndpointConfig.class ), new URI( "/" ) );
    }

    @Test(expected = DeploymentException.class)
    public void testConnectToServer4()
        throws Exception
    {
        this.handler.connectToServer( Endpoint.class, Mockito.mock( ClientEndpointConfig.class ), new URI( "/" ) );
    }

    @Test(expected = DeploymentException.class)
    public void testAddEndpoint1()
        throws Exception
    {
        ( (WebSocketHandlerImpl) this.handler ).addEndpoint( Endpoint.class );
    }

    @Test(expected = DeploymentException.class)
    public void testAddEndpoint2()
        throws Exception
    {
        ( (WebSocketHandlerImpl) this.handler ).addEndpoint( Object.class );
    }

    @Test(expected = DeploymentException.class)
    public void testAddEndpoint3()
        throws Exception
    {
        ( (WebSocketHandlerImpl) this.handler ).addEndpoint( Mockito.mock( ServerEndpointConfig.class ) );
    }

    @Test
    public void testInstalledExtensions()
    {
        assertNotNull( this.handler.getInstalledExtensions() );
        assertEquals( 0, this.handler.getInstalledExtensions().size() );
    }

    @Test(expected = IllegalStateException.class)
    public void testAcceptWebSocketNoEndpoint()
        throws Exception
    {
        this.handler.acceptWebSocket( new MockHttpServletRequest(), new MockHttpServletResponse() );
    }

    @Test
    public void testAddEncoder()
    {
        this.handler.addEncoder( Encoder.class );
    }

    @Test
    public void testAddDecoder()
    {
        this.handler.addDecoder( Decoder.class );
    }

    @Test
    public void testAddSubProtocol()
    {
        this.handler.addSubProtocol( "X" );
    }
}
