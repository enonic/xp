package com.enonic.xp.jaxrs.impl;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpRequestFactory;
import org.jboss.resteasy.plugins.server.servlet.Servlet3AsyncHttpRequest;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final class RequestFactoryImpl
    implements HttpRequestFactory
{
    private final ServletContext context;

    private SynchronousDispatcher dispatcher;

    RequestFactoryImpl( final ServletContext context )
    {
        this.context = context;
    }

    @Override
    public HttpRequest createResteasyHttpRequest( final String httpMethod, final HttpServletRequest request,
                                                  final ResteasyHttpHeaders headers, final ResteasyUriInfo uriInfo,
                                                  final HttpResponse theResponse, final HttpServletResponse response )
    {
        return new Servlet3AsyncHttpRequest( request, response, this.context, theResponse, headers, uriInfo,
                                             httpMethod.toUpperCase(), this.dispatcher );
    }

    public void setDispatcher( final SynchronousDispatcher dispatcher )
    {
        this.dispatcher = dispatcher;
    }
}
