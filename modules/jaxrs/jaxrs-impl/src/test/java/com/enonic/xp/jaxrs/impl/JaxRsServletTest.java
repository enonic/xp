package com.enonic.xp.jaxrs.impl;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import com.enonic.xp.web.filter.BaseWebFilter;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.jupiter.api.Assertions.*;

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

        addFilter( new BaseWebFilter()
        {
            @Override
            protected void doFilter( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
                throws Exception
            {
                ServletRequestHolder.setRequest( req );
                chain.doFilter( req, res );
            }
        }, "/*" );
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
