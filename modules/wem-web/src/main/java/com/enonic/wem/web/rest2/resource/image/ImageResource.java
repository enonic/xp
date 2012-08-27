package com.enonic.wem.web.rest2.resource.image;

import java.awt.image.BufferedImage;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.stereotype.Component;

@Path("misc/image")
@Component
public final class ImageResource
{
    private final ImageHelper helper;

    public ImageResource()
        throws Exception
    {
        this.helper = new ImageHelper();
    }

    @GET
    @Path("{name}")
    @Produces("image/png")
    public BufferedImage getImageAsPng( @PathParam("name") final String name, @QueryParam("size") @DefaultValue("100") final int size )
        throws Exception
    {
        return this.helper.getImage( name, size );
    }
}
