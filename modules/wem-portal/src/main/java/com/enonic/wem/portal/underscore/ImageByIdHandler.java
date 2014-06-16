package com.enonic.wem.portal.underscore;

import java.awt.image.BufferedImage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.InjectParam;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;

@Path("{mode}/{content:.+}/_/image/id/{id:.+}")
public final class ImageByIdHandler
    extends ImageBaseHandler
{
    public final static class Params
    {
        @PathParam("mode")
        public String mode;

        @PathParam("content")
        public String content;

        @PathParam("id")
        public String id;

        @QueryParam("filter")
        public String filter;

        @QueryParam("background")
        public String background;

        @QueryParam("quality")
        public String quality;
    }

    @GET
    public Response handle( @InjectParam final Params params )
        throws Exception
    {
        final ContentId imageContentId = ContentId.from( params.id );
        final Content imageContent = getContent( imageContentId );

        final Attachment attachment = getAttachment( imageContent.getId() );
        if ( attachment == null )
        {
            throw notFound();
        }

        final Blob blob = getBlob( attachment.getBlobKey() );
        if ( blob == null )
        {
            throw notFound();
        }

        final BufferedImage contentImage = toBufferedImage( blob.getStream() );

        final String format = getFormat( attachment.getName() );
        final int backgroundColor = parseBackgroundColor( params.background );
        final BufferedImage image = applyFilters( contentImage, format, params.filter, backgroundColor );

        final int quality = parseQuality( params.quality );
        final byte[] imageData = serializeImage( image, format, quality );

        return Response.ok( imageData, attachment.getMimeType() ).build();
    }
}
