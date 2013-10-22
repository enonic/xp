package com.enonic.wem.admin.rest.resource.space;

import java.awt.image.BufferedImage;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;

import static com.enonic.wem.api.command.Commands.space;


@Path("space/image")
@Produces("image/*")
public final class SpaceImageResource
{
    private final SpaceImageHelper helper;

    private Client client;

    public SpaceImageResource()
        throws Exception
    {
        this.helper = new SpaceImageHelper();
    }

    @GET
    @Path("{spaceName}")
    public Response getSpaceIcon( @PathParam("spaceName") final String spaceName, @QueryParam("size") @DefaultValue("128") final int size )
        throws Exception
    {
        final String mimeType;
        final BufferedImage schemaImage;
        final Icon spaceIcon = findSpaceIcon( SpaceName.from( spaceName ) );
        if ( spaceIcon == null )
        {
            schemaImage = helper.getDefaultSpaceImage( size );
            mimeType = "image/png";
        }
        else
        {
            schemaImage = helper.getIconImage( spaceIcon, size );
            mimeType = spaceIcon.getMimeType();
        }

        return Response.ok( schemaImage, mimeType ).build();
    }

    private Icon findSpaceIcon( final SpaceName spaceName )
    {
        final Space space = client.execute( space().get().name( spaceName ) ).first();
        return space == null ? null : space.getIcon();
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
