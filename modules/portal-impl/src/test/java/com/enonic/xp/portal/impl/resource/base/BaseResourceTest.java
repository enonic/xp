package com.enonic.xp.portal.impl.resource.base;

import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.portal.impl.OldPortalHandler;
import com.enonic.xp.portal.impl.services.PortalServicesImpl;

public abstract class BaseResourceTest
{
    protected OldPortalHandler handler;

    protected PortalServicesImpl services;

    @Before
    public final void setup()
        throws Exception
    {
        this.handler = new OldPortalHandler();
        this.services = new PortalServicesImpl();
        this.handler.setServices( this.services );
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
        final MockHttpServletResponse response = new MockHttpServletResponse();
        this.handler.handle( request, response, null );
        return response;
    }
}
