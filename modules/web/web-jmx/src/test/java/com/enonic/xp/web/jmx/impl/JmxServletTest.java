package com.enonic.xp.web.jmx.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

public class JmxServletTest
{
    private JmxServlet servlet;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Before
    public void setup()
        throws Exception
    {
        final MockServletConfig config = new MockServletConfig();

        this.servlet = new JmxServlet();
        this.servlet.init( config );

        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
    }

    @After
    public void destroy()
    {
        this.servlet.destroy();
    }

    @Test
    public void testRead()
        throws Exception
    {
        this.request.setMethod( "GET" );
        this.request.setPathInfo( "/read/java.lang:type=Memory" );

        this.servlet.service( this.request, this.response );

        Assert.assertEquals( 200, this.response.getStatus() );
    }
}
