package com.enonic.xp.jaxrs.impl;

import org.junit.Test;
import static org.junit.Assert.*;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import com.enonic.xp.web.jetty.impl.JettyTestSupport;

public class JaxRsServletTest
    extends JettyTestSupport
{
    private JaxRsServlet servlet;

    @Override
    protected void configure()
        throws Exception
    {
        this.servlet = new JaxRsServlet();
        addServlet( this.servlet, "/*" );
        this.servlet.addComponent( new TestErrorHandler() );
    }

    @Test
    public void testNoResources()
        throws Exception
    {
        assertNotFound();
    }

    private void assertNotFound()
        throws Exception
    {
        final Request request = newRequest( "/test" ).
            get().
            build();

        final Response response = callRequest( request );
        assertEquals( 404, response.code() );
    }

    private void assertFound()
        throws Exception
    {
        final Request request = newRequest( "/test" ).
            get().
            build();

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
        assertEquals( "text/plain", response.body().contentType().toString() );
        assertEquals( "Hello World", response.body().string() );
    }

    @Test
    public void testResource()
        throws Exception
    {
        final TestResource resource = new TestResource();

        this.servlet.addComponent( resource );
        assertFound();

        this.servlet.removeComponent( resource );
        assertNotFound();
    }
}
