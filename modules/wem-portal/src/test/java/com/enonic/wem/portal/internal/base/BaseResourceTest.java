package com.enonic.wem.portal.internal.base;

import java.util.List;

import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.collect.Lists;

import com.enonic.wem.portal.internal.MainResource;
import com.enonic.wem.servlet.internal.JaxRsServlet;
import com.enonic.wem.servlet.mock.MockServletConfig;

// import org.springframework.mock.web.MockServletConfig;

public abstract class BaseResourceTest
{
    private JaxRsServlet servlet;

    protected List<ResourceFactory> factories;

    @Before
    public final void setup()
        throws Exception
    {
        this.factories = Lists.newArrayList();

        this.servlet = new JaxRsServlet();
        configure();

        final MainResource mainResource = new MainResource();
        mainResource.setFactories( this.factories );
        this.servlet.addComponent( mainResource );

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
