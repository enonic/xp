package com.enonic.wem.portal.internal.base;

import java.util.Set;

import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
// import org.springframework.mock.web.MockServletConfig;

import com.google.common.collect.Sets;

import com.enonic.wem.portal.internal.PortalServlet;
import com.enonic.wem.portal.internal.exception.PortalExceptionMapper;
import com.enonic.wem.servlet.mock.MockServletConfig;

public abstract class BaseResourceTest
{
    private PortalServlet servlet;

    protected Set<Object> resources;

    @Before
    public final void setup()
        throws Exception
    {
        this.resources = Sets.newHashSet();
        this.resources.add( new PortalExceptionMapper() );

        this.servlet = new PortalServlet();
        this.servlet.setContributor( () -> resources );

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
