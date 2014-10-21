package com.enonic.wem.admin.rest.resource.ui;

import java.io.File;
import java.io.FileInputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.enonic.wem.core.home.HomeDir;

@Path("ui")
public final class BackgroundImageResource
{
    private static final int A_DAY_IN_SECONDS = 60 * 60 * 24;

    @GET
    @Path("background.jpg")
    @Produces("image/jpeg")
    public Response streamBackgroundImage()
        throws Exception
    {
        Response.ResponseBuilder responseBuilder;
        final File customBackgroundAsFile = new File( HomeDir.get().toFile(), "custom/background.jpg" );
        if ( customBackgroundAsFile.exists() )
        {
            responseBuilder = Response.ok( new FileInputStream( customBackgroundAsFile ), "image/jpg" );
            applyMaxAge( A_DAY_IN_SECONDS, responseBuilder );
        }
        else
        {
            responseBuilder = Response.ok( getClass().getResourceAsStream( "default-background.jpg" ), "image/jpg" );
            applyMaxAge( Integer.MAX_VALUE, responseBuilder );
        }
        return responseBuilder.build();
    }

    private void applyMaxAge( int maxAge, final Response.ResponseBuilder responseBuilder )
    {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( maxAge );
        responseBuilder.cacheControl( cacheControl );
    }
}
