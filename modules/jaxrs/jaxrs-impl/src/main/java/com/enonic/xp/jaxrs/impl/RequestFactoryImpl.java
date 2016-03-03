package com.enonic.xp.jaxrs.impl;

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

import com.google.common.base.Strings;

import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

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
        final ResteasyUriInfo resteasyUriInfo = extractUriInfo( request );
        return new HttpServletInputMessage( request, response, this.context, theResponse, headers, resteasyUriInfo,
                                            httpMethod.toUpperCase(), this.dispatcher );
    }

    private static ResteasyUriInfo extractUriInfo( HttpServletRequest request )
    {
        final String requestURI = request.getRequestURI();
        final String absoluteUri = ServletRequestUrlHelper.getServerUrl() + ( Strings.isNullOrEmpty( requestURI ) ? "/" : requestURI );
        return new ResteasyUriInfo( absoluteUri, request.getQueryString(), request.getContextPath() );
    }

    public void setDispatcher( final SynchronousDispatcher dispatcher )
    {
        this.dispatcher = dispatcher;
    }
}
