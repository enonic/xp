package com.enonic.wem.portal.underscore;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.InjectParam;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.core.image.ImageHelper;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;

@Path("{mode}/{path:.+}/_/image/{fileName:.+}")
public final class ImageResource
    extends AbstractImageResource
{
    public final class Request
        implements Params
    {
        @PathParam("mode")
        protected String mode;

        @PathParam("path")
        protected String contentPath;

        @PathParam("fileName")
        protected String fileName;

        @QueryParam("filter")
        protected String filter;

        @QueryParam("background")
        protected String backgroundColor;

        @QueryParam("quality")
        protected String quality;

        @Override
        public String getFilterParam()
        {
            return this.filter;
        }

        @Override
        public String getQualityParam()
        {
            return this.quality;
        }

        @Override
        public String getBackgroundColorParam()
        {
            return this.backgroundColor;
        }
    }

    @Inject
    protected ImageFilterBuilder imageFilterBuilder;

    @GET
    public Response getResource( @InjectParam final Request request )
        throws IOException
    {
        final ContentPath path = ContentPath.from( request.contentPath );
        final Content content = getContent( path );

        final Attachment attachment = getAttachment( content.getId(), request.fileName );

        final Blob blob = getBlob( attachment.getBlobKey() );
        if ( blob == null )
        {
            throw new RuntimeException( "Blob not found: " + attachment.getBlobKey() );
        }

        final BufferedImage contentImage = toBufferedImage( blob.getStream() );

        final String format = getFormat( request.fileName );

        final BufferedImage image = applyFilters( request, contentImage, format );

        byte[] imageData = ImageHelper.writeImage( image, format, resolveQuality( request ) );

        return Response.ok( imageData, attachment.getMimeType() ).build();
    }
}
