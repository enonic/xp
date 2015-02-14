package com.enonic.xp.web.jaxrs.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpRequestFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletInputMessage;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyUriInfo;

final class RequestFactoryImpl
    implements HttpRequestFactory
{
    private final ServletContext context;

    private SynchronousDispatcher dispatcher;

    public RequestFactoryImpl( final ServletContext context )
    {
        this.context = context;
    }

    @Override
    public HttpRequest createResteasyHttpRequest( final String httpMethod, final HttpServletRequest request,
                                                  final ResteasyHttpHeaders headers, final ResteasyUriInfo uriInfo,
                                                  final HttpResponse theResponse, final HttpServletResponse response )
    {
        return new HttpServletInputMessage( request, response, this.context, theResponse, headers, uriInfo, httpMethod.toUpperCase(),
                                            this.dispatcher );
    }

    public void setDispatcher( final SynchronousDispatcher dispatcher )
    {
        this.dispatcher = dispatcher;
    }
}
