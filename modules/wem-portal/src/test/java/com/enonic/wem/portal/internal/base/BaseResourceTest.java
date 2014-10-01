package com.enonic.wem.portal.internal.base;

import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import com.enonic.wem.portal.internal.PortalApplication;
import com.enonic.wem.portal.internal.PortalServlet;
import com.enonic.wem.portal.internal.exception.PortalExceptionMapper;

public abstract class BaseResourceTest
{
    private PortalServlet servlet;

    protected PortalApplication application;

    @Before
    public final void setup()
        throws Exception
    {
        this.application = new PortalApplication();
        this.application.addSingleton( new PortalExceptionMapper() );

        this.servlet = new PortalServlet();
        this.servlet.setApplication( this.application );

        configure();

        this.servlet.init( new MockServletConfig() );
    }

    protected abstract void configure()
        throws Exception;

    protected final MockHttpServletRequest newGetRequest( final String uri )
    {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod( "GET" );
        request.setRequestURI( uri );
        return request;
    }

    protected final MockHttpServletRequest newPostRequest( final String uri )
    {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod( "POST" );
        request.setRequestURI( uri );
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
