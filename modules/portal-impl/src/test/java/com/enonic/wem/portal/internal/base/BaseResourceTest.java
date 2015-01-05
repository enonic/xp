package com.enonic.wem.portal.internal.base;

import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.wem.servlet.internal.dispatch.WebContextImpl;
import com.enonic.wem.servlet.internal.jaxrs.JaxRsHandler;

public abstract class BaseResourceTest
{
    protected JaxRsHandler servlet;

    @Before
    public final void setup()
        throws Exception
    {
        this.servlet = new JaxRsHandler();
        configure();
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
        final WebContextImpl context = new WebContextImpl();
        context.setRequest( request );

        final MockHttpServletResponse response = new MockHttpServletResponse();
        context.setResponse( response );
        this.servlet.handle( context );
        return response;
    }
}
