package com.enonic.xp.web.impl;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.web.mock.MockHttpServletRequest;
import com.enonic.xp.web.mock.MockHttpServletResponse;
import com.enonic.xp.web.mock.MockServletConfig;

public class DispatcherServletTest
{
    private DispatcherServlet servlet;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    @Before
    public void setup()
        throws Exception
    {
        this.servlet = new DispatcherServlet();
        this.servlet.init( new MockServletConfig() );

        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();
    }

    @Test
    public void testNoHandlers()
        throws Exception
    {
        this.servlet.service( this.req, this.res );
    }

    @Test
    public void testAddHandler()
        throws Exception
    {
        final TestWebHandler handler = new TestWebHandler( 0, false );
        this.servlet.addHandler( handler );

        this.servlet.service( this.req, this.res );
        Assert.assertEquals( 1, handler.getInvocations() );
    }

    @Test
    public void testRemoveHandler()
        throws Exception
    {
        final TestWebHandler handler = new TestWebHandler( 0, false );
        this.servlet.addHandler( handler );
        this.servlet.removeHandler( handler );

        this.servlet.service( this.req, this.res );
        Assert.assertEquals( 0, handler.getInvocations() );
    }

    @Test
    public void testHandled()
        throws Exception
    {
        final TestWebHandler handler = new TestWebHandler( 0, false );
        this.servlet.addHandler( handler );

        this.servlet.service( this.req, this.res );
        Assert.assertEquals( 1, handler.getInvocations() );
        Assert.assertEquals( "handled", this.res.getContentAsString() );
    }

    @Test(expected = IOException.class)
    public void testThrowIOException()
        throws Exception
    {
        final ThrowTestWebHandler handler = new ThrowTestWebHandler( new IOException() );
        this.servlet.addHandler( handler );
        this.servlet.service( this.req, this.res );
    }

    @Test(expected = ServletException.class)
    public void testThrowServletException()
        throws Exception
    {
        final ThrowTestWebHandler handler = new ThrowTestWebHandler( new ServletException() );
        this.servlet.addHandler( handler );
        this.servlet.service( this.req, this.res );
    }

    @Test(expected = ServletException.class)
    public void testThrowAnyException()
        throws Exception
    {
        final ThrowTestWebHandler handler = new ThrowTestWebHandler( new IllegalArgumentException() );
        this.servlet.addHandler( handler );
        this.servlet.service( this.req, this.res );
    }
}
