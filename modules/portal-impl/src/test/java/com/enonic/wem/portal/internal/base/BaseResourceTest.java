package com.enonic.wem.portal.internal.base;

import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.wem.servlet.internal.JaxRsServlet;
import com.enonic.wem.servlet.mock.MockServletConfig;

public abstract class BaseResourceTest
{
    protected JaxRsServlet servlet;

    @Before
    public final void setup()
        throws Exception
    {
        this.servlet = new JaxRsServlet();
        configure();
        this.servlet.init( new MockServletConfig() );
    }

    protected abstract void configure()
        throws Exception;

    protected final MockHttpServletRequest newGetRequest( final String uri )
    {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod( "GET" );
        request.setRequestURI( "/portal" + uri );
        return request;
    }

    protected final MockHttpServletRequest newPostRequest( final String uri )
    {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod( "POST" );
        request.setRequestURI( "/portal" + uri );
        return request;
    }

    protected final MockHttpServletResponse executeRequest( final MockHttpServletRequest request )
        throws Exception
    {
        final MockHttpServletResponse response = new MockHttpServletResponse();
        this.servlet.service( request, response );
        return response;
    }
}
