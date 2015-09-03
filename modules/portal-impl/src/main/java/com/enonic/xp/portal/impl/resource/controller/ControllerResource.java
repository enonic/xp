package com.enonic.xp.portal.impl.resource.controller;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
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

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.resource.base.BaseResource;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

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

    @HEAD
    public Response handleHead()
        throws Exception
    {
        final Response response = handleGet();
        return Response.fromResponse( response ).entity( null ).build();
    }

    private Response doHandle()
        throws Exception
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( this.mode );
        portalRequest.setScheme( ServletRequestUrlHelper.getScheme() );
        portalRequest.setHost( ServletRequestUrlHelper.getHost() );
        portalRequest.setPort( ServletRequestUrlHelper.getPort() );
        portalRequest.setPath( ServletRequestUrlHelper.getPath() );
        portalRequest.setUrl( ServletRequestUrlHelper.getFullUrl() );
        portalRequest.setMethod( this.request.getMethod() );
        portalRequest.setBaseUri( this.baseUri );
        portalRequest.setBranch( this.branch );
        portalRequest.getCookies().putAll( getCookieMap() );

        final Map<String, String> contextHeaders = portalRequest.getHeaders();
        for ( final String key : this.httpHeaders.getRequestHeaders().keySet() )
        {
            contextHeaders.put( key, this.httpHeaders.getHeaderString( key ) );
        }

        setParams( portalRequest.getParams(), this.uriInfo.getQueryParameters() );

        if ( this.form != null )
        {
            setParams( portalRequest.getFormParams(), this.form.asMap() );
        }

        configure( portalRequest );

        final PortalResponse response = execute( portalRequest );
        return toResponse( response );
    }

    private void setParams( final Multimap<String, String> to, final MultivaluedMap<String, String> from )
    {
        for ( final Map.Entry<String, List<String>> entry : from.entrySet() )
        {
            to.putAll( entry.getKey(), entry.getValue() );
        }
    }

    protected abstract void configure( PortalRequest portalRequest );

    protected abstract PortalResponse execute( PortalRequest portalRequest )
        throws Exception;

    private Response toResponse( final PortalResponse result )
    {
        final Response.ResponseBuilder builder = Response.status( result.getStatus() );
        builder.type( result.getContentType() );

        for ( final Map.Entry<String, String> header : result.getHeaders().entrySet() )
        {
            builder.header( header.getKey(), header.getValue() );
        }

        if ( result.getBody() instanceof byte[] )
        {
            builder.entity( result.getBody() );
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
