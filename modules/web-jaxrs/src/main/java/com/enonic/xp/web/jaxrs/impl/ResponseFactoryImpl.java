package com.enonic.xp.web.jaxrs.impl;

import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpResponseFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.spi.HttpResponse;

final class ResponseFactoryImpl
    implements HttpResponseFactory
{
    private SynchronousDispatcher dispatcher;

    @Override
    public HttpResponse createResteasyHttpResponse( final HttpServletResponse response )
    {
        return new HttpServletResponseWrapper( response, this.dispatcher.getProviderFactory() );
    }

    public void setDispatcher( final SynchronousDispatcher dispatcher )
    {
        this.dispatcher = dispatcher;
    }
}
