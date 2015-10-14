package com.enonic.xp.web.websocket;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

public class WebSocketServletTest
{
    private MockServletConfig config;

    private WebSocketServlet servlet;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    private WebSocketHandler handler;

    private WebSocketHandlerFactory factory;

    @Before
    public void setup()
    {
        this.config = new MockServletConfig();
        this.servlet = new WebSocketServlet()
        {
            @Override
            protected void configure( final WebSocketHandler handler )
                throws Exception
            {
            }
        };

        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();

        this.handler = Mockito.mock( WebSocketHandler.class );
        this.factory = Mockito.mock( WebSocketHandlerFactory.class );

        Mockito.when( this.factory.create() ).thenReturn( this.handler );
    }

    private void setupFactory()
    {
        this.config.getServletContext().setAttribute( WebSocketHandlerFactory.class.getName(), this.factory );
    }

    @Test(expected = ServletException.class)
    public void noFactorySet()
        throws Exception
    {
        this.servlet.init( this.config );
    }

    @Test
    public void notUpgradeRequest()
        throws Exception
    {
        setupFactory();

        Mockito.when( this.handler.isUpgradeRequest( this.req, this.res ) ).thenReturn( false );

        this.servlet.init( this.config );
        this.servlet.service( this.req, this.res );
        this.servlet.destroy();

        Mockito.verify( this.handler, Mockito.times( 0 ) ).acceptWebSocket( this.req, this.res );
    }

    @Test
    public void upgradeRequest_notAccepted()
        throws Exception
    {
        setupFactory();

        Mockito.when( this.handler.isUpgradeRequest( this.req, this.res ) ).thenReturn( true );
        Mockito.when( this.handler.acceptWebSocket( this.req, this.res ) ).thenReturn( false );

        this.servlet.init( this.config );
        this.servlet.service( this.req, this.res );
        this.servlet.destroy();

        Mockito.verify( this.handler, Mockito.times( 1 ) ).acceptWebSocket( this.req, this.res );
    }

    @Test
    public void upgradeRequest_notAccepted_committed()
        throws Exception
    {
        setupFactory();

        Mockito.when( this.handler.isUpgradeRequest( this.req, this.res ) ).thenReturn( true );
        Mockito.when( this.handler.acceptWebSocket( this.req, this.res ) ).thenReturn( false );

        this.res.setCommitted( true );

        this.servlet.init( this.config );
        this.servlet.service( this.req, this.res );
        this.servlet.destroy();

        Mockito.verify( this.handler, Mockito.times( 1 ) ).acceptWebSocket( this.req, this.res );
    }

    @Test
    public void upgradeRequest()
        throws Exception
    {
        setupFactory();

        Mockito.when( this.handler.isUpgradeRequest( this.req, this.res ) ).thenReturn( true );

        this.servlet.init( this.config );
        this.servlet.service( this.req, this.res );
        this.servlet.destroy();

        Mockito.verify( this.handler, Mockito.times( 1 ) ).acceptWebSocket( this.req, this.res );
    }
}
