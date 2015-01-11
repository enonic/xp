package com.enonic.xp.portal.impl.resource.controller;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.controller.PortalRequestImpl;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.xp.portal.impl.resource.base.BaseResource;

public abstract class ControllerResource
    extends BaseResource
{
    @Context
    protected Request request;

    @Context
    protected UriInfo uriInfo;

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
        final PortalContextImpl context = new PortalContextImpl();

        final PortalRequestImpl jsRequest = new PortalRequestImpl();
        jsRequest.setMode( this.mode );
        jsRequest.setMethod( this.request.getMethod() );
        jsRequest.addParams( this.uriInfo.getQueryParameters() );

        if ( this.form != null )
        {
            jsRequest.addFormParams( this.form.asMap() );
        }

        context.setRequest( jsRequest );
        configure( context );

        /*
        final ControllerScript controllerScript = this.services.getControllerScriptFactory().newController( this.scriptDir );
        controllerScript.execute( context );

        final RenderResult result = new PortalResponseSerializer( context.getResponse() ).serialize();
        return toResponse( result );*/

        final RenderResult result = execute( context );
        return toResponse( result );
    }

    protected abstract void configure( PortalContextImpl context );

    protected abstract RenderResult execute( PortalContextImpl context )
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
}
