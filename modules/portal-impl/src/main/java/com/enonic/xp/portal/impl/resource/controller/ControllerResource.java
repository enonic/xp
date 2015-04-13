package com.enonic.xp.portal.impl.resource.controller;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.impl.resource.base.BaseResource;
import com.enonic.xp.portal.rendering.RenderResult;

public abstract class ControllerResource
    extends BaseResource
{
    @Context
    protected Request request;

    @Context
    protected UriInfo uriInfo;

    @Context
    protected HttpHeaders httpHeaders;

    protected Form form;

    @GET
    public Response handleGet()
        throws Exception
    {
        return doHandle();
    }

    @POST
    public Response handlePost( final Form form )
        throws Exception
    {
        this.form = form;
        return doHandle();
    }

    private Response doHandle()
        throws Exception
    {
        final PortalContext context = new PortalContext();
        context.setMode( this.mode );
        context.setMethod( this.request.getMethod() );
        context.setBaseUri( this.baseUri );
        context.setBranch( this.branch );
        context.getCookies().putAll( getCookieMap() );

        final Multimap<String, String> contextHeaders = context.getHeaders();
        this.httpHeaders.getRequestHeaders().forEach( contextHeaders::putAll );
        setParams( context.getParams(), this.uriInfo.getQueryParameters() );

        if ( this.form != null )
        {
            setParams( context.getFormParams(), this.form.asMap() );
        }

        configure( context );

        final RenderResult result = execute( context );
        return toResponse( result );
    }

    private void setParams( final Multimap<String, String> to, final MultivaluedMap<String, String> from )
    {
        for ( final Map.Entry<String, List<String>> entry : from.entrySet() )
        {
            to.putAll( entry.getKey(), entry.getValue() );
        }
    }

    protected abstract void configure( PortalContext context );

    protected abstract RenderResult execute( PortalContext context )
        throws Exception;

    private Response toResponse( final RenderResult result )
    {
        final Response.ResponseBuilder builder = Response.status( result.getStatus() );
        builder.type( result.getType() );

        for ( final Map.Entry<String, String> header : result.getHeaders().entrySet() )
        {
            builder.header( header.getKey(), header.getValue() );
        }

        if ( result.getEntity() instanceof byte[] )
        {
            builder.entity( result.getEntity() );
        }
        else
        {
            builder.entity( result.getAsString() );
        }

        return builder.build();
    }

    private Map<String, String> getCookieMap()
    {
        final Map<String, String> result = Maps.newHashMap();
        for ( final Cookie cookie : this.httpHeaders.getCookies().values() )
        {
            result.put( cookie.getName(), cookie.getValue() );
        }

        return result;
    }
}
