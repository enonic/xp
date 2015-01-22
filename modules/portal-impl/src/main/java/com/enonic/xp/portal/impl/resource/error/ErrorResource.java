package com.enonic.xp.portal.impl.resource.error;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.xp.portal.impl.resource.base.BaseSubResource;

public final class ErrorResource
    extends BaseSubResource
{
    @GET
    @Path("{code}")
    public Response handle( @PathParam("code") final int code, @DefaultValue("") @QueryParam("message") final String message )
    {
        return Response.status( code ).entity( message ).type( MediaType.TEXT_PLAIN ).build();
    }
}
