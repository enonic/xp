package com.enonic.wem.admin.rest.resource.content;

import java.awt.image.BufferedImage;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.binary.Binary;

import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleMax;


@Path("attachment/{contentId}")
@Produces("*/*")
public class ContentAttachmentResource
{
    private Client client;

    private final ContentImageHelper helper;

    public ContentAttachmentResource()
    {
        this.helper = new ContentImageHelper();
    }

    @GET
    @Path("{attachment}")
    public Response getContentImage( @PathParam("contentId") final String contentId, @PathParam("attachment") final String attachmentName,
                                     @QueryParam("size") @DefaultValue("0") final int size )
        throws Exception
    {
        if ( StringUtils.isBlank( contentId ) || StringUtils.isBlank( attachmentName ) )
        {
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        final ContentId contentIdValue = ContentId.from( contentId );
        final Content content = findContent( contentIdValue );
        if ( content == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        final Attachment attachment = findAttachment( contentIdValue, attachmentName );
        if ( attachment == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        final Binary binary = attachment.getBinary();
        final String mimeType = attachment.getMimeType();
        if ( size <= 0 )
        {
            return Response.ok( binary.asInputStream(), mimeType ).build();
        }
        else
        {
            final BufferedImage scaledImage = helper.getImageFromBinary( binary, size, ScaleMax );
            return Response.ok( scaledImage, mimeType ).build();
        }
    }

    private Content findContent( final ContentId contentId )
    {
        return client.execute( Commands.content().get().selectors( ContentIds.from( contentId ) ) ).first();
    }

    private Attachment findAttachment( final ContentId contentId, final String attachmentName )
    {
        return client.execute( Commands.attachment().get().contentSelector( contentId ).attachmentName( attachmentName ) );
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
