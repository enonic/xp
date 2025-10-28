package com.enonic.xp.jaxrs.impl;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.filter.BaseWebFilter;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JaxRsServletTest
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
                chain.doFilter( req, res );
            }
        }, "/*" );
    }

    @Test
    void testNoResources()
        throws Exception
    {
        assertNotFound();
    }

    private void assertNotFound()
        throws Exception
    {
        final HttpRequest request = newRequest( "/test" ).
            GET().
            build();

        final HttpResponse response = callRequest( request );
        assertEquals( 404, response.statusCode() );
    }

    private void assertFound()
        throws Exception
    {
        final HttpRequest request = newRequest( "/test" ).
            GET().
            build();

        final HttpResponse response = callRequest( request );
        assertEquals( 200, response.statusCode() );
        assertEquals( List.of( "text/plain;charset=utf-8" ), response.headers().allValues( "content-type" ) );
        assertEquals( "Hello World", response.body().toString() );
    }

    @Test
    void testResource()
        throws Exception
    {
        final TestResource resource = new TestResource();

        this.servlet.addComponent( resource );
        assertFound();

        this.servlet.removeComponent( resource );
        assertNotFound();
    }
}
