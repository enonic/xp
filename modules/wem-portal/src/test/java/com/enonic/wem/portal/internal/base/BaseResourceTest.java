package com.enonic.wem.portal.internal.base;

import java.util.List;

import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.collect.Lists;

import com.enonic.wem.portal.internal.PortalServlet;
import com.enonic.wem.portal.internal.exception.PortalExceptionMapper;
import com.enonic.wem.servlet.mock.MockServletConfig;

// import org.springframework.mock.web.MockServletConfig;

public abstract class BaseResourceTest
{
    private PortalServlet servlet;

    protected List<Object> resources;

    @Before
    public final void setup()
        throws Exception
    {
        this.resources = Lists.newArrayList();
        this.resources.add( new PortalExceptionMapper() );

        this.servlet = new PortalServlet();
        this.servlet.init( new MockServletConfig() );

        configure();

        this.servlet.setResources( this.resources );
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
