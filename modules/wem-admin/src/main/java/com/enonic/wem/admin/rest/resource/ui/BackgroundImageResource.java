package com.enonic.wem.admin.rest.resource.ui;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.resource.ResourceConstants;

@Path(ResourceConstants.REST_ROOT + "ui")
public final class BackgroundImageResource
    implements AdminResource
{
    @GET
    @Path("background.jpg")
    @Produces("image/jpeg")
    public Response streamBackgroundImage()
        throws Exception
    {
        final Response.ResponseBuilder responseBuilder =
            Response.ok( getClass().getResourceAsStream( "default-background.jpg" ), "image/jpg" );
        applyMaxAge( Integer.MAX_VALUE, responseBuilder );
        return responseBuilder.build();
    }

    private void applyMaxAge( int maxAge, final Response.ResponseBuilder responseBuilder )
    {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( maxAge );
        responseBuilder.cacheControl( cacheControl );
    }
}
